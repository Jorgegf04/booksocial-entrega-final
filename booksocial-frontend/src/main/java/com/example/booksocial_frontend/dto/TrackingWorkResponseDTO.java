package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de seguimiento de lectura desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class TrackingWorkResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Identificador del usuario relacionado. */
  private Long userId;
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Identificador de la obra relacionada. */
  private Long workId;
  /** Titulo de la obra relacionada. */
  private String workTitle;
  /** Estado actual del seguimiento. */
  private String status;
  /** Texto legible del estado actual. */
  private String statusLabel;
  /** Fecha en la que se creo o registro el dato. */
  private LocalDateTime date;
  /** Indica si el seguimiento esta completado. */
  private Boolean completed;
}
