package com.example.booksocial_backend.domain.social;

import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un comentario publicado por un usuario
 * sobre una obra dentro del sistema.
 *
 * Los comentarios pueden responder a otros comentarios, por lo que la entidad
 * contiene una relación jerárquica consigo misma.
 *
 * @author Jorge
 * @since 16/03/2026
 * @version 1.0
 */

@Entity
@Table(name = "COMMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

  /**
   * ID del comentario.
   * El id es autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Contenido de texto del comentario.
   * Tiene un maximo de 1000 caracteres
   * La columna no puede ser nula y tiene una capacidad de 1000 caracteres
   */
  @NotBlank
  @Size(max = 1000)
  @Column(nullable = false, length = 1000)
  private String content;

  /**
   * Fecha del comentario.
   * Puede ser actual o pasada
   * La columna no puede ser nula
   */
  @NotNull
  @PastOrPresent
  @Column(nullable = false)
  private LocalDateTime date;

  /**
   * Fecha de la última edición del comentario.
   *
   * Permanece a {@code null} si el comentario no se ha modificado después de su
   * creación.
   */
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Indica si el comentario fue editado después de publicarse.
   * La columna no puede ser falsa
   */
  @Builder.Default
  @Column(nullable = false)
  private Boolean edited = false;

  /**
   * Usuario que publica el comentario.
   * Relación muchos a uno obligatoria: un usuario puede publicar muchos
   * comentarios.
   * No puede ser nulo
   * La columana no puede ser nulo
   */
  @ToString.Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  /**
   * Obra sobre la que se publica el comentario.
   * Relación muchos a uno obligatoria: una obra puede recibir muchos comentarios.
   */
  @ToString.Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Work work;

  /**
   * Comentario padre al que responde este comentario.
   */
  @JsonIgnore
  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  /**
   * Respuestas directas asociadas a este comentario.
   * Relación uno a muchos inversa del campo {@link #parent}. La cascada mantiene
   * las respuestas ligadas al comentario padre.
   */
  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Comment> replies = new ArrayList<>();

  /**
   * Reacciones recibidas por este comentario.
   * Relación uno a muchos con {@link Reaction}. Cada reacción pertenece a un
   * usuario y a este comentario.
   */
  @Builder.Default
  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<Reaction> reactions = new ArrayList<>();

}
