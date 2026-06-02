package com.example.booksocial_frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de seguimiento de pedido desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingOrderRequestDTO {
  /** Identificador del pedido relacionado. */
  private Long orderId;
  /** Estado de seguimiento enviado al backend. */
  private String trackingStatus;
}
