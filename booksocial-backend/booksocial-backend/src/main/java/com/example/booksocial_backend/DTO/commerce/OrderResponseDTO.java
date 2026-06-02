package com.example.booksocial_backend.DTO.commerce;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Order.
 * Representa un pedido dentro del sistema de compra,
 * incluyendo sus líneas asociadas.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    /**
     * ID unico del pedido en la respuesta.
     */
    private Long id;
    /**
     * Fecha y hora asociada al evento o registro del pedido en el sistema y lo
     * muesta en la respuesta.
     */
    private LocalDateTime date;
    /**
     * Importe total del pedido en la respuesta.
     */
    private Double total;
    /**
     * Identificador del usuario asociado al pedido y que se muestra en en el
     * pedido.
     */
    private Long userId;
    /**
     * Nombre de usuario asociado al pedido en la respuesta.
     */
    private String username;
    /**
     * Correo electronico usado para pedidos de invitado en la respuesta.
     */
    private String guestEmail;
    /**
     * Numero total de articulos incluidos en el pedido en la respuesta.
     */
    private Integer totalItems;
    /**
     * Lineas de pedido incluidas en la compra en la respuesta.
     */
    private List<OrderLineResponseDTO> orderLines;
}