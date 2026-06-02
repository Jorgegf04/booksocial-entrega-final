package com.example.booksocial_backend.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para actualizar un usuario.
 *
 * Permite modificar los datos básicos del perfil del usuario.
 *
 * Utilizado en peticiones PUT de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
    /**
     * Nombre de usuario asociado al recurso.
     */
    @Size(min = 3, max = 50)
    private String username;
    /**
     * Correo electronico asociado al usuario o pedido invitado.
     */
    @Email
    private String email;
    /**
     * Nombre principal del recurso.
     */
    @Size(max = 100)
    private String name;
    /**
     * Apellidos o segundo nombre del usuario.
     */
    @Size(max = 100)
    private String secondName;
    /**
     * Referencia o URL de la imagen asociada al recurso.
     */
    @Size(max = 500)
    private String img;
    /**
     * Rol asignado al usuario dentro de la aplicacion.
     */
    private String role;
    /**
     * Indica si el usuario se encuentra activo.
     */
    private Boolean active;

}