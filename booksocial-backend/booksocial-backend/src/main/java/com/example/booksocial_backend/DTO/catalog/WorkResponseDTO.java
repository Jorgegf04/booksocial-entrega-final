package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;
import java.util.List;

import com.example.booksocial_backend.domain.catalog.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta que representa una obra del catálogo.
 * Contiene la información necesaria para la respuesta en la aplicación
 * 
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkResponseDTO {
    /**
     * ID unico de la obra en la respuesta.
     */
    private Long id;
    /**
     * Titulo de la obra en la respuesta.
     */
    private String title;
    /**
     * Sinopsis de la obra en la respuesta.
     */
    private String description;
    /**
     * Genero de la obra dentro del aplicacion en la respuesta.
     */
    private Genre genre;
    /**
     * Tipo de obra dento de la aplicación en la respuesta.
     */
    private WorkType type;
    /**
     * Demografia o publico objetivo de la obra en la respuesta.
     */
    private Demographic demographic;
    /**
     * Fecha de publicacion de la obra para la respuesta.
     */
    private LocalDate publicationDate;
    /**
     * Referencia o URL de la imagen asociada a la obra en la respuesta.
     */
    private String img;
    /**
     * Valoracion media asociada a la obra en la respuesta.
     */
    private Double averageRating;
    /**
     * Nombres de los autores asociados a la obra en la respuesta.
     */
    private List<String> authors;
}