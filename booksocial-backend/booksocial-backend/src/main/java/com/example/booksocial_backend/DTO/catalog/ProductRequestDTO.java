package com.example.booksocial_backend.DTO.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de productos.
 *
 * Contiene los datos necesarios para registrar o modificar un producto
 * dentro del sistema de compra.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    /**
     * Precio unitario del producto en la solicitud.
     */
    private Double price;
    /**
     * Cantidad de unidades disponibles del producto en la solicitud .
     */
    private Integer stock;
    /**
     * ID de la edicion asociada en la solicitud.
     */
    private Long editionId;
}