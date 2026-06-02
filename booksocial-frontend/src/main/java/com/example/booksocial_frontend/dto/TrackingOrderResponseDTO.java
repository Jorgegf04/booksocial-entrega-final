package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de seguimiento de pedido desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class TrackingOrderResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Estado actual del seguimiento. */
  private String status;
  /** Texto legible del estado actual. */
  private String statusLabel;
  /** Fecha en la que se creo o registro el dato. */
  private LocalDateTime date;
  /** Identificador del pedido relacionado. */
  private Long orderId;
}
