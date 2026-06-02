package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;
import java.util.List;

import com.example.booksocial_backend.domain.catalog.Demographic;
import com.example.booksocial_backend.domain.catalog.Genre;
import com.example.booksocial_backend.domain.catalog.WorkType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de obras.
 * Contiene los datos necesarios para registrar o modificar una obra
 * dentro del sistema, incluyendo los autores asociados mediante
 * sus identificadores.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequestDTO {
    /**
     * Titulo de la obra en la solicitud.
     * Es un campo obligatorio y tiene que tener 255 caracteres
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255)
    private String title;
    /**
     * Sinopsis de la obra en la solicitud.
     * Tiene una restrcción de 2000 caracteres
     */
    @Size(max = 2000)
    private String description;
    /**
     * Genero de la obra en la solicitud.
     * No puede ser nulo
     */
    @NotNull(message = "El género es obligatorio")
    private Genre genre;
    /**
     * Tipo de obra que es en la solitud.
     * No puede ser nulos
     */
    @NotNull(message = "El tipo de obra es obligatorio")
    private WorkType type;
    /**
     * Demografia o publico objetivo de la obra en la solicitud.
     */
    private Demographic demographic;
    /**
     * Fecha de publicacion de la obra en la solicitud.
     */
    private LocalDate publicationDate;
    /**
     * Referencia o URL de la imagen asociada a la obra en la solicitud
     * Tiene un maximo de 500
     */
    @Size(max = 500)
    private String img;
    /**
     * Nota asociada a la obra en la solicutud.
     */
    private Double averageRating;
    /**
     * Nombres de los autores asociados a la obra en la solicitud.
     */
    private List<String> authors;
}