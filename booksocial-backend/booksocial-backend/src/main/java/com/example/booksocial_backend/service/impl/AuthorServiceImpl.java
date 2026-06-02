package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.catalog.AuthorRequestDTO;
import com.example.booksocial_backend.DTO.catalog.AuthorResponseDTO;
import com.example.booksocial_backend.DTO.catalog.WorkResponseDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;
import com.example.booksocial_backend.domain.catalog.Author;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.social.AuthorFollow;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.exception.AuthorAlreadyExistsException;
import com.example.booksocial_backend.exception.AuthorNotFoundException;
import com.example.booksocial_backend.exception.UserNotFoundException;
import com.example.booksocial_backend.repository.AuthorFollowRepository;
import com.example.booksocial_backend.repository.AuthorRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.AuthorService;
import com.example.booksocial_backend.service.EmailService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de autores.
 *
 * Define las operaciones de negocio relacionadas con autores, seguimiento de
 * autores y consulta de seguidores. Coordina repositorios, validaciones de
 * dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

  private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar autores. */
  private final AuthorRepository authorRepository;
  /** Repositorio JPA utilizado para gestionar seguidores de autores. */
  private final AuthorFollowRepository authorFollowRepository;
  /**
   * Repositorio JPA utilizado para resolver usuarios en operaciones de
   * seguimiento.
   */
  private final UserRepository userRepository;
  /** Repositorio JPA utilizado para asociar autores con obras. */
  private final WorkRepository workRepository;
  /** Servicio utilizado para enviar confirmaciones de seguimiento por correo. */
  private final EmailService emailService;

  /**
   * Crea un nuevo autor aplicando las validaciones de negocio correspondientes.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del autor creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public AuthorResponseDTO createAuthor(AuthorRequestDTO request) {

    log.info("[AUTHOR] [CREATE] [START] name='{}'", request != null ? request.getName() : "null");
    validateAuthorRequest(request);

    String normalizedName = request.getName().trim();

    if (authorRepository.existsByName(normalizedName)) {
      log.warn("[AUTHOR] [CREATE] [CONFLICT] Ya existe autor con nombre='{}'", normalizedName);
      throw new AuthorAlreadyExistsException(normalizedName);
    }

    Author author = Author.builder()
        .name(normalizedName)
        .nationality(request.getNationality())
        .birthDate(request.getBirthDate())
        .img(request.getImg())
        .build();

    Author saved = authorRepository.save(author);
    log.info("[AUTHOR] [CREATE] [SUCCESS] id={} name='{}'", saved.getId(), saved.getName());
    Long savedId = saved.getId();

    if (savedId != null && request.getWorkIds() != null && !request.getWorkIds().isEmpty()) {
      // Mantiene la relacion bidireccional autor-obra cuando se crea el autor desde
      // la API.
      for (Long workId : request.getWorkIds()) {
        workRepository.findById(workId).ifPresent(w -> {
          if (w.getAuthors().stream().noneMatch(a -> savedId.equals(a.getId()))) {
            w.getAuthors().add(saved);
            workRepository.save(w);
          }
        });
      }
    }

    return mapAuthorToDTO(savedId != null
        ? authorRepository.findById(savedId).orElse(saved)
        : saved);
  }

  /**
   * Obtiene una obra por su identificador.
   *
   * @param id identificador de la obra sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public AuthorResponseDTO getAuthorById(Long id) {
    return mapAuthorToDTO(getAuthorEntityById(id));
  }

  /**
   * Obtiene todos las obras disponibles en la aplicación
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<AuthorResponseDTO> getAllAuthors() {
    return authorRepository.findAll()
        .stream()
        .map(this::mapAuthorToDTO)
        .toList();
  }

  /**
   * Actualiza una obra existente aplicando validaciones de negocio.
   *
   * @param id      identificador de la obra sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request) {

    log.info("[AUTHOR] [UPDATE] [START] id={}", id);
    Author existing = getAuthorEntityById(id);

    String normalizedName = request.getName().trim();

    if (!existing.getName().equalsIgnoreCase(normalizedName)
        && authorRepository.existsByName(normalizedName)) {
      throw new IllegalArgumentException("Ya existe otro autor con ese nombre");
    }

    existing.setName(normalizedName);
    existing.setNationality(request.getNationality());
    existing.setBirthDate(request.getBirthDate());
    if (request.getImg() != null)
      existing.setImg(request.getImg());

    Author saved = authorRepository.save(existing);

    if (request.getWorkIds() != null) {
      syncAuthorWorkAssociations(saved, request.getWorkIds());
    }

    Long savedId = saved.getId();
    return mapAuthorToDTO(savedId != null
        ? authorRepository.findById(savedId).orElse(saved)
        : saved);
  }

  /**
   * Elimina un autor dentro de la aplicación
   *
   * @param id identificador del autor sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteAuthor(Long id) {
    log.info("[AUTHOR] [DELETE] [START] id={}", id);
    Author author = getAuthorEntityById(id);

    List<Work> worksWithAuthor = workRepository.findByAuthorId(id);
    worksWithAuthor.forEach(w -> {
      w.getAuthors().removeIf(a -> id.equals(a.getId()));
      workRepository.save(w);
    });
    log.info("[AUTHOR] [DELETE] Desasociadas {} obra(s) del autor id={}", worksWithAuthor.size(), id);

    authorFollowRepository.deleteByAuthorId(id);
    log.info("[AUTHOR] [DELETE] Eliminados follows del autor id={}", id);

    authorRepository.delete(author);
    log.info("[AUTHOR] [DELETE] [SUCCESS] id={} name='{}'", id, author.getName());
  }

  /**
   * Crea una relación entre los usuarios y los autores
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void followAuthor(Long userId, Long authorId) {
    if (authorFollowRepository.existsByUserIdAndAuthorId(userId, authorId))
      return;

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + userId));
    Author author = getAuthorEntityById(authorId);

    authorFollowRepository.save(AuthorFollow.builder()
        .user(user)
        .author(author)
        .followDate(LocalDateTime.now())
        .build());

    emailService.sendFollowConfirmation(user.getEmail(), author.getName());
  }

  /**
   * Elimina un autor dentro de la aplicación
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional
  public void unfollowAuthor(Long userId, Long authorId) {
    authorFollowRepository.deleteByUserIdAndAuthorId(userId, authorId);
  }

  /**
   * Comprueba una condicion de negocio y devuelve el resultado.
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return true si la condicion se cumple; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public boolean isFollowing(Long userId, Long authorId) {
    return authorFollowRepository.existsByUserIdAndAuthorId(userId, authorId);
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param authorId identificador del autor asociado a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserResponseDTO> getFollowers(Long authorId) {
    return authorFollowRepository.findByAuthorId(authorId).stream()
        .filter(af -> af.getUser() != null)
        .map(af -> {
          User u = af.getUser();
          return new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail(),
              u.getName(), u.getSecondName(), u.getImg(),
              u.getRegistrationDate(), u.getActive(), u.getRole());
        })
        .toList();
  }

  // =========================
  // MAPPERS
  // =========================

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param author valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private AuthorResponseDTO mapAuthorToDTO(Author author) {
    return new AuthorResponseDTO(
        author.getId(),
        author.getName(),
        author.getNationality(),
        author.getBirthDate(),
        author.getImg(),
        authorFollowRepository.countByAuthorId(author.getId()),
        author.getWorks().stream()
            .map(this::mapWorkToDTO)
            .toList());
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param work valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private WorkResponseDTO mapWorkToDTO(Work work) {
    return new WorkResponseDTO(
        work.getId(),
        work.getTitle(),
        work.getDescription(),
        work.getGenre(),
        work.getType(),
        work.getDemographic(),
        work.getPublicationDate(),
        work.getImg(),
        work.getAverageRating(),
        work.getAuthors()
            .stream()
            .map(Author::getName)
            .toList());
  }

  // =========================
  // UTILIDADES
  // =========================

  /**
   * Ejecuta la operacion de servicio syncAuthorWorkAssociations.
   *
   * @param author     valor utilizado por la operacion
   * @param newWorkIds valor utilizado por la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void syncAuthorWorkAssociations(Author author, List<Long> newWorkIds) {
    Long authorId = author.getId();
    if (authorId == null)
      return;

    // Calcula diferencias para eliminar asociaciones antiguas y agregar solo las
    // nuevas.
    List<Work> currentWorks = workRepository.findByAuthorId(authorId);
    Set<Long> newIds = new HashSet<>(newWorkIds);
    Set<Long> currentIds = new HashSet<>();
    currentWorks.forEach(w -> currentIds.add(w.getId()));

    currentWorks.stream()
        .filter(w -> !newIds.contains(w.getId()))
        .forEach(w -> {
          w.getAuthors().removeIf(a -> authorId.equals(a.getId()));
          workRepository.save(w);
        });

    newIds.stream()
        .filter(id -> !currentIds.contains(id))
        .forEach(id -> workRepository.findById(id).ifPresent(w -> {
          if (w.getAuthors().stream().noneMatch(a -> authorId.equals(a.getId()))) {
            w.getAuthors().add(author);
            workRepository.save(w);
          }
        }));
  }

  /**
   * Obtiene un autor por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private Author getAuthorEntityById(Long id) {
    return authorRepository.findById(id)
        .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado con id: " + id));
  }

  /**
   * Valida las reglas de negocio antes de continuar la operacion.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void validateAuthorRequest(AuthorRequestDTO request) {
    if (request == null) {
      throw new IllegalArgumentException("La request no puede ser nula");
    }
    if (request.getName() == null || request.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre del autor es obligatorio");
    }
  }
}
