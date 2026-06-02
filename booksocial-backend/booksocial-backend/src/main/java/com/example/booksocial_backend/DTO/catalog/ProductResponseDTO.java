package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Product.
 *
 * Representa la respuesta de un producto comercializable dentro de la
 * aplicación
 * Evita exponer directamente la entidad Edition.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    /**
     * ID único del producto en la respuesta.
     */
    private Long id;
    /**
     * Precio unitario del producto en la respuesta.
     */
    private Double price;
    /**
     * Cantidad de unidades disponibles del producto en la respuesta.
     */
    private Integer stock;

    // A partir de aquí es la información relacinada del producto con una edición
    /**
     * ID de la edicion asociada a un producto dentro de la respuesta.
     */
    private Long editionId;
    /**
     * Titulo de la edicion asociada a un producto en la respuesta.
     */
    private String editionTitle;
    /**
     * ISBN de la edicion asociada a un producto en la respuesta.
     */
    private String editionIsbn;
    /**
     * Numero total de tomos de un edición en la solicitud en el caso de que sea un
     * manga.
     */
    private Integer totalTomes;

    // A partir de aquí es la información relacionada del producto con una obra
    /**
     * Identificador de la obra en la respuesta.
     */
    private Long workId;
    /**
     * Titulo de la obra .
     */
    private String workTitle;

    // A partir de aqui es la información relacionada del producto con una editorial
    /**
     * Nombre de la editorial asociada.
     */
    private String editorialName;
}