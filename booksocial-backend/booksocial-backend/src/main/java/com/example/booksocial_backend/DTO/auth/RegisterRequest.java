package com.example.booksocial_backend.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO utilizado para el registro de nuevos usuarios.
 *
 * Contiene los datos básicos necesarios para crear
 * una nueva cuenta en el sistema.
 *
 * @author Jorge
 * @since 2026
 */
@Data
public class RegisterRequest {
  /**
   * Nombre de usuario asociado a la solicutud.
   */
  @NotBlank(message = "El nombre de usuario es obligatorio")
  @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
  private String username;
  /**
   * Correo electronico asociado al usuario o pedido invitado.
   */
  @NotBlank(message = "El correo electronico es obligatorio")
  @Email(message = "El correo electronico no tiene un formato valido")
  @Size(max = 100, message = "El correo electronico no puede superar 100 caracteres")
  private String email;
  /**
   * Contrasena enviada para autenticacion o registro.
   */
  @NotBlank(message = "La contrasena es obligatoria")
  @Size(min = 6, max = 100, message = "La contrasena debe tener entre 6 y 100 caracteres")
  private String password;
  /**
   * Nombre del usuario que soclicaita la solicutud.
   */
  @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
  private String name;
  /**
   * Apellidos o segundo nombre del usuario.
   */
  @Size(max = 50, message = "El apellido no puede superar 50 caracteres")
  private String secondName;
  /**
   * Referencia o URL de la imagen asociada a la solicitud.
   */
  @Size(max = 500, message = "La URL de avatar no puede superar 500 caracteres")
  private String img;
}
