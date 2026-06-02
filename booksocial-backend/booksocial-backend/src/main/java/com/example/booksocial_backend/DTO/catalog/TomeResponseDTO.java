package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Tome.
 * Representa un tomo dentro de una edición dentro de la aplicación.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TomeResponseDTO {
    /**
     * ID unico del tomo en la respuesta.
     */
    private Long id;
    /**
     * Numero del tomo dentro de la edicion en la respuesta.
     */
    private Integer numberTome;
    /**
     * Titulo del tomo en la respuesta.
     */
    private String title;
    /**
     * ID de la edicion asociado a un tomo en la respuesta.
     */
    private Long editionId;
    /**
     * Titulo de la edicion asociada a un tomo en la respuesta.
     */
    private String editionTitle;
    /**
     * Titulo de la obra asociada a un tomo en la respuesta.
     */
    private String workTitle;
}