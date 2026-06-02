package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de obra desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class WorkResponseDTO {

    /** Identificador unico del registro. */
    private Long id;
    /** Titulo que se muestra en la vista. */
    private String title;
    /** Descripcion breve del elemento. */
    private String description;
    /** Genero principal de la obra. */
    private Genre genre;
    /** Tipo o categoria usada por la obra o el token. */
    private WorkType type;
    /** Demografia a la que va dirigida la obra. */
    private Demographic demographic;
    /** Fecha de publicacion de la obra. */
    private LocalDate publicationDate;
    /** Ruta o URL de la imagen asociada. */
    private String img;
    /** Valoracion media de la obra. */
    private Double averageRating;
    /** Autores relacionados con la obra. */
    private List<String> authors;
}
