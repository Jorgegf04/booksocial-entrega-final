package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.ProductRequestDTO;
import com.example.booksocial_backend.DTO.catalog.ProductResponseDTO;

/**
 * Contrato de servicio para la gestion de productos.
 * Define las operaciones de catalogo comercial, disponibilidad y consulta por
 * obra o edicion.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface ProductService {

  /**
   * Crea un nuevo producto dentro de la aplicación
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del producto creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  ProductResponseDTO createProduct(ProductRequestDTO request);

  /**
   * Obtiene un recurso por su producto.
   *
   * @param id ID del producto sobre el que se realiza la operacion
   * @return DTO de respuesta del producto solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  ProductResponseDTO getProductById(Long id);

  /**
   * Obtiene todos los productos disponibles dentro del sistema.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ProductResponseDTO> getAllProducts();

  /**
   * Obtiene todos los productos disponibles
   *
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ProductResponseDTO> getAvailableProducts();

  /**
   * Obtiene los productos asociados a una edición
   *
   * @param editionId identificador de la edicion asociada a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ProductResponseDTO> getProductsByEdition(Long editionId);

  /**
   * Obtieen los productos asociados a una obra
   *
   * @param workId identificador de la obra asociada a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ProductResponseDTO> getProductsByWork(Long workId);

  /**
   * Actualiza un producto existente con los datos recibidos.
   *
   * @param id      ID del producto sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);

}
