package com.example.booksocial_backend.DTO.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de volúmenes.
 * Contiene los datos necesarios para registrar o modificar un volumen
 * dentro de una edición concreta.
 *
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeRequestDTO {
    /**
     * Numero del volumen dentro de la edicion en la solicitud.
     * No puede ser nulo y tiene que ser positivo
     */
    @NotNull(message = "El número de volumen es obligatorio")
    @Positive
    private Integer volumeNumber;
    /**
     * Titulo del volumen en la solicitud.
     * Tiene una restricción de 200 caracteres
     */
    @Size(max = 200)
    private String title;
    /**
     * ID de la edicion asociada al volumen.
     * No puede ser nulo.
     */
    @NotNull(message = "El volumen debe estar asociado a una edición")
    private Long editionId;
}