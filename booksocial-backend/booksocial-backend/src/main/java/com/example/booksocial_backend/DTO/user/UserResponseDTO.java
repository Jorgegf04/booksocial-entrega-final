package com.example.booksocial_backend.DTO.user;

import java.time.LocalDate;

import com.example.booksocial_backend.domain.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para la entidad User.
 *
 * Representa la información de un usuario dentro del sistema,
 * incluyendo sus datos personales, estado y rol.
 *
 * Utilizado en las respuestas de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    /**
     * Identificador unico del recurso.
     */
    private Long id;
    /**
     * Nombre de usuario asociado al recurso.
     */
    private String username;
    /**
     * Correo electronico asociado al usuario o pedido invitado.
     */
    private String email;
    /**
     * Nombre principal del recurso.
     */
    private String name;
    /**
     * Apellidos o segundo nombre del usuario.
     */
    private String secondName;
    /**
     * Referencia o URL de la imagen asociada al recurso.
     */
    private String img;
    /**
     * Fecha de registro del usuario.
     */
    private LocalDate registrationDate;
    /**
     * Indica si el usuario se encuentra activo.
     */
    private Boolean active;
    /**
     * Rol asignado al usuario dentro de la aplicacion.
     */
    private Role role;

}