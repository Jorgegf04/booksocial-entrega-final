package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de seguimiento de lectura desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class TrackingWorkRequestDTO {
  /** Identificador del usuario relacionado. */
  private Long userId;
  /** Identificador de la obra relacionada. */
  private Long workId;
  /** Estado actual del seguimiento. */
  private String status;

  public TrackingWorkRequestDTO(Long userId, Long workId, String status) {
    this.userId = userId;
    this.workId = workId;
    this.status = status;
  }
}
