package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de evento desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    /** Titulo que se muestra en la vista. */
    private String title;
    /** Descripcion breve del elemento. */
    private String description;
    /** Ruta o URL de la imagen asociada. */
    private String img;
    /** Fecha en la que se creo o registro el dato. */
    private LocalDateTime date;
    /** Identificadores de usuarios relacionados. */
    private List<Long> userIds;
}
