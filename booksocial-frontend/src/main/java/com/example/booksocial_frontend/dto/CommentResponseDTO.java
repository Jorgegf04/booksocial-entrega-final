package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de comentario desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class CommentResponseDTO {

  /** Identificador unico del registro. */
  private Long id;
  /** Texto principal del comentario. */
  private String content;
  /** Fecha en la que se creo o registro el dato. */
  private LocalDateTime date;
  /** Fecha de la ultima modificacion. */
  private LocalDateTime updatedAt;
  /** Indica si el comentario fue editado. */
  private Boolean edited;
  /** Identificador del usuario relacionado. */
  private Long userId;
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Identificador de la obra relacionada. */
  private Long workId;
  /** Titulo de la obra relacionada. */
  private String workTitle;
  /** Identificador del comentario padre si es una respuesta. */
  private Long parentId;
  /** Respuestas asociadas al comentario. */
  private List<CommentResponseDTO> replies;
}
