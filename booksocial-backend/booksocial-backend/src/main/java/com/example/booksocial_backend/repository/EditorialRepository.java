package com.example.booksocial_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Editorial;

/**
 * Repositorio de editoriales de BookSocial.
 *
 * Maneja la persistencia de {@link Editorial} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las editoriales del catalogo.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {

  /**
   * Comprueba si existe un registro con el nombre indicado.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param name nombre del recurso usado como criterio de busqueda
   * @return true si existe un registro con ese nombre; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByName(String name);
}
