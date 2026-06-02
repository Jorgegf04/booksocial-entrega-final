package com.example.booksocial_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.commerce.OrderLine;

/**
 * Repositorio de lineas de pedido de BookSocial.
 *
 * Maneja la persistencia de {@link OrderLine} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las lineas que relacionan pedidos y productos comprados.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

  /**
   * Comprueba si existen lineas de pedido asociadas a un producto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param productId identificador del producto usado como filtro
   * @return true si el producto aparece en alguna linea de pedido; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByProductId(Long productId);
}
