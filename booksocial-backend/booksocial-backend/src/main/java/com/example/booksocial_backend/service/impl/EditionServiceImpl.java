package com.example.booksocial_backend.service.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.catalog.EditionRequestDTO;
import com.example.booksocial_backend.DTO.catalog.EditionResponseDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.domain.catalog.Editorial;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.exception.EditionAlreadyExistsException;
import com.example.booksocial_backend.exception.EditionHasOrderLinesException;
import com.example.booksocial_backend.exception.EditionNotFoundException;
import com.example.booksocial_backend.exception.EditorialNotFoundException;
import com.example.booksocial_backend.exception.WorkNotFoundException;
import com.example.booksocial_backend.repository.EditionRepository;
import com.example.booksocial_backend.repository.EditorialRepository;
import com.example.booksocial_backend.repository.OrderLineRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.EditionService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de ediciones.
 *
 * Define las operaciones de negocio para crear, consultar, actualizar y eliminar ediciones. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EditionServiceImpl implements EditionService {

  private static final Logger log = LoggerFactory.getLogger(EditionServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar ediciones. */
  private final EditionRepository editionRepository;
  /** Repositorio JPA utilizado para validar la obra asociada a la edicion. */
  private final WorkRepository workRepository;
  /** Repositorio JPA utilizado para validar la editorial asociada a la edicion. */
  private final EditorialRepository editorialRepository;
  /** Repositorio JPA utilizado para comprobar ventas antes de eliminar ediciones. */
  private final OrderLineRepository orderLineRepository;

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
  public EditionResponseDTO createEdition(EditionRequestDTO request) {

    log.info("[EDITION] [CREATE] [START] isbn='{}'", request != null ? request.getIsbn() : "null");

    if (request == null) {
      throw new IllegalArgumentException("La request no puede ser nula");
    }
    if (request.getIsbn() == null || request.getIsbn().trim().isEmpty()) {
      throw new IllegalArgumentException("El ISBN es obligatorio");
    }
    if (request.getWorkId() == null) {
      throw new IllegalArgumentException("La edición debe estar asociada a una obra");
    }
    if (request.getEditorialId() == null) {
      throw new IllegalArgumentException("La edición debe estar asociada a una editorial");
    }

    Work work = workRepository.findById(request.getWorkId())
        .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + request.getWorkId()));

    Editorial editorial = editorialRepository.findById(request.getEditorialId())
        .orElseThrow(() -> new EditorialNotFoundException("Editorial no encontrada con id: " + request.getEditorialId()));

    Edition edition = new Edition();
    edition.setIsbn(request.getIsbn().trim());
    edition.setEditionDate(request.getEditionDate());
    edition.setTitle(request.getTitle());
    edition.setTotalTomes(request.getTotalTomes());
    edition.setWork(work);
    edition.setEditorial(editorial);

    if (editionRepository.findByIsbn(edition.getIsbn()).isPresent()) {
      log.warn("[EDITION] [CREATE] [CONFLICT] Ya existe edición con isbn='{}'", edition.getIsbn());
      throw new EditionAlreadyExistsException(edition.getIsbn());
    }

    Edition saved = Objects.requireNonNull(editionRepository.save(edition));
    log.info("[EDITION] [CREATE] [SUCCESS] id={} isbn='{}'", saved.getId(), saved.getIsbn());
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
  public EditionResponseDTO getEditionById(Long id) {
    return mapToDTO(getEditionEntityById(id));
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
  public List<EditionResponseDTO> getAllEditions() {
    return editionRepository.findAll()
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
  public EditionResponseDTO updateEdition(Long id, EditionRequestDTO request) {

    log.info("[EDITION] [UPDATE] [START] id={}", id);
    Edition existing = getEditionEntityById(id);

    if (request.getIsbn() != null && !request.getIsbn().isBlank()) {
      String normalizedIsbn = request.getIsbn().trim();
      if (!existing.getIsbn().equalsIgnoreCase(normalizedIsbn)
          && editionRepository.findByIsbn(normalizedIsbn).isPresent()) {
        throw new EditionAlreadyExistsException(normalizedIsbn);
      }
      existing.setIsbn(normalizedIsbn);
    }

    if (request.getEditionDate() != null) existing.setEditionDate(request.getEditionDate());
    if (request.getTitle() != null) existing.setTitle(request.getTitle());
    if (request.getTotalTomes() != null) existing.setTotalTomes(request.getTotalTomes());

    if (request.getWorkId() != null) {
      Work work = workRepository.findById(request.getWorkId())
          .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + request.getWorkId()));
      existing.setWork(work);
    }

    if (request.getEditorialId() != null) {
      Editorial editorial = editorialRepository.findById(request.getEditorialId())
          .orElseThrow(() -> new EditorialNotFoundException("Editorial no encontrada con id: " + request.getEditorialId()));
      existing.setEditorial(editorial);
    }

    Edition saved = Objects.requireNonNull(editionRepository.save(existing));
    log.info("[EDITION] [UPDATE] [SUCCESS] id={} isbn='{}'", saved.getId(), saved.getIsbn());
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
  public void deleteEdition(Long id) {
    log.info("[EDITION] [DELETE] [START] id={}", id);
    Edition edition = getEditionEntityById(id);
    boolean hasOrderLines = edition.getProducts() != null &&
        edition.getProducts().stream().anyMatch(p -> orderLineRepository.existsByProductId(p.getId()));
    if (hasOrderLines) {
      log.warn("[EDITION] [DELETE] [CONFLICT] edición id={} tiene pedidos históricos", id);
      throw new EditionHasOrderLinesException(id);
    }
    editionRepository.delete(edition);
    log.info("[EDITION] [DELETE] [SUCCESS] id={} isbn='{}'", id, edition.getIsbn());
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param edition valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private EditionResponseDTO mapToDTO(Edition edition) {
    Integer totalTomes = edition.getTotalTomes() != null
        ? edition.getTotalTomes()
        : (edition.getTomes() != null ? edition.getTomes().size() : 0);

    return new EditionResponseDTO(
        edition.getId(),
        edition.getIsbn(),
        edition.getEditionDate(),
        edition.getTitle(),
        totalTomes,
        edition.getWork() != null ? edition.getWork().getId() : null,
        edition.getWork() != null ? edition.getWork().getTitle() : null,
        edition.getEditorial() != null ? edition.getEditorial().getId() : null,
        edition.getEditorial() != null ? edition.getEditorial().getName() : null);
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
  private Edition getEditionEntityById(Long id) {
    return editionRepository.findById(id)
        .orElseThrow(() -> new EditionNotFoundException("Edición no encontrada con id: " + id));
  }
}
