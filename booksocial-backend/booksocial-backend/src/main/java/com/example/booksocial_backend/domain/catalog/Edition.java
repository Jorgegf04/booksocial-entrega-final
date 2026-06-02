package com.example.booksocial_backend.domain.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad que representa una edición concreta de una obra.
 *
 * Una misma obra puede tener múltiples ediciones publicadas por
 * diferentes editoriales o en distintos formatos.
 * La edición es la entidad que conecta el catálogo con el sistema
 * de productos y ventas.
 * La tabla EDITION guarda los datos propios de cada publicación y sus claves
 * foráneas hacia la obra y la editorial.
 *
 * @author Jorge
 * @since 12/03/2026
 */

@Entity
@Table(name = "EDITION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Edition {

  /**
   * ID único de la edición.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * ISBN de la edición.
   * Tiene una restriccion de 20 caracteres
   * La columna tiene valor unico, maximo de 20 caracteres y no pude ser nulo
   */
  @NotBlank
  @Size(max = 20)
  @Column(unique = true, length = 20, nullable = false)
  private String isbn;

  /**
   * Fecha de publicación de la edición.
   */
  @PastOrPresent
  @Column(name = "edition_date")
  private LocalDate editionDate;

  /**
   * Título comercial de esta edición.
   * Puede coincidir con el título de la obra o variar según la publicación,
   * idioma, formato o editorial
   */
  private String title;

  /**
   * Número total de tomos previstos o publicados para esta edición.
   */
  private Integer totalTomes;

  /**
   * Tomos que pertenecen a esta edición.
   *
   * <p>
   * Relación uno a muchos con {@link Tome}. La clave foránea está en la tabla
   * TOME y los tomos huérfanos se eliminan al dejar de pertenecer a la edición.
   * </p>
   */
  @Builder.Default
  @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Tome> tomes = new ArrayList<>();

  /**
   * Obra a la que pertenece esta edición.
   *
   * <p>
   * Relación muchos a uno obligatoria: varias ediciones pueden pertenecer a una
   * misma obra.
   * </p>
   */

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  /**
   * Editorial responsable de la edición.
   *
   * <p>
   * Relación muchos a uno obligatoria: una editorial puede publicar varias
   * ediciones.
   * </p>
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "editorial_id", nullable = false)
  private Editorial editorial;

  /**
   * Productos asociados a esta edición.
   *
   * <p>
   * Conecta la edición del catálogo con los productos que se venden en la tienda.
   * Se ignora en JSON para evitar ciclos de serialización.
   * </p>
   */
  @Builder.Default
  @ToString.Exclude
  @JsonIgnore
  @OneToMany(mappedBy = "edition", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Product> products = new ArrayList<>();
}
