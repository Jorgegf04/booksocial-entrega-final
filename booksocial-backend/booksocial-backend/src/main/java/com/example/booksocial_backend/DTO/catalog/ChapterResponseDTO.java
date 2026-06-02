package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Chapter.
 *
 * Representa la información que creo que es necesaria para cuando obtiene un
 * capitulo o varios
 *
 * 
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponseDTO {
    /**
     * Identificador del capitulo en la respuesta.
     */
    private Long id;
    /**
     * Numero del capitulo dentro del tomo en la respuesta.
     */
    private Integer chapterNumber;
    /**
     * Titulo del capitulo en la respuesta.
     */
    private String title;
    /**
     * Identificador del tomo asociado a un capitulo en la respuesta.
     */
    private Long tomeId;
    /**
     * Titulo del tomo asociado a un capitulo en la respuesta.
     */
    private String tomeTitle;
    /**
     * Nombre o titulo de la edicion asociada a un capitulo en la respuesta.
     */
    private String editionName;
}