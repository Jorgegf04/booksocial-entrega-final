package com.example.booksocial_backend.DTO.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad Subscription.
 *
 * Representa la información de una suscripción de usuario,
 * incluyendo su estado, fechas y relación con el usuario.
 *
 * Utilizado en respuestas de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {
    /**
     * Identificador unico del recurso.
     */
    private Long id;
    /**
     * Fecha de inicio de la suscripcion.
     */
    private LocalDate startDate;
    /**
     * Fecha de finalizacion de la suscripcion.
     */
    private LocalDate endDate;
    /**
     * Indica si la suscripcion esta activa.
     */
    private Boolean activated;
    /**
     * Identificador del usuario asociado.
     */
    private Long userId;

}