package com.example.booksocial_backend.DTO.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de tomos.
 * Contiene los datos necesarios para registrar o modificar un tomo
 * dentro de una edición concreta.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TomeRequestDTO {
    /**
     * Numero del tomo dentro de la edicion en la solicitud.
     * No puede ser nulo y tiene que ser un numero positivo
     */
    @NotNull(message = "El número de tomo es obligatorio")
    @Positive
    private Integer numberTome;
    /**
     * Identificador de la edicion asociada en la respuesta.
     * No puede ser nulo
     */
    @NotNull(message = "El tomo debe estar asociado a una edición")
    private Long editionId;
    /**
     * Titulo visible del recurso.
     * Tiene una restricción de 150 caractares
     */
    @Size(max = 150)
    private String title;
}