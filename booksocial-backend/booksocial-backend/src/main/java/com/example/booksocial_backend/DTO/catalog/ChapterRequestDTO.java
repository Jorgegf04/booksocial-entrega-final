package com.example.booksocial_backend.DTO.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de capítulos.
 *
 * Solo contiene la información necesaria para persistir un capítulo dentro de
 * un tomo concreto, ya que el modelo de datos especifica que los tomos son
 * compuestos por capitulos
 * dentro de un tomo concreto.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequestDTO {
  /**
   * Numero del capitulo dentro del tomo en la solicitud.
   * El numero del capitulo es obligatorio y tiene que ser positivo
   */
  @NotNull(message = "El número de capítulo es obligatorio")
  @Positive
  private Integer chapterNumber;
  /**
   * Titulo del capitulo dentro del tomo en la solicitud. 
   * Tiene 200 caractares como maximo
   */
  @Size(max = 200)
  private String title;
  /**
   * Identificador del tomo asociado a un capítulo dentro de la solicitud.
   * El ID del tomo al que pertenece tiene que ser obligatorio.
   */
  @NotNull(message = "El capítulo debe estar asociado a un tomo")
  private Long tomeId;
}