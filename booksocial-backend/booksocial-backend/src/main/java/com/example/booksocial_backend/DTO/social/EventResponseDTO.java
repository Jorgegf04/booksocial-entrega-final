package com.example.booksocial_backend.DTO.social;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Event.
 *
 * Representa un evento exclusivo dentro de la plataforma,
 * incluyendo los usuarios participantes mediante sus identificadores.
 *
 * Este DTO es utilizado en las respuestas de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {
    /**
     * Identificador unico del recurso.
     */
    private Long id;
    /**
     * Titulo visible del recurso.
     */
    private String title;
    /**
     * Descripcion textual del recurso.
     */
    private String description;
    /**
     * Referencia o URL de la imagen asociada al recurso.
     */
    private String img;
    /**
     * Fecha y hora asociada al evento o registro.
     */
    private LocalDateTime date;
    /**
     * Identificadores de los usuarios asociados al evento.
     */
    private List<Long> userIds;
    /**
     * Nombres de usuario asociados al evento.
     */
    private List<String> usernames;
    /**
     * Numero total de participantes del evento.
     */
    private Integer totalParticipants;
}