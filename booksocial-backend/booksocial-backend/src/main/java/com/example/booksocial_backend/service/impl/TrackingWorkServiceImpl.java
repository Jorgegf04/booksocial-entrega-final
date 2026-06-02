package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.social.*;
import com.example.booksocial_backend.exception.TrackingWorkAlreadyExistsException;
import com.example.booksocial_backend.exception.TrackingWorkNotFoundException;
import com.example.booksocial_backend.exception.WorkNotFoundException;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.social.TrackingWork;
import com.example.booksocial_backend.domain.social.TrackingWorkStatus;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.repository.TrackingWorkRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.TrackingWorkService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para seguimiento de obras.
 *
 * Define operaciones para biblioteca, wishlist y estado de lectura de obras por usuario. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrackingWorkServiceImpl implements TrackingWorkService {

  private static final Logger log = LoggerFactory.getLogger(TrackingWorkServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar tracking de obras. */
  private final TrackingWorkRepository repository;
  /** Repositorio JPA utilizado para validar la obra asociada al tracking. */
  private final WorkRepository workRepository;

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
  public TrackingWorkResponseDTO create(TrackingWorkRequestDTO request) {

    validateRequest(request);
    log.info("[TRACKING_WORK] [CREATE] [START] userId={} workId={}", request.getUserId(), request.getWorkId());

    if (repository.existsByUserIdAndWorkId(request.getUserId(), request.getWorkId())) {
      throw new TrackingWorkAlreadyExistsException(request.getUserId(), request.getWorkId());
    }

    Work work = workRepository.findById(request.getWorkId())
        .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + request.getWorkId()));

    TrackingWork tracking = TrackingWork.builder()
        .user(User.builder().id(request.getUserId()).build())
        .work(work)
        .date(LocalDateTime.now())
        .status(
            request.getStatus() != null
                ? request.getStatus()
                : TrackingWorkStatus.PENDING)
        .build();

    return map(repository.save(tracking));
  }

  /**
   * Ejecuta una creacion masiva de recursos.
   *
   * @param requests lista de DTOs con los datos necesarios para ejecutar la operacion masiva
   * @return lista de DTOs de respuesta creados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public List<TrackingWorkResponseDTO> createMany(List<TrackingWorkRequestDTO> requests) {

    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista no puede estar vacía");
    }

    return requests.stream()
        .map(this::create)
        .toList();
  }

  /**
   * Obtiene recursos asociados a un usuario.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return lista de DTOs asociados al usuario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<TrackingWorkResponseDTO> getByUser(Long userId) {

    return repository.findByUserId(userId)
        .stream()
        .map(this::map)
        .toList();
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
  public void delete(Long id) {

    log.info("[TRACKING_WORK] [DELETE] [START] id={}", id);
    repository.deleteById(id);
    log.info("[TRACKING_WORK] [DELETE] [SUCCESS] id={}", id);
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
  public TrackingWorkResponseDTO updateStatus(Long id, TrackingWorkRequestDTO request) {

    if (request == null || request.getStatus() == null) {
      throw new IllegalArgumentException("El estado es obligatorio");
    }

    log.info("[TRACKING_WORK] [UPDATE_STATUS] [START] id={} status={}", id, request.getStatus());
    TrackingWork tracking = repository.findById(id)
        .orElseThrow(() -> new TrackingWorkNotFoundException(id));

    tracking.setStatus(request.getStatus());

    return map(repository.save(tracking));
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param t valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private TrackingWorkResponseDTO map(TrackingWork t) {
    return new TrackingWorkResponseDTO(
        t.getId(),

        t.getUser() != null ? t.getUser().getId() : null,
        t.getUser() != null ? t.getUser().getUsername() : null,

        t.getWork() != null ? t.getWork().getId() : null,
        t.getWork() != null ? t.getWork().getTitle() : null,

        t.getStatus(),
        t.getStatus() != null ? mapStatusLabel(t.getStatus()) : null,

        t.getDate(),

        t.getStatus() == TrackingWorkStatus.COMPLETED);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param status estado asociado a la operacion
   * @return texto generado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private String mapStatusLabel(TrackingWorkStatus status) {
    return switch (status) {
      case PENDING -> "Pendiente";
      case READING -> "Leyendo";
      case COMPLETED -> "Completado";
      case DROPPED -> "Abandonado";
    };
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
  private void validateRequest(TrackingWorkRequestDTO request) {

    if (request == null) {
      throw new IllegalArgumentException("Request inválido");
    }

    if (request.getUserId() == null) {
      throw new IllegalArgumentException("UserId obligatorio");
    }

    if (request.getWorkId() == null) {
      throw new IllegalArgumentException("WorkId obligatorio");
    }
  }

}
