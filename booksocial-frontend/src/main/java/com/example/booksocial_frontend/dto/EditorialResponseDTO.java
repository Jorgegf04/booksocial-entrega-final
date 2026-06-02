package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de editorial desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class EditorialResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Nombre principal que se muestra en la vista. */
  private String name;
  /** Pais de la editorial. */
  private String country;
}
