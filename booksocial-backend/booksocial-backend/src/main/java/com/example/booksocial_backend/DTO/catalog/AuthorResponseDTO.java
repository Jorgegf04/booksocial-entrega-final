package com.example.booksocial_backend.DTO.catalog;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para autores.
 *
 * Coentiene los datos de un autor junto con la información extra como puede la
 * cantidad de seguidores del autor y obras que tienen
 *
 * 
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseDTO {
    /**
     * Identificador unico de autor en la respuesta.
     */
    private Long id;
    /**
     * Nombre del autor en la respuesta.
     */
    private String name;
    /**
     * Nacionalidad del autor en la respuesta
     */
    private String nationality;
    /**
     * Fecha de nacimiento del autor en la respueta
     */
    private LocalDate birthDate;
    /**
     * Referencia o URL de la imagen asociada al autor en la respuesta.
     */
    private String img;
    /**
     * Numero total de seguidores del autor en la respuesta.
     */
    private Long followerCount;
    /**
     * Listado de obras asociadas al autor en la respuesta.
     */
    private List<WorkResponseDTO> works;
}
