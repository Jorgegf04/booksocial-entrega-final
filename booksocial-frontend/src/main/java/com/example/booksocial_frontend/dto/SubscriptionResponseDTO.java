package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de suscripcion desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class SubscriptionResponseDTO {
    /** Identificador unico del registro. */
    private Long id;
    /** Fecha de inicio de la suscripcion. */
    private LocalDate startDate;
    /** Fecha de fin de la suscripcion. */
    private LocalDate endDate;
    /** Indica si la suscripcion esta activa. */
    private Boolean activated;
    /** Identificador del usuario relacionado. */
    private Long userId;
}
