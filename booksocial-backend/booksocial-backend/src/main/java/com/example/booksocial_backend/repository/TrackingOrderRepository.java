package com.example.booksocial_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.commerce.TrackingOrder;

/**
 * Repositorio de seguimiento de pedidos de BookSocial.
 *
 * Maneja la persistencia de {@link TrackingOrder} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona el historial de estados de los pedidos.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface TrackingOrderRepository extends JpaRepository<TrackingOrder, Long> {

  /**
   * Obtiene el historial de estados de un pedido ordenado por fecha ascendente.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param orderId identificador del pedido usado como filtro
   * @return lista cronologica de estados del pedido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<TrackingOrder> findByOrderIdOrderByDateAsc(Long orderId);

  /**
   * Obtiene el ultimo estado registrado de un pedido.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param orderId identificador del pedido usado como filtro
   * @return Optional con el estado mas reciente, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  Optional<TrackingOrder> findFirstByOrder_IdOrderByDateDesc(Long orderId);

}
