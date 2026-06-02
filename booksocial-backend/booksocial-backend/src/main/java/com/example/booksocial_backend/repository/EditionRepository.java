package com.example.booksocial_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Edition;

/**
 * Repositorio de ediciones de BookSocial.
 *
 * Maneja la persistencia de {@link Edition} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las ediciones asociadas a obras y editoriales.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface EditionRepository extends JpaRepository<Edition, Long> {

  /**
   * Busca una edicion por su ISBN.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param isbn ISBN de la edicion usada como criterio de busqueda
   * @return Optional con la edicion encontrada, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  Optional<Edition> findByIsbn(String isbn);
}
