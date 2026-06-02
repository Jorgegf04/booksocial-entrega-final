package com.example.booksocial_backend.DTO.social;

import java.time.LocalDateTime;

import com.example.booksocial_backend.domain.social.TrackingWorkStatus;

import lombok.*;

/**
 * DTO de salida para el seguimiento de obras.
 *
 * Representa la información del seguimiento realizado por un usuario.
 *
 * @author Jorge
 * @since 2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingWorkResponseDTO {
  /**
   * Identificador unico del recurso.
   */
  private Long id;
  /**
   * Identificador del usuario asociado.
   */
  private Long userId;
  /**
   * Nombre de usuario asociado al recurso.
   */
  private String username;
  /**
   * Identificador de la obra asociada.
   */
  private Long workId;
  /**
   * Titulo de la obra asociada.
   */
  private String workTitle;
  /**
   * Estado actual del seguimiento.
   */
  private TrackingWorkStatus status;
  /**
   * Texto legible que representa el estado actual.
   */
  private String statusLabel;
  /**
   * Fecha y hora asociada al evento o registro.
   */
  private LocalDateTime date;
  /**
   * Indica si el seguimiento ha alcanzado un estado final.
   */
  private Boolean completed;

}