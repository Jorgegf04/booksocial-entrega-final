package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO de request para enviar datos de actualizacion de usuario desde el frontend al backend.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Correo electronico del usuario. */
  private String email;
  /** Nombre principal que se muestra en la vista. */
  private String name;
  /** Segundo nombre o apellido del usuario. */
  private String secondName;
  /** Ruta o URL de la imagen asociada. */
  private String img;
  /** Rol que tiene el usuario dentro de la aplicacion. */
  private String role;
  /** Indica si el usuario esta activo. */
  private Boolean active;
}
