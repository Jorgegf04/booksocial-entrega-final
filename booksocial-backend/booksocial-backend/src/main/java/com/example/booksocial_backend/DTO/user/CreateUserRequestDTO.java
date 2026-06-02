package com.example.booksocial_backend.DTO.user;

import com.example.booksocial_backend.domain.user.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creación de usuarios.
 *
 * Contiene los datos necesarios para registrar un nuevo usuario
 * en el sistema, incluyendo credenciales y rol.
 *
 * Utilizado en peticiones POST de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    /**
     * Nombre de usuario asociado al recurso.
     */
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    /**
     * Contrasena enviada para autenticacion o registro.
     */
    @NotBlank
    @Size(min = 6)
    private String password;
    /**
     * Correo electronico asociado al usuario o pedido invitado.
     */
    @NotBlank
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
     * Rol asignado al usuario dentro de la aplicacion.
     */
    @NotNull
    private Role role;

}