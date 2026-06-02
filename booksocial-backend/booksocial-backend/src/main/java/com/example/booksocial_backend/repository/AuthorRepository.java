package com.example.booksocial_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Author;

/**
 * Repositorio de autores de BookSocial.
 *
 * Maneja la persistencia de {@link Author} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona la persistencia y busqueda de autores del catalogo.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

  /**
   * Busca un registro por nombre ignorando mayusculas y minusculas.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param name nombre del recurso usado como criterio de busqueda
   * @return Optional con el registro encontrado, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    Optional<Author> findByNameIgnoreCase(String name);

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
