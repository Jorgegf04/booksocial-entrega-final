package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de usuario desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class UserResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
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
  /** Fecha de registro del usuario. */
  private LocalDate registrationDate;
  /** Indica si el usuario esta activo. */
  private Boolean active;
  /** Rol que tiene el usuario dentro de la aplicacion. */
  private String role;
}
