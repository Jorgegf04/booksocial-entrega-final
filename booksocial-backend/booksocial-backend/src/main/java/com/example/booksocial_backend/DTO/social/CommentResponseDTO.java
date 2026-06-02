package com.example.booksocial_backend.DTO.social;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para comentarios.
 *
 * Representa un comentario con sus respuestas directas,
 * evitando recursividad infinita mediante una estructura controlada.
 *
 * Incluye información del usuario, la obra asociada y
 * la jerarquía de comentarios.
 *
 * Este DTO es utilizado en las respuestas de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    /**
     * Identificador unico del recurso.
     */
    private Long id;
    /**
     * Contenido textual del comentario.
     */
    private String content;
    /**
     * Fecha y hora asociada al evento o registro.
     */
    private LocalDateTime date;
    /**
     * Fecha y hora de la ultima actualizacion del comentario.
     */
    private LocalDateTime updatedAt;
    /**
     * Indica si el comentario fue editado tras su creacion.
     */
    private Boolean edited;
    /**
     * Identificador del usuario asociado.
     */
    private Long userId;
    /**
     * Nombre de usuario asociado al recurso.
     */
    private String username;
    /**
     * Identificador de la obra asociada.
     */
    private Long workId;
    /**
     * Titulo de la obra asociada.
     */
    private String workTitle;
    /**
     * Identificador del comentario padre, si se trata de una respuesta.
     */
    private Long parentId;
    /**
     * Listado de respuestas asociadas al comentario.
     */
    private List<CommentResponseDTO> replies;
}