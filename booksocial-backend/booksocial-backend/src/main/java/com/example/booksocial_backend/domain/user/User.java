package com.example.booksocial_backend.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.booksocial_backend.domain.social.TrackingWork;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad que representa a un usuario registrado en la plataforma BookSocial.
 *
 * Un usuario puede realizar compras, publicar comentarios, reaccionar a
 * contenido,
 * seguir obras o seguir a otros usuarios. Además puede disponer de una
 * suscripción
 * activa que le proporciona beneficios adicionales dentro de la plataforma.
 *
 * El usuario invitado no se persiste en base de datos; su acceso se gestiona
 * mediante las reglas de autorización de Spring Security.
 *
 * Esta clase se mapea contra la tabla APPUSER para evitar conflictos con
 * palabras reservadas habituales en bases de datos.
 * 
 * @author Jorge
 * @since 12/03/2026
 * @version 1.0
 */
@Entity
@Table(name = "APPUSER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  /**
   * ID único del usuario generado automáticamente por la base de
   * datos.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Nombre de usuario utilizado para autenticarse en el sistema.
   * No puede ser nulo
   * El tamaña es entre 3 y 50 caractares
   * la columna no puede ser nula, es unico y una cantidad de 50 caracteres por
   * los menos
   */
  @NotBlank
  @Size(min = 3, max = 50)
  @Column(nullable = false, unique = true, length = 50)
  // @Pattern(regexp = "^[a-zA-Z0-9_]+$")
  private String username;

  /**
   * Contraseña del usuario almacenada de forma encriptada con BCrypt.
   * Nunca se devuelve en las respuestas de la API.
   * No puede ser nulo y tiene que tener entre 6 y 100 caracteres
   */
  @NotBlank
  @Size(min = 6, max = 100)
  @Column(nullable = false)
  private String password;

  /**
   * Dirección de correo electrónico única del usuario.
   * No puede ser nulo, tiene que ser un gmail, y tiene 100 caracteres como mucho
   * La columana no puede falsa, debe ser unica y como mucho 100 caracteres
   */
  @NotBlank
  @Email
  @Size(max = 100)
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  /**
   * Nombre real del usuario.
   * El campo tiene una restricción de 50 caracteres
   * La columana en la base de datos solo puede tener 50 caractares
   */
  @Size(max = 50)
  @Column(length = 50)
  private String name;

  /**
   * Apellido o segundo nombre del usuario.
   * Tiene un tamaña de 100 caracteres
   * La columana tiene una restricción de 100 caracteres
   */

  @Size(max = 100)
  @Column(name = "second_name", length = 100)
  private String secondName;

  /**
   * Ruta o URL de la imagen de perfil del usuario.
   * La columana solo puede tener 500 caracteres
   */
  @Column(length = 500)
  private String img;

  /**
   * Fecha en la que el usuario se registró en la plataforma.
   * Se asigna automáticamente en el backend al crear el usuario.
   */
  @NotNull
  @PastOrPresent
  @Column(name = "registration_date", nullable = false)
  private LocalDate registrationDate;

  /**
   * Indica si la cuenta del usuario se encuentra activa.
   * Un usuario inactivo no puede iniciar sesión.
   * No puede ser falso
   * La columna no puede ser falsa
   */
  @NotNull
  @Builder.Default
  @Column(nullable = false)
  private Boolean active = true;

  /**
   * Rol del usuario dentro del sistema.
   * Determina los permisos y funcionalidades disponibles.
   * No puede ser nulo
   * La columana no puede ser nula y con un tamaña de 20 caracteres
   *
   * @see Role
   */

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  /**
   * Suscripción activa del usuario.
   * Relación uno a uno opcional: no todos los usuarios tienen suscripción. El
   * campo {@code mappedBy = "user"} indica que la clave foránea vive en
   * {@link Subscription}.
   */
  @ToString.Exclude
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Subscription subscription;

  /**
   * Lista de obras seguidas por el usuario.
   * Representa la relación entre el usuario y las obras que sigue,
   * incluyendo el estado de seguimiento (pendiente, leyendo, completada, etc).
   * La relacion es de User y trackingWokr que es de uno a muchos
   * Se utiliza carga perezosa (LAZY) para optimizar el rendimiento y evitar
   * cargar innecesariamente los seguimientos.
   */
  @Builder.Default
  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<TrackingWork> trackingWorks = new ArrayList<>();

  /**
   * Relaciones en las que este usuario actúa como seguidor.
   * Cada elemento representa a otro usuario seguido por esta cuenta. La entidad
   * intermedia {@link UserFollow} guarda la fecha del seguimiento y evita
   * duplicados.
   */
  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "follower")
  private List<UserFollow> following = new ArrayList<>();

  /**
   * Relaciones en las que este usuario es seguido por otros usuarios.
   * Es el lado inverso de los seguimientos de usuarios y permite consultar la
   * comunidad que sigue a esta cuenta.
   */
  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "following")
  private List<UserFollow> followers = new ArrayList<>();
}
