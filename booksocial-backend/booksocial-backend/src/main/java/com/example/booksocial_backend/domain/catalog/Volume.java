package com.example.booksocial_backend.domain.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad que representa un volumen o número de cómic.
 * Cada volumen pertenece a una edición concreta de una obra.
 * Se utiliza para obras tipo cómic, donde la división principal se modela por
 * volúmenes en lugar de tomos y capítulos.
 * 
 * @author Jorge
 * @since 12/03/2026
 */

@Entity
@Table(name = "VOLUME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volume {

  /**
   * ID del volumen.
   * Tiene id autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Número del volumen.
   * Es un numero positivo
   * La columna no puede ser nula
   */
  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer volumeNumber;

  /**
   * Título del volumen.
   * Tiene como maximo 200 caractares
   */
  @Size(max = 200)
  @Column(length = 200)
  private String title;

  /**
   * Edición a la que pertenece.
   * Relación muchos a uno obligatoria: una edición puede contener varios
   * volúmenes.
   */
  @NotNull
  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "edition_id", nullable = false)
  private Edition edition;
}
