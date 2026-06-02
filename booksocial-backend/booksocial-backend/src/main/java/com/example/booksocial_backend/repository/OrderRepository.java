package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.commerce.Order;

/**
 * Repositorio de pedidos de BookSocial.
 *
 * Maneja la persistencia de {@link Order} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los pedidos realizados por usuarios o invitados.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * Obtiene los pedidos realizados por un usuario, cargando sus asociaciones.
   *
   * Esta consulta utiliza JPQL declarado con {@link Query} y emplea
   * {@code LEFT JOIN FETCH} para cargar lineas de pedido, productos y ediciones
   * en una sola consulta, evitando errores de carga diferida al construir la
   * respuesta.
   *
   * @param userId identificador del usuario usado como filtro
   * @return lista de pedidos del usuario con lineas, productos y ediciones cargados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Query("SELECT DISTINCT o FROM Order o " +
      "LEFT JOIN FETCH o.orderLines ol " +
      "LEFT JOIN FETCH ol.product p " +
      "LEFT JOIN FETCH p.edition " +
      "WHERE o.user.id = :userId")
  List<Order> findByUserId(@Param("userId") Long userId);
}
