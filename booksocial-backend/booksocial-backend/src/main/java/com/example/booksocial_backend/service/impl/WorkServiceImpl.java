package com.example.booksocial_backend.service.impl;

import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.exception.AuthorNotFoundException;
import com.example.booksocial_backend.exception.WorkNotFoundException;
import com.example.booksocial_backend.DTO.catalog.WorkRequestDTO;
import com.example.booksocial_backend.DTO.catalog.WorkResponseDTO;
import com.example.booksocial_backend.domain.catalog.Author;
import com.example.booksocial_backend.repository.AuthorFollowRepository;
import com.example.booksocial_backend.repository.AuthorRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.EmailService;
import com.example.booksocial_backend.service.WorkService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de obras.
 *
 * Define operaciones de catalogo para crear, buscar, consultar, actualizar y
 * eliminar obras. Coordina repositorios, validaciones de dominio y
 * transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkServiceImpl implements WorkService {

  private static final Logger log = LoggerFactory.getLogger(WorkServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar obras. */
  private final WorkRepository workRepository;
  /** Componente utilizado para transformar DTOs en entidades de dominio. */
  private final ModelMapper modelMapper;
  /** Repositorio JPA utilizado para resolver autores por nombre. */
  private final AuthorRepository authorRepository;
  /** Repositorio JPA utilizado para localizar seguidores de autores. */
  private final AuthorFollowRepository authorFollowRepository;
  /** Servicio utilizado para enviar avisos de nuevas obras a seguidores. */
  private final EmailService emailService;

  /**
   * Crea un nuevo recurso aplicando las validaciones de negocio correspondientes.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del recurso creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public WorkResponseDTO createWork(WorkRequestDTO request) {

    log.info("[WORK] [CREATE] [START] title='{}'", request != null ? request.getTitle() : "null");
    validateWorkRequest(request);

    // Codigo de la ilustracion 30
    Work work = modelMapper.map(request, Work.class);

    work.setTitle(request.getTitle().trim());
    work.setGenre(request.getGenre());

    if (request.getAuthors() != null) {
      work.setAuthors(resolveAuthorsByName(request.getAuthors()));
    }

    Work saved = Objects.requireNonNull(workRepository.save(work));
    log.info("[WORK] [CREATE] [SUCCESS] id={} title='{}'", saved.getId(), saved.getTitle());

    List<Author> savedAuthors = saved.getAuthors();
    if (savedAuthors != null) {
      // La notificacion a seguidores es secundaria: si falla el correo, la obra sigue
      // creada.
      savedAuthors.forEach(author -> authorFollowRepository.findByAuthorId(author.getId()).forEach(follow -> {
        try {
          if (follow.getUser() != null && follow.getUser().getEmail() != null) {
            emailService.sendNewWorkNotification(
                follow.getUser().getEmail(), author.getName(), saved.getTitle());
          }
        } catch (Exception e) {
          log.warn("[WORK] [EMAIL] Error al notificar a seguidor: {}", e.getMessage());
        }
      }));
    }

    return mapWorkToDTO(saved);
  }

  /**
   * Ejecuta una creacion masiva de recursos.
   *
   * @param requests lista de DTOs con los datos necesarios para ejecutar la
   *                 operacion masiva
   * @return lista de DTOs de respuesta creados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public List<WorkResponseDTO> createMany(List<WorkRequestDTO> requests) {

    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista de obras no puede estar vacía");
    }

    List<Work> works = requests.stream()
        .map(request -> {

          validateWorkRequest(request);

          Work work = modelMapper.map(request, Work.class);

          work.setTitle(request.getTitle().trim());
          work.setGenre(request.getGenre());

          if (request.getAuthors() != null) {
            work.setAuthors(resolveAuthorsByName(request.getAuthors()));
          }

          return work;
        })
        .toList();

    List<Work> saved = workRepository.saveAll(List.copyOf(works));

    saved.forEach(work -> {
      List<Author> authors = work.getAuthors();
      if (authors != null) {
        // Cada obra creada en lote notifica a los seguidores de sus autores asociados.
        authors.forEach(author -> authorFollowRepository.findByAuthorId(author.getId()).forEach(follow -> {
          try {
            if (follow.getUser() != null && follow.getUser().getEmail() != null) {
              emailService.sendNewWorkNotification(
                  follow.getUser().getEmail(), author.getName(), work.getTitle());
            }
          } catch (Exception e) {
            log.warn("[WORK] [EMAIL] Error al notificar a seguidor: {}", e.getMessage());
          }
        }));
      }
    });

    return saved.stream().map(this::mapWorkToDTO).toList();
  }

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public WorkResponseDTO getWorkById(Long id) {
    return mapWorkToDTO(getWorkEntityById(id));
  }

  /**
   * Obtiene todos los recursos disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<WorkResponseDTO> getAllWorks() {
    return workRepository.findAllWithAuthors()
        .stream()
        .map(this::mapWorkToDTO)
        .toList();
  }

  /**
   * Busca recursos aplicando los filtros recibidos.
   *
   * @param title  titulo usado como filtro de busqueda
   * @param genre  genero usado como filtro de busqueda
   * @param rating valoracion minima usada como filtro de busqueda
   * @return lista de DTOs que cumplen los filtros
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<WorkResponseDTO> searchWorks(String title, String genre, Double rating) {
    return workRepository.searchWorksWithAuthors(title, genre, rating)
        .stream()
        .map(this::mapWorkToDTO)
        .toList();
  }

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param id      identificador del recurso sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public WorkResponseDTO updateWork(Long id, WorkRequestDTO request) {

    log.info("[WORK] [UPDATE] [START] id={}", id);
    Work existing = getWorkEntityById(id);

    if (request.getTitle() != null) {
      existing.setTitle(request.getTitle().trim());
    }

    if (request.getDescription() != null) {
      existing.setDescription(request.getDescription());
    }

    if (request.getGenre() != null) {
      existing.setGenre(request.getGenre());
    }

    if (request.getPublicationDate() != null) {
      existing.setPublicationDate(request.getPublicationDate());
    }

    if (request.getImg() != null) {
      existing.setImg(request.getImg());
    }

    if (request.getAverageRating() != null) {
      existing.setAverageRating(request.getAverageRating());
    }

    if (request.getAuthors() != null) {
      existing.getAuthors().clear();
      existing.getAuthors().addAll(resolveAuthorsByName(request.getAuthors()));
    }

    validateWork(existing);

    Work saved = Objects.requireNonNull(workRepository.save(existing));
    log.info("[WORK] [UPDATE] [SUCCESS] id={} title='{}'", saved.getId(), saved.getTitle());
    return mapWorkToDTO(saved);
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteWork(Long id) {
    log.info("[WORK] [DELETE] [START] id={}", id);
    Work work = getWorkEntityById(id);
    workRepository.delete(Objects.requireNonNull(work));
    log.info("[WORK] [DELETE] [SUCCESS] id={} title='{}'", id, work.getTitle());
  }

  // =========================
  // MAPPERS
  // =========================

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

    WorkResponseDTO dto = new WorkResponseDTO();

    dto.setId(work.getId());
    dto.setTitle(work.getTitle());
    dto.setDescription(work.getDescription());
    dto.setGenre(work.getGenre());
    dto.setType(work.getType());
    dto.setDemographic(work.getDemographic());
    dto.setPublicationDate(work.getPublicationDate());
    dto.setImg(work.getImg());
    dto.setAverageRating(work.getAverageRating());

    List<Author> authors = work.getAuthors();
    dto.setAuthors(
        authors != null
            ? authors.stream().map(Author::getName).toList()
            : List.of());

    return dto;
  }

  // =========================
  // UTILIDADES
  // =========================

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private Work getWorkEntityById(Long id) {
    return workRepository.findByIdWithAuthors(id)
        .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + id));
  }

  /**
   * Resuelve entidades relacionadas a partir de los datos recibidos.
   *
   * @param authorNames valor utilizado por la operacion
   * @return lista de entidades resueltas
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private List<Author> resolveAuthorsByName(List<String> authorNames) {
    return authorNames.stream()
        .map(name -> {
          String normalized = name.trim();
          return authorRepository.findByNameIgnoreCase(normalized)
              .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado: " + normalized));
        })
        .toList();
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
  private void validateWorkRequest(WorkRequestDTO request) {

    if (request == null) {
      throw new IllegalArgumentException("La request no puede ser nula");
    }

    if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("El título es obligatorio");
    }

    if (request.getGenre() == null) {
      throw new IllegalArgumentException("El género es obligatorio");
    }

    if (request.getType() == null) {
      throw new IllegalArgumentException("El tipo de obra es obligatorio");
    }
  }

  /**
   * Valida las reglas de negocio antes de continuar la operacion.
   *
   * @param work valor utilizado por la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void validateWork(Work work) {

    if (work == null) {
      throw new IllegalArgumentException("La obra no puede ser nula");
    }

    if (work.getTitle() == null || work.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("El título es obligatorio");
    }

    if (work.getGenre() == null) {
      throw new IllegalArgumentException("El género es obligatorio");
    }
  }

}
