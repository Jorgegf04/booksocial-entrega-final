package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de autores.
 *
 * Contiene los datos necesarios para registrar o modificar un autor
 * dentro de la aplicación.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDTO {
    /**
     * Nombre principal del autor en la solicitud.
     * El campo es obligatorio y un maximo de 150
     */
    @NotBlank(message = "El nombre del autor es obligatorio")
    @Size(max = 150)
    private String name;
    /**
     * Nacionalidad registrada para el autor en la solicitud.
     * Tiene una capacidad de 100 caracteres
     */
    @Size(max = 100)
    private String nationality;
    /**
     * Fecha de nacimiento del autor en la solicitud.
     */
    private LocalDate birthDate;
    /**
     * Referencia o URL de la imagen asociada en la solicitud.
     * La imagen tiene un limitador de capacidad
     */
    @Size(max = 500)
    private String img;
    /**
     * Identificadores de las obras asociadas al autor.
     */
    private List<Long> workIds;
}