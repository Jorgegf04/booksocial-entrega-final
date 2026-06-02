package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de editorial desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class EditorialRequestDTO {
  /** Nombre principal que se muestra en la vista. */
  private String name;
  /** Pais de la editorial. */
  private String country;
}
