package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Product;

/**
 * Repositorio de productos de BookSocial.
 *
 * Maneja la persistencia de {@link Product} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los productos comerciales vinculados a ediciones.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Obtiene productos con stock superior al valor indicado.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param stock cantidad minima de stock que debe superar el producto
   * @return lista de productos con disponibilidad mayor que el parametro recibido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Product> findByStockGreaterThan(Integer stock);

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
  List<Product> findByEditionId(Long editionId);

  /**
   * Obtiene productos asociados a una obra a traves de su edicion.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param workId identificador de la obra usada como filtro
   * @return lista de productos relacionados con la obra indicada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Product> findByEdition_Work_Id(Long workId);

}