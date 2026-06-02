package com.example.booksocial_frontend.dto;

import lombok.Data;

// dto/auth/LoginRequestDTO

/**
 * DTO de response para recibir datos de respuesta de login desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
public class JwtResponseDTO {
  /** Token JWT recibido al iniciar sesion. */
  private String token;
  /** Tipo o categoria usada por la obra o el token. */
  private String type;
  /** Identificador del usuario relacionado. */
  private Long userId;
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Rol que tiene el usuario dentro de la aplicacion. */
  private String role;
}