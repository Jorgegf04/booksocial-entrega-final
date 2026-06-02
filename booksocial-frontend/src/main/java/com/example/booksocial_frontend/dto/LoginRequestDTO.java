package com.example.booksocial_frontend.dto;

import lombok.Data;

// dto/auth/LoginRequestDTO
/**
 * DTO de request para enviar datos de inicio de sesion desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
public class LoginRequestDTO {
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Contrasena enviada en formularios de acceso. */
  private String password;
}
