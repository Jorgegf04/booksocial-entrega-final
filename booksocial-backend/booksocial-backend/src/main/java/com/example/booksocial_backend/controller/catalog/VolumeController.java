package com.example.booksocial_backend.controller.catalog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.catalog.VolumeRequestDTO;
import com.example.booksocial_backend.DTO.catalog.VolumeResponseDTO;
import com.example.booksocial_backend.service.VolumeService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los volumenes asociados a ediciones de tipo comic.
 * Permite crear, listar, actualizar y eliminar volumenes del catalogo.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "VolumeController", description = "API REST para la gestion de volumenes")
@RestController
@RequestMapping("/api/volumes")
@RequiredArgsConstructor
public class VolumeController {

  private final VolumeService volumeService;

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
  public ResponseEntity<VolumeResponseDTO> create(@Valid @RequestBody VolumeRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(volumeService.createVolume(request));
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
  public ResponseEntity<List<VolumeResponseDTO>> createMany(
      @Valid @RequestBody List<VolumeRequestDTO> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista de volumenes no puede estar vacia");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(volumeService::createVolume).toList());
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
  public ResponseEntity<List<VolumeResponseDTO>> getAll() {
    return ResponseEntity.ok(volumeService.getAllVolumes());
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
  public ResponseEntity<VolumeResponseDTO> update(@PathVariable Long id,
      @Valid @RequestBody VolumeRequestDTO request) {
    return ResponseEntity.ok(volumeService.updateVolume(id, request));
  }

  /**
   * Endpoint que sirve para eliminar un recurso del sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar recurso", description = "Elimina el recurso indicado y devuelve una respuesta sin contenido.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    volumeService.deleteVolume(id);
    return ResponseEntity.noContent().build();
  }
}
