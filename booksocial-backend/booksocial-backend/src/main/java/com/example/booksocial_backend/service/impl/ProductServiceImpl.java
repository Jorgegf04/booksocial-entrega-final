package com.example.booksocial_backend.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.catalog.Product;
import com.example.booksocial_backend.DTO.catalog.ProductRequestDTO;
import com.example.booksocial_backend.DTO.catalog.ProductResponseDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.domain.catalog.Editorial;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.exception.EditionNotFoundException;
import com.example.booksocial_backend.exception.ProductNotFoundException;
import com.example.booksocial_backend.repository.EditionRepository;
import com.example.booksocial_backend.repository.ProductRepository;
import com.example.booksocial_backend.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de productos.
 *
 * Define las operaciones de catalogo comercial, disponibilidad y consulta por obra o edicion. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

  private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar productos. */
  private final ProductRepository productRepository;
  /** Repositorio JPA utilizado para validar la edicion comercializada. */
  private final EditionRepository editionRepository;
  /** Componente utilizado para transformar DTOs en entidades de dominio. */
  private final ModelMapper modelMapper;

  /**
   * Crea un nuevo recurso aplicando las validaciones de negocio correspondientes.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del recurso creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public ProductResponseDTO createProduct(ProductRequestDTO request) {

    log.info("[PRODUCT] [CREATE] [START] editionId={}", request.getEditionId());
    Product product = modelMapper.map(request, Product.class);

    Edition edition = editionRepository.findById(request.getEditionId())
        .orElseThrow(() -> new EditionNotFoundException("Edición no encontrada con id: " + request.getEditionId()));

    product.setEdition(edition);

    validateProduct(product);

    Product saved = productRepository.save(product);

    return mapToDTO(saved);
  }

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public ProductResponseDTO getProductById(Long id) {

    Product product = getProductEntityById(id);

    return mapToDTO(product);
  }

  /**
   * Obtiene todos los recursos disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getAllProducts() {

    return productRepository.findAll()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getAvailableProducts() {

    return productRepository.findByStockGreaterThan(0)
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param editionId identificador de la edicion asociada a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getProductsByEdition(Long editionId) {

    return productRepository.findByEditionId(editionId)
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param workId identificador de la obra asociada a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getProductsByWork(Long workId) {

    return productRepository.findByEdition_Work_Id(workId)
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {

    log.info("[PRODUCT] [UPDATE] [START] id={}", id);
    Product existing = getProductEntityById(id);

    Product updated = modelMapper.map(request, Product.class);

    Edition edition = editionRepository.findById(request.getEditionId())
        .orElseThrow(() -> new EditionNotFoundException("Edición no encontrada con id: " + request.getEditionId()));

    updated.setEdition(edition);

    validateProduct(updated);

    existing.setPrice(updated.getPrice());
    existing.setStock(updated.getStock());
    existing.setEdition(updated.getEdition());

    Product saved = productRepository.save(existing);

    return mapToDTO(saved);
  }

  /**
   * Convierte Product en ProductDTO.
   */
  private ProductResponseDTO mapToDTO(Product product) {

    Edition edition = product.getEdition();
    Work work = edition != null ? edition.getWork() : null;
    Editorial editorial = edition != null ? edition.getEditorial() : null;

    return new ProductResponseDTO(
        product.getId(),
        product.getPrice(),
        product.getStock(),

        edition != null ? edition.getId() : null,
        edition != null ? edition.getTitle() : null,
        edition != null ? edition.getIsbn() : null,
        edition != null ? edition.getTotalTomes() : null,

        work != null ? work.getId() : null,
        work != null ? work.getTitle() : null,

        editorial != null ? editorial.getName() : null);
  }

  /**
   * Obtiene entidad Product o lanza excepción.
   */
  private Product getProductEntityById(Long id) {

    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con id: " + id));
  }

  /**
   * Valida los datos de un producto.
   */
  private void validateProduct(Product product) {

    if (product == null) {
      throw new IllegalArgumentException("El producto no puede ser nulo");
    }

    if (product.getPrice() == null || product.getPrice() <= 0) {
      throw new IllegalArgumentException("El precio debe ser mayor que 0");
    }

    if (product.getStock() == null || product.getStock() < 0) {
      throw new IllegalArgumentException("El stock no puede ser negativo");
    }

    if (product.getEdition() == null || product.getEdition().getId() == null) {
      throw new IllegalArgumentException("El producto debe estar asociado a una edición válida");
    }
  }
}
