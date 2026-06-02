package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.social.*;

/**
 * Contrato de servicio para seguimiento de obras.
 * Define operaciones para biblioteca, wishlist y estado de lectura de obras por usuario.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface TrackingWorkService {

  /**
   * Crea un seguimiento de obra.
   *
   * @param request DTO de entrada
   * @return seguimiento creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  TrackingWorkResponseDTO create(TrackingWorkRequestDTO request);

  /**
   * Crea múltiples seguimientos.
   *
   * @param requests lista de solicitudes
   * @return lista de seguimientos
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<TrackingWorkResponseDTO> createMany(List<TrackingWorkRequestDTO> requests);

  /**
   * Obtiene seguimientos por usuario.
   *
   * @param userId ID del usuario
   * @return lista de seguimientos
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<TrackingWorkResponseDTO> getByUser(Long userId);

  /**
   * Elimina un seguimiento.
   *
   * @param id ID del seguimiento
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void delete(Long id);

  /**
   * Actualiza el estado de un seguimiento de obra.
   *
   * @param id      identificador del seguimiento
   * @param request DTO con el nuevo estado
   * @return seguimiento actualizado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  TrackingWorkResponseDTO updateStatus(Long id, TrackingWorkRequestDTO request);
}
