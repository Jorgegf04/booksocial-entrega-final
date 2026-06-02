package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.social.TrackingWork;

/**
 * Repositorio de seguimiento de obras de BookSocial.
 *
 * Maneja la persistencia de {@link TrackingWork} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona la biblioteca, wishlist o estado de lectura de obras por usuario.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface TrackingWorkRepository extends JpaRepository<TrackingWork, Long> {

  /**
   * Obtiene registros asociados a un usuario concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @return resultado asociado al usuario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<TrackingWork> findByUserId(Long userId);

  /**
   * Comprueba si un usuario ya sigue o guarda una obra concreta.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @param workId identificador de la obra usada como filtro
   * @return true si existe el seguimiento; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByUserIdAndWorkId(Long userId, Long workId);
}
