package com.example.booksocial_backend.controller.catalog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.catalog.ProductRequestDTO;
import com.example.booksocial_backend.DTO.catalog.ProductResponseDTO;
import com.example.booksocial_backend.service.ProductService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los productos comerciales vinculados a las ediciones.
 * Permite consultar disponibilidad, productos por obra o edicion, y administrar su precio y stock.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "ProductController", description = "API REST para la gestion de productos")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  /**
   * Crea un recurso en el sistema y devuelve la respuesta con los datos actualizados.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Crear recurso", description = "Registra un nuevo recurso y devuelve los datos generados por el servicio.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
  }

  /**
   * Endpoint que sirve para crear varios recursos de golpe, util para cargas iniciales o pruebas desde Postman.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Creacion masiva", description = "Registra varios recursos en una sola peticion y devuelve el listado creado.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/batch")
  public ResponseEntity<List<ProductResponseDTO>> createMany(
      @Valid @RequestBody List<ProductRequestDTO> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista de productos no puede estar vacia");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(productService::createProduct).toList());
  }

  /**
   * Endpoint que devuelve un recurso segun su ID.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Obtener por ID", description = "Recupera un recurso concreto mediante su identificador.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  /**
   * Endpoint que devuelve todos los recursos disponibles en el sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar recursos", description = "Devuelve el listado de recursos disponibles para consulta.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping
  public ResponseEntity<List<ProductResponseDTO>> getAll() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  /**
   * Endpoint que devuelve los productos con stock disponible.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar productos disponibles", description = "Devuelve los productos que tienen unidades disponibles para la compra.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/available")
  public ResponseEntity<List<ProductResponseDTO>> getAvailable() {
    return ResponseEntity.ok(productService.getAvailableProducts());
  }

  /**
   * Endpoint que devuelve los productos asociados a una edicion concreta.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Productos por edicion", description = "Recupera los productos relacionados con la edicion indicada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/edition/{editionId}")
  public ResponseEntity<List<ProductResponseDTO>> getByEdition(@PathVariable Long editionId) {
    return ResponseEntity.ok(productService.getProductsByEdition(editionId));
  }

  /**
   * Endpoint que devuelve los productos asociados a una obra concreta.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Productos por obra", description = "Recupera los productos relacionados con la obra indicada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<ProductResponseDTO>> getByWork(@PathVariable Long workId) {
    return ResponseEntity.ok(productService.getProductsByWork(workId));
  }

  /**
   * Endpoint que sirve para actualizar los datos de un recurso y devolver su version actualizada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Actualizar recurso", description = "Modifica los datos de un recurso existente y devuelve la version actualizada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
      @Valid @RequestBody ProductRequestDTO request) {
    return ResponseEntity.ok(productService.updateProduct(id, request));
  }
}
