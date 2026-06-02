package com.example.booksocial_backend.DTO.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta cuando la autenticación es correcta.
 *
 * Contiene el token JWT generado que el cliente
 * deberá enviar en futuras peticiones.
 *
 * @author Jorge
 * @since 2026
 */
@Data
@AllArgsConstructor
public class JwtResponse {
  /**
   * Token JWT emitido para autenticar los siguientes inicios de sesión.
   */
  private String token;
  /**
   * Tipo funcional del recurso dentro del sistema.
   */
  private String type;
  /**
   * Identificador del usuario .
   */
  private Long userId;
  /**
   * Nombre de usuario asociado al petición.
   */
  private String username;
  /**
   * Rol asignado al usuario dentro de la aplicacion.
   */
  private String role;
}