package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Edition.
 *
 * Representa una edición de una obra con los datos necesarios para poder
 * representarla dentro de la aplicación
 * Incluye únicamente los identificadores de las entidades relacionadas.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditionResponseDTO {
    /**
     * ID del la edicion en la respuesta.
     */
    private Long id;
    /**
     * Codigo ISBN de la edicion en la respuesta.
     */
    private String isbn;
    /**
     * Fecha de publicacion de la edicion de la respuesta.
     */
    private LocalDate editionDate;
    /**
     * Titulo de la edicion en la respuesta.
     */
    private String title;
    /**
     * Numero total de tomos en la respuesta.
     */
    private Integer totalTomes;
    /**
     * ID de la obra asociado a una edición en la respuesta.
     */
    private Long workId;
    /**
     * Titulo de la obra asocido a una edición en la respuesta.
     */
    private String workTitle;
    /**
     * ID de la editorial asociada a una edición en la respuesta.
     */
    private Long editorialId;
    /**
     * Nombre de la editorial asociada a una edición en la respuesta.
     */
    private String editorialName;
}