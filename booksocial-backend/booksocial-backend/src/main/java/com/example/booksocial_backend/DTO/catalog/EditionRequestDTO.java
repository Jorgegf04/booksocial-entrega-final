package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de ediciones.
 *
 * Contiene los datos necesarios para registrar o modificar una edición,
 * incluyendo las referencias a la obra y la editorial.
 *
 * Este DTO es utilizado en las peticiones de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditionRequestDTO {
    /**
     * Codigo ISBN de la edicion.
     * El ISBM es obligatorio y ademas solo de 20 caracteres
     */
    @NotBlank(message = "El ISBN es obligatorio")
    @Size(max = 20)
    private String isbn;
    /**
     * Fecha de publicacion de la edicion.
     */
    private LocalDate editionDate;
    /**
     * Identificador de la obra asociada.
     * El identificador de la obra asociada es obligatorio
     */
    @NotNull(message = "La edición debe estar asociada a una obra")
    private Long workId;
    /**
     * Identificador de la editorial asociada.
     * El id de la editorial no puede ser nulo
     */
    @NotNull(message = "La edición debe estar asociada a una editorial")
    private Long editorialId;
    /**
     * Titulo visible del recurso.
     * Tiene un restricción de 255 caracteres
     */
    @Size(max = 255)
    private String title;
    /**
     * Numero total de tomos de la edición
     */
    private Integer totalTomes;
}