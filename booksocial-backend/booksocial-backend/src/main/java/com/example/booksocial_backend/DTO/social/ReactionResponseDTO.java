package com.example.booksocial_backend.DTO.social;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Reaction.
 *
 * Representa una reacción (like) de un usuario sobre un comentario,
 * incluyendo la fecha en la que se realizó.
 *
 * Este DTO es utilizado en las respuestas de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseDTO {
    /**
     * Identificador unico del recurso.
     */
    private Long id;
    /**
     * Fecha y hora asociada al evento o registro.
     */
    private LocalDateTime date;
    /**
     * Identificador del usuario asociado.
     */
    private Long userId;
    /**
     * Nombre de usuario asociado al recurso.
     */
    private String username;
    /**
     * Identificador del comentario asociado.
     */
    private Long commentId;
    /**
     * Indica si el usuario mantiene activa la reaccion.
     */
    private Boolean liked;
}