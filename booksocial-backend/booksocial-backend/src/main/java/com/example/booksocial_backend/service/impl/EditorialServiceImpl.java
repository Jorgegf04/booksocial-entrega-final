package com.example.booksocial_backend.service.impl;

import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.catalog.EditorialRequestDTO;
import com.example.booksocial_backend.DTO.catalog.EditorialResponseDTO;
import com.example.booksocial_backend.domain.catalog.Editorial;
import com.example.booksocial_backend.exception.EditorialAlreadyExistsException;
import com.example.booksocial_backend.exception.EditorialHasEditionsException;
import com.example.booksocial_backend.exception.EditorialNotFoundException;
import com.example.booksocial_backend.repository.EditorialRepository;
import com.example.booksocial_backend.service.EditorialService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de editoriales.
 *
 * Define las operaciones de negocio para administrar editoriales del catalogo. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EditorialServiceImpl implements EditorialService {

  private static final Logger log = LoggerFactory.getLogger(EditorialServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar editoriales. */
  private final EditorialRepository editorialRepository;
  /** Componente utilizado para transformar DTOs en entidades de dominio. */
  private final ModelMapper modelMapper;

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
  public EditorialResponseDTO createEditorial(EditorialRequestDTO request) {

    log.info("[EDITORIAL] [CREATE] [START] name='{}'", request != null ? request.getName() : "null");

    Editorial editorial = modelMapper.map(request, Editorial.class);
    validateEditorial(editorial);

    String normalizedName = editorial.getName().trim();
    if (editorialRepository.existsByName(normalizedName)) {
      log.warn("[EDITORIAL] [CREATE] [CONFLICT] Ya existe editorial con nombre='{}'", normalizedName);
      throw new EditorialAlreadyExistsException(normalizedName);
    }

    editorial.setName(normalizedName);
    Editorial saved = Objects.requireNonNull(editorialRepository.save(editorial));
    log.info("[EDITORIAL] [CREATE] [SUCCESS] id={} name='{}'", saved.getId(), saved.getName());
    return mapToDTO(saved);
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
  public EditorialResponseDTO getEditorialById(Long id) {
    return mapToDTO(getEditorialEntityById(id));
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
  public List<EditorialResponseDTO> getAllEditorials() {
    return editorialRepository.findAll()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public EditorialResponseDTO updateEditorial(Long id, EditorialRequestDTO request) {

    log.info("[EDITORIAL] [UPDATE] [START] id={}", id);
    Editorial existing = getEditorialEntityById(id);
    Editorial updated = modelMapper.map(request, Editorial.class);
    validateEditorial(updated);

    String normalizedName = updated.getName().trim();
    if (!existing.getName().equalsIgnoreCase(normalizedName)
        && editorialRepository.existsByName(normalizedName)) {
      throw new IllegalArgumentException("Ya existe otra editorial con ese nombre");
    }

    existing.setName(normalizedName);
    existing.setCountry(updated.getCountry());

    Editorial saved = Objects.requireNonNull(editorialRepository.save(existing));
    log.info("[EDITORIAL] [UPDATE] [SUCCESS] id={} name='{}'", saved.getId(), saved.getName());
    return mapToDTO(saved);
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
  public void deleteEditorial(Long id) {
    log.info("[EDITORIAL] [DELETE] [START] id={}", id);
    Editorial editorial = getEditorialEntityById(id);
    int editionCount = editorial.getEditions() != null ? editorial.getEditions().size() : 0;
    if (editionCount > 0) {
      log.warn("[EDITORIAL] [DELETE] [CONFLICT] editorial='{}' tiene {} edición(es)", editorial.getName(), editionCount);
      throw new EditorialHasEditionsException(editorial.getName(), editionCount);
    }
    editorialRepository.delete(editorial);
    log.info("[EDITORIAL] [DELETE] [SUCCESS] id={} name='{}'", id, editorial.getName());
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param editorial valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private EditorialResponseDTO mapToDTO(Editorial editorial) {
    return new EditorialResponseDTO(
        editorial.getId(),
        editorial.getName(),
        editorial.getCountry(),
        editorial.getEditions() != null ? editorial.getEditions().size() : 0);
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
  private Editorial getEditorialEntityById(Long id) {
    return editorialRepository.findById(id)
        .orElseThrow(() -> new EditorialNotFoundException("Editorial no encontrada con id: " + id));
  }

  /**
   * Valida las reglas de negocio antes de continuar la operacion.
   *
   * @param editorial valor utilizado por la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void validateEditorial(Editorial editorial) {
    if (editorial == null) {
      throw new IllegalArgumentException("La editorial no puede ser nula");
    }
    if (editorial.getName() == null || editorial.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de la editorial es obligatorio");
    }
  }
}
