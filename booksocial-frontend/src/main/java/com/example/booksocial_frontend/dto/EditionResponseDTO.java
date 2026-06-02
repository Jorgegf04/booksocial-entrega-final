package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de edicion desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class EditionResponseDTO {

  /** Identificador unico del registro. */
  private Long id;
  /** Codigo ISBN de la edicion. */
  private String isbn;
  /** Fecha de publicacion de la edicion. */
  private LocalDate editionDate;
  /** Titulo que se muestra en la vista. */
  private String title;
  /** Numero total de tomos de la edicion. */
  private Integer totalTomes;
  /** Identificador de la obra relacionada. */
  private Long workId;
  /** Titulo de la obra relacionada. */
  private String workTitle;
  /** Identificador de la editorial relacionada. */
  private Long editorialId;
  /** Nombre de la editorial relacionada. */
  private String editorialName;
}
