package com.example.booksocial_backend.DTO.auth;

import lombok.Data;

/**
 * DTO utilizado para la autenticación de usuarios.
 *
 * Contiene las credenciales necesarias para iniciar sesión
 * en el aplicación (username y password).
 *
 * @author Jorge
 * @since 2026
 */
@Data
public class LoginRequest {
  /**
   * Nombre de usuario asociado a la solicitud.
   */
  private String username;
  /**
   * Contraseña enviada para autenticacion o registro.
   */
  private String password;
}