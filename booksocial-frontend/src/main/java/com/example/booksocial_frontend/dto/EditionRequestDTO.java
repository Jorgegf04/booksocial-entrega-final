package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de edicion desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditionRequestDTO {
    /** Codigo ISBN de la edicion. */
    private String isbn;
    /** Fecha de publicacion de la edicion. */
    private LocalDate editionDate;
    /** Identificador de la obra relacionada. */
    private Long workId;
    /** Identificador de la editorial relacionada. */
    private Long editorialId;
    /** Titulo que se muestra en la vista. */
    private String title;
    /** Numero total de tomos de la edicion. */
    private Integer totalTomes;
}
