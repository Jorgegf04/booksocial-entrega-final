package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Volume;

/**
 * Repositorio de volumenes de BookSocial.
 *
 * Maneja la persistencia de {@link Volume} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los volumenes asociados a ediciones de tipo comic.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {

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
  List<Volume> findByEditionId(Long editionId);

  /**
   * Busca volúmenes de una edición ordenados por número.
   *
   * @param editionId identificador de la edición
   * @return lista de volúmenes ordenados
   */
}
