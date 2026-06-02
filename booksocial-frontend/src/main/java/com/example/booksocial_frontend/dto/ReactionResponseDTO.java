package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de reaccion desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class ReactionResponseDTO {
    /** Identificador unico del registro. */
    private Long id;
    /** Fecha en la que se creo o registro el dato. */
    private LocalDateTime date;
    /** Identificador del usuario relacionado. */
    private Long userId;
    /** Nombre de usuario usado para mostrar o iniciar sesion. */
    private String username;
    /** Dato de comment id usado por el DTO. */
    private Long commentId;
    /** Indica si la reaccion es positiva. */
    private Boolean liked;
}
