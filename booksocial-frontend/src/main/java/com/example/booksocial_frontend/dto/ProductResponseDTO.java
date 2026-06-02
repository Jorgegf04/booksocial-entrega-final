package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de producto desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class ProductResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Precio del producto. */
  private Double price;
  /** Cantidad disponible en inventario. */
  private Integer stock;
  /** Identificador de la edicion relacionada. */
  private Long editionId;
  /** Titulo de la edicion relacionada. */
  private String editionTitle;
  /** ISBN de la edicion relacionada. */
  private String editionIsbn;
  /** Numero total de tomos de la edicion. */
  private Integer totalTomes;
  /** Identificador de la obra relacionada. */
  private Long workId;
  /** Titulo de la obra relacionada. */
  private String workTitle;
  /** Nombre de la editorial relacionada. */
  private String editorialName;
}
