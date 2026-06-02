package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de linea de pedido desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class OrderLineResponseDTO {
  /** Identificador del producto relacionado. */
  private Long productId;
  /** Titulo que se muestra en la vista. */
  private String title;
  /** Precio del producto. */
  private Double price;
  /** Cantidad seleccionada. */
  private Integer quantity;
  /** Importe parcial de una linea del pedido. */
  private Double subtotal;
}
