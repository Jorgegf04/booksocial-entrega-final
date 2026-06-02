package com.example.booksocial_frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para guardar un producto dentro del carrito de la sesion.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
  /** Identificador del producto relacionado. */
  private Long productId;
  /** Titulo de la obra relacionada. */
  private String workTitle;
  /** Titulo de la edicion relacionada. */
  private String editionTitle;
  /** Nombre de la editorial relacionada. */
  private String editorialName;
  /** Imagen de la obra mostrada en el carrito. */
  private String workImg;
  /** Precio del producto. */
  private Double price;
  /** Cantidad seleccionada. */
  private Integer quantity;

  public Double getSubtotal() {
    return price != null && quantity != null ? price * quantity : 0.0;
  }
}
