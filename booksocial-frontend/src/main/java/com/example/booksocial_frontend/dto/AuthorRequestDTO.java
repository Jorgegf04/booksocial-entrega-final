package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para enviar datos de autor desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDTO {
    /** Nombre principal que se muestra en la vista. */
    private String name;
    /** Nacionalidad del autor. */
    private String nationality;
    /** Fecha de nacimiento del autor. */
    private LocalDate birthDate;
    /** Ruta o URL de la imagen asociada. */
    private String img;
    /** Identificadores de obras relacionadas. */
    private List<Long> workIds;
}
