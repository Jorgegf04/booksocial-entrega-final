package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Editorial.
 * Representa la respuesta de una editorial en el sistema
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorialResponseDTO {
    /**
     * ID de la editorial en la respuesta.
     */
    private Long id;
    /**
     * Nombre de la editorial en la respuesta.
     */
    private String name;
    /**
     * Pais de donde es la editorial en la respuesta.
     */
    private String country;
    /**
     * Numero total de ediciones que tiene un editorial.
     */
    private Integer totalEditions;
}