package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de capitulo desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class ChapterResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Numero del capitulo dentro del tomo. */
  private Integer chapterNumber;
  /** Titulo que se muestra en la vista. */
  private String title;
  /** Dato de tome id usado por el DTO. */
  private Long tomeId;
  /** Dato de tome title usado por el DTO. */
  private String tomeTitle;
}
