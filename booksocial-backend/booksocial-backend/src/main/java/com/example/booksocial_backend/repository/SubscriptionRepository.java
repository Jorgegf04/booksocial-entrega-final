package com.example.booksocial_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.user.Subscription;

/**
 * Repositorio de suscripciones de BookSocial.
 *
 * Maneja la persistencia de {@link Subscription} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las suscripciones asociadas a usuarios.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

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
  Optional<Subscription> findByUserId(Long userId);

  /**
   * Comprueba si un usuario tiene una suscripcion activa.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @return true si el usuario tiene suscripcion activa; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByUserIdAndActivatedTrue(Long userId);

}