package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de editoriales.
 *
 * Contiene los datos necesarios para registrar o modificar una editorial
 * dentro de la aplicación.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorialRequestDTO {
    /**
     * Nombre de la editorial en la solicitud.
     */
    private String name;
    /**
     * Pais de la editorial en la solicitud.
     */
    private String country;
}