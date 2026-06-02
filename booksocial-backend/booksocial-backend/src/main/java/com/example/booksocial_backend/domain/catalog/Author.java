package com.example.booksocial_backend.domain.catalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Entidad que representa a un autor dentro de la aplicación.
 * Un autor puede estar relacionado con una o varias obras. La relación
 * entre autores y obras es de muchos a muchos, gestionada desde la entidad
 * {@link Work}.
 * 
 * @author Jorge
 * @since 12/03/2026
 * @version 1.0
 */

@Entity
@Table(name = "AUTHOR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Author {

  /**
   * ID único del autor.
   * Se genera automaticamente
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Nombre completo del autor.
   * Es un campo obligatorio
   * Las columnas en la base no puede ser nula y tiene una restrición de 150
   * caractes
   */
  @NotBlank
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String name;

  /**
   * Nacionalidad del autor
   */
  @Column(name = "nationality")
  private String nationality;

  /**
   * Fecha de nacimiento del autor.
   * La fecha tiene que ser anterior a la fecha actual
   */
  @Past
  @Column(name = "birth_date")
  private LocalDate birthDate;

  /**
   * Ruta o URL de la imagen del autor.
   */
  @Column(length = 500)
  private String img;

  /**
   * Lista de obras en las que este autor ha participado.
   *
   * <p>
   * Es el lado inverso de la relación muchos a muchos. La relación se gestiona
   * desde {@link Work#authors}, por eso se usa {@code mappedBy = "authors"}.
   * </p>
   */
  @Builder.Default
  @ToString.Exclude
  @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
  private List<Work> works = new ArrayList<>();
}
