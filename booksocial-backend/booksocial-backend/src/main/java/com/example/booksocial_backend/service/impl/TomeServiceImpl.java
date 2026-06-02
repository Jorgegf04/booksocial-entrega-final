package com.example.booksocial_backend.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.catalog.Tome;
import com.example.booksocial_backend.DTO.catalog.TomeRequestDTO;
import com.example.booksocial_backend.DTO.catalog.TomeResponseDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.repository.EditionRepository;
import com.example.booksocial_backend.repository.TomeRepository;
import com.example.booksocial_backend.exception.EditionNotFoundException;
import com.example.booksocial_backend.exception.TomeAlreadyExistsException;
import com.example.booksocial_backend.exception.TomeNotFoundException;
import com.example.booksocial_backend.service.TomeService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de tomos.
 *
 * Define operaciones de negocio para tomos asociados a ediciones de manga. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TomeServiceImpl implements TomeService {

  private static final Logger log = LoggerFactory.getLogger(TomeServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar tomos. */
  private final TomeRepository tomeRepository;
  /** Repositorio JPA utilizado para actualizar el total de tomos de una edicion. */
  private final EditionRepository editionRepository;
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
  public TomeResponseDTO createTome(TomeRequestDTO request) {

    log.info("[TOME] [CREATE] [START] editionId={} number={}", request.getEditionId(), request.getNumberTome());
    Tome tome = modelMapper.map(request, Tome.class);

    tome.setEdition(Edition.builder().id(request.getEditionId()).build());

    validateTome(tome);

    List<Tome> existing = tomeRepository.findByEditionId(request.getEditionId());

    boolean exists = existing.stream()
        .anyMatch(t -> t.getNumberTome().equals(request.getNumberTome()));

    if (exists) {
      throw new TomeAlreadyExistsException(request.getNumberTome(), request.getEditionId());
    }

    Tome saved = tomeRepository.save(tome);
    updateEditionTotalTomes(saved.getEdition().getId());
    log.info("[TOME] [CREATE] [SUCCESS] id={} number={}", saved.getId(), saved.getNumberTome());
    return mapToDTO(saved);
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
  public List<TomeResponseDTO> getAllTomes() {

    return tomeRepository.findAll()
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
  public TomeResponseDTO updateTome(Long id, TomeRequestDTO request) {

    log.info("[TOME] [UPDATE] [START] id={}", id);
    Tome existing = getTomeEntityById(id);

    Long oldEditionId = existing.getEdition().getId();

    Tome updated = modelMapper.map(request, Tome.class);

    updated.setEdition(Edition.builder().id(request.getEditionId()).build());

    validateTome(updated);

    if (!existing.getNumberTome().equals(updated.getNumberTome())) {

      List<Tome> tomes = tomeRepository.findByEditionId(request.getEditionId());

      boolean exists = tomes.stream()
          .anyMatch(t -> t.getNumberTome().equals(request.getNumberTome()));

      if (exists) {
        throw new TomeAlreadyExistsException(request.getNumberTome(), request.getEditionId());
      }
    }

    existing.setNumberTome(updated.getNumberTome());
    existing.setEdition(updated.getEdition());

    Tome saved = tomeRepository.save(existing);

    updateEditionTotalTomes(oldEditionId);
    updateEditionTotalTomes(saved.getEdition().getId());

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
  public void deleteTome(Long id) {

    log.info("[TOME] [DELETE] [START] id={}", id);
    Tome tome = getTomeEntityById(id);
    Long editionId = tome.getEdition().getId();
    tomeRepository.delete(tome);
    updateEditionTotalTomes(editionId);
    log.info("[TOME] [DELETE] [SUCCESS] id={}", id);
  }

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param editionId identificador de la edicion asociada a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void updateEditionTotalTomes(Long editionId) {

    Edition edition = editionRepository.findById(editionId)
        .orElseThrow(() -> new EditionNotFoundException("Edición no encontrada con id: " + editionId));

    // El total se recalcula desde la base de datos para reflejar altas, bajas y cambios de edicion.
    int total = tomeRepository.findByEditionId(editionId).size();

    edition.setTotalTomes(total);

    editionRepository.save(edition);
  }

  /**
   * Convierte Tome en TomeDTO.
   */
  private TomeResponseDTO mapToDTO(Tome tome) {

    return new TomeResponseDTO(
        tome.getId(),
        tome.getNumberTome(),
        tome.getTitle(),

        tome.getEdition() != null ? tome.getEdition().getId() : null,
        tome.getEdition() != null ? tome.getEdition().getTitle() : null,

        (tome.getEdition() != null && tome.getEdition().getWork() != null)
            ? tome.getEdition().getWork().getTitle()
            : null);
  }

  /**
   * Obtiene la entidad Tome o lanza excepción si no existe.
   */
  private Tome getTomeEntityById(Long id) {

    return tomeRepository.findById(id)
        .orElseThrow(() -> new TomeNotFoundException("Tomo no encontrado con id: " + id));
  }

  /**
   * Valida los datos de un tomo.
   */
  private void validateTome(Tome tome) {

    if (tome == null) {
      throw new IllegalArgumentException("El tomo no puede ser nulo");
    }

    if (tome.getNumberTome() == null || tome.getNumberTome() <= 0) {
      throw new IllegalArgumentException("El número de tomo debe ser positivo");
    }

    if (tome.getEdition() == null || tome.getEdition().getId() == null) {
      throw new IllegalArgumentException("El tomo debe pertenecer a una edición válida");
    }
  }

}
