package com.example.booksocial_frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de producto desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    /** Precio del producto. */
    private Double price;
    /** Cantidad disponible en inventario. */
    private Integer stock;
    /** Identificador de la edicion relacionada. */
    private Long editionId;
}
