package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Volume.
 * Representa la respuesta de un volumen dentro de una edición
 * Incluye únicamente el identificador de la edición asociada.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeResponseDTO {
    /**
     * ID unico del volumen en el volumen en la respuesta.
     */
    private Long id;
    /**
     * Numero del volumen dentro de la edicion en la respuesta.
     */
    private Integer volumeNumber;
    /**
     * Titulo del volumen en la respuesta.
     */
    private String title;
    /**
     * ID de la edicion asociada a un volumen en la respuesta.
     */
    private Long editionId;
    /**
     * ISBN de la edicion asociada a un volumen en la respuesta.
     */
    private String editionIsbn;
    /**
     * Titulo de la obra asociada a un volumen en la respuesta.
     */
    private String workTitle;
}