package com.example.booksocial_backend.DTO.commerce;

import java.time.LocalDateTime;

import com.example.booksocial_backend.domain.commerce.TrackingOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Tracking.
 * Representa el estado logístico de un pedido en un momento concreto,
 * incluyendo el identificador del pedido asociado y la fecha del cambio.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingOrderResponseDTO {
    /**
     * ID unico del tracking en la respuesta.
     */
    private Long id;
    /**
     * Estado actual del seguimiento en la respuesta.
     */
    private TrackingOrderStatus status;
    /**
     * Texto legible que representa el estado actual en la respuesta.
     */
    private String statusLabel;
    /**
     * Fecha y hora asociada al evento o registro.
     */
    private LocalDateTime date;
    /**
     * Identificador del pedido asociado.
     */
    private Long orderId;
    /**
     * Identificador del usuario asociado.
     */
    private Long userId;
    /**
     * Nombre de usuario asociado al recurso.
     */
    private String username;
    /**
     * Indica si el seguimiento ha alcanzado un estado final.
     */
    private Boolean completed;
}