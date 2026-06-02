package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Tome;

/**
 * Repositorio de tomos de BookSocial.
 *
 * Maneja la persistencia de {@link Tome} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los tomos de ediciones de tipo manga.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface TomeRepository extends JpaRepository<Tome, Long> {

  /**
   * Obtiene registros asociados a una edicion concreta.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param editionId identificador de la edicion usada como filtro
   * @return lista de entidades vinculadas a la edicion indicada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Tome> findByEditionId(Long editionId);

  /**
   * Busca tomos por edición ordenados por número.
   *
   * @param editionId identificador de la edición
   * @return lista de tomos ordenados
   */
}
