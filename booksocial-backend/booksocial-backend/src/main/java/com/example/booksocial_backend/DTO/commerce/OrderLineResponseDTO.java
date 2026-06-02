package com.example.booksocial_backend.DTO.commerce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa una línea de pedido.
 *
 * Contiene la información de los productos incluidos en un pedido,
 * como el identificador del producto, la cantidad solicitada y el precio
 * unitario.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineResponseDTO {
    /**
     * ID del producto asociado a la linea de pedido en la respuesta.
     */
    private Long productId;
    /**
     * Titulo de la obra asociado a linea de pedido en la respuesta.
     */
    private String title;
    /**
     * Precio unitario del producto en la respuesta.
     */
    private Double price;
    /**
     * Cantidad solicitada del producto en la respuesta.
     */
    private Integer quantity;
    /**
     * Importe total de la linea calculado a partir de precio y cantidad y que se
     * muestra en la respuesta.
     */
    private Double subtotal;

}