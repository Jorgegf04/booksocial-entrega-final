package com.example.booksocial_backend.domain.catalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.booksocial_backend.domain.social.Comment;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * Entidad que representa una obra dentro de la aplicacion.
 *
 * Una obra es el concepto que agrupa la información general de un libro, manga
 * o cómic
 * A partir de esta entidad se gestionan las distintas ediciones publicadas por
 * diferentes editorieles
 * Su relacion principla es la de una a muchos con la entidad edicion
 *
 * @author Jorge
 * @since 16/03/2026
 * @version 1.0
 */
@Entity
@Table(name = "WORK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Work {
  /**
   * ID único de la obra.
   * El id es autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Título principal de la obra.
   * Tiene un maximo de 255 caracteres
   * En la base de datos no puede ser nulo y tiene una longitud de 255
   */
  @NotBlank
  @Size(max = 255)
  @Column(nullable = false, length = 255)
  private String title;

  /**
   * Sinopsis o descripción general de la obra.
   * Tiene un maximo de 2000 caractares
   * Tiene un tamaña de 2000 caracteres en la base de datos
   */
  @Size(max = 2000)
  @Column(length = 2000)
  private String description;

  /**
   * Fecha de publicación original de la obra.
   * Puede ser presente o pasada
   */
  @PastOrPresent
  @Column(name = "publication_date")
  private LocalDate publicationDate;

  /**
   * Imagen de la obra.
   */
  @Size(max = 500)
  private String img;

  /**
   * Valoración media calculada a partir de las opiniones de los usuarios.
   * La nota tiene que ser entre 0 o 10
   */
  @Min(0)
  @Max(10)
  private Double averageRating;

  /**
   * Género literario principal.
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  private Genre genre;

  /**
   * Tipo de obra dentro del catálogo.
   *
   * Permite diferenciar si la obra se gestiona como libro, manga o cómic, lo que
   * afecta a su estructura interna de ediciones, tomos, capítulos o volúmenes.
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkType type;

  /**
   * Demografía de la obra.
   *
   * Es un dato opcional, principalmente útil en obras tipo manga.
   */
  @Enumerated(EnumType.STRING)
  private Demographic demographic;

  /**
   * Autores asociados a la obra
   * Relación muchos a muchos entre obras y autores. La tabla intermedia
   * WORK_AUTHOR guarda las claves foráneas de ambas entidades.
   */
  @Builder.Default
  @ToString.Exclude
  @ManyToMany
  @JoinTable(name = "WORK_AUTHOR", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
  private List<Author> authors = new ArrayList<>();

  /**
   * Ediciones publicadas de esta obra.
   * 
   * Relación uno a muchos. La clave foránea se encuentra en {@link Edition}
   * mediante el campo {@code work}. Al eliminar una obra se eliminan también sus
   * ediciones por {@code orphanRemoval = true}.
   */
  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Edition> editions = new ArrayList<>();

  /**
   * Lista de seguimientos asociados a esta obra.
   * Representa todos los usuarios que siguen esta obra dentro del sistema,
   * junto con su estado de seguimiento (pendiente, leyendo, completada, etc).
   * 
   * La relació nes de una a muchos con la entidad TrackingWokr
   * {@link TrackingWork}
   *
   * Se utiliza carga perezosa (LAZY) para optimizar el rendimiento
   * y evitar cargar innecesariamente los seguimientos.
   */
  @Builder.Default
  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<TrackingWork> trackingWorks = new ArrayList<>();

  /**
   * Comentarios publicados por los usuarios sobre esta obra.
   *
   * Relación uno a muchos con {@link Comment}. Se ignora en la serialización JSON
   * para evitar ciclos entre obra, comentario y usuario.
   */
  @Builder.Default
  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();
}
