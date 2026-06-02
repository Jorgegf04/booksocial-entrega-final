package com.example.booksocial_backend.domain.social;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.booksocial_backend.domain.user.User;

/**
 * Evento exclusivo para usuarios suscritos dentro de la plataforma.
 *
 * La entidad se persiste en EVENT y se relaciona con usuarios mediante una
 * tabla
 * intermedia que registra las inscripciones o participantes del evento.
 *
 * @author Jorge
 * @since 15/03/2026
 * @version 1.0
 */

@Entity
@Table(name = "EVENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

  /**
   * ID único del evento.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Nombre del evento.
   * Tiene un tamaño maximo de 150 caracteres
   * La columana no puede ser falsa y con una longitud de 150 caracteres
   */
  @NotBlank
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String title;

  /**
   * Descripción del evento.
   * Tamaño maximo de 1000 caracteres
   * La columna solo puede ser de 1000 caracteres
   */
  @Size(max = 1000)
  @Column(length = 1000)
  private String description;

  /**
   * Imagen representativa del evento.
   * Tamaño maximo de 500 caracteres
   * La columana solo puede tener 500 caracteres
   */
  @Size(max = 500)
  @Column(length = 500)
  private String img;

  /**
   * Fecha del evento.
   * No puedes nulo y tiene que ser una fecha futura
   */
  @NotNull
  @Future
  private LocalDateTime date;

  /**
   * Usuarios asociados al evento.
   * Relación muchos a muchos gestionada con la tabla intermedia USER_EVENT. Un
   * evento puede tener varios usuarios y un usuario puede participar en varios
   * eventos.
   */
  @Builder.Default
  @ToString.Exclude
  @ManyToMany
  @JoinTable(name = "USER_EVENT", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> users = new ArrayList<>();
}
