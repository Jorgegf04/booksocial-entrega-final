package com.example.booksocial_backend.domain.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un tomo dentro de una edición de manga.
 * Un tomo agrupa varios capítulos de una obra.
 * Se persiste en la tabla TOME y pertenece siempre a una edición concreta.
 *
 * @author Jorge
 * @since 12/03/2026
 */

@Entity
@Table(name = "TOME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tome {

  /**
   * ID único del tomo.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Número del tomo dentro de la colección.
   * Teien que ser un numero positivo
   * La columana no puede ser nula
   */
  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer numberTome;

  /**
   * Título del tomo (opcional).
   * Tiene como maximo 150 caractares
   */
  @Size(max = 150)
  @Column(length = 150)
  private String title;

  /**
   * Edición a la que pertenece el tomo.
   * Relación muchos a uno obligatoria. La edición es la entidad padre que agrupa
   * todos los tomos publicados bajo una misma publicación
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "edition_id", nullable = false)
  private Edition edition;

  /**
   * Capítulos incluidos en este tomo.
   * Relación uno a muchos con {@link Chapter}. La eliminación en cascada mantiene
   * sincronizados los capítulos cuando se elimina o modifica un tomo.
   */
  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "tome", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Chapter> chapters = new ArrayList<>();
}
