package com.example.booksocial_backend.DTO.commerce;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación de pedidos.
 *
 * Soporta dos modos:
 * - Usuario registrado: proporciona userId.
 * - Invitado: proporciona guestEmail (sin userId).
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    /**
     * ID del usuario registrado. Null si el comprador es invitado en la solicitud.
     */
    private Long userId;

    /**
     * Email del comprador invitado. Requerido si userId es null en la solicutud.
     * Tiene que ser un gmail
     */
    @Email
    private String guestEmail;
    /**
     * Lineas de pedido incluidas en la compra para hacer la solicitud.
     */
    @NotEmpty
    private List<OrderLineRequestDTO> orderLines;

}
