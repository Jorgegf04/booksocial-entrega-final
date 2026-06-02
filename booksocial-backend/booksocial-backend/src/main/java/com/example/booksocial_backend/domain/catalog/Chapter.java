package com.example.booksocial_backend.domain.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad que representa un capítulo de una obra tipo manga.
 * Los capítulos se agrupan dentro de tomos.
 * La restricción de tabla evita repetir el mismo número de capítulo dentro de
 * un mismo tomo.
 *
 * @author Jorge
 * @since 12/03/2026
 */

@Entity
@Table(name = "CHAPTER", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "chapterNumber", "tome_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

  /**
   * ID único del capítulo.
   * El ID es autogenerado
   */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Número del capítulo.
   * Tiene que ser un numero positoivo
   * La columna en la base de datos es debe no acepta nulos
   */
  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer chapterNumber;

  /**
   * Título del capítulo.
   */
  @Size(max = 200)
  @Column(length = 200)
  private String title;

  /**
   * Tomo al que pertenece el capítulo.
   *
   * <p>
   * Relación muchos a uno obligatoria. El tomo es la entidad padre que ordena y
   * agrupa los capítulos.
   * <p>
   * 
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tome_id", nullable = false)
  private Tome tome;
}
