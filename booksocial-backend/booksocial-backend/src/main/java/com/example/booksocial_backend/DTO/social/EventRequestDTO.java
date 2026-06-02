package com.example.booksocial_backend.DTO.social;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * DTO de entrada para la creación y actualización de eventos.
 *
 * Permite registrar o modificar un evento exclusivo dentro de la plataforma,
 * incluyendo los usuarios participantes mediante sus identificadores.
 *
 * Este DTO es utilizado en las peticiones de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    /**
     * Titulo visible del recurso.
     */
    @NotBlank
    private String title;
    /**
     * Descripcion textual del recurso.
     */
    private String description;
    /**
     * Referencia o URL de la imagen asociada al recurso.
     */
    private String img;
    /**
     * Fecha y hora asociada al evento o registro.
     */
    @NotNull
    private LocalDateTime date;
    /**
     * Identificadores de los usuarios asociados al evento.
     */
    private List<Long> userIds;
}