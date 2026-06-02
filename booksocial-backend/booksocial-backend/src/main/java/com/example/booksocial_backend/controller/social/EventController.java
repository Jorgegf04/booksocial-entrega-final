package com.example.booksocial_backend.controller.social;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.social.EventRequestDTO;
import com.example.booksocial_backend.DTO.social.EventResponseDTO;
import com.example.booksocial_backend.service.EventService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los eventos sociales de BookSocial.
 * Permite crear, consultar, actualizar, unirse, salir y eliminar eventos.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "EventController", description = "API REST para la gestion de eventos")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;

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
  public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
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
  public ResponseEntity<List<EventResponseDTO>> createMany(@Valid @RequestBody List<EventRequestDTO> requests) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(eventService::createEvent).toList());
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
  public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(eventService.getEventById(id));
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
  public ResponseEntity<List<EventResponseDTO>> getAll() {
    return ResponseEntity.ok(eventService.getAllEvents());
  }

  /**
   * Endpoint que coordina la peticion REST con el servicio correspondiente.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "getUpcoming", description = "Ejecuta la operacion solicitada y devuelve la respuesta generada por el servicio.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/upcoming")
  public ResponseEntity<List<EventResponseDTO>> getUpcoming() {
    return ResponseEntity.ok(eventService.getUpcomingEvents());
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
  public ResponseEntity<EventResponseDTO> update(@PathVariable Long id,
      @Valid @RequestBody EventRequestDTO request) {
    return ResponseEntity.ok(eventService.updateEvent(id, request));
  }

  /**
   * Endpoint que registra la asistencia de un usuario a un evento.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Unirse a evento", description = "Asocia el usuario indicado al evento solicitado.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasAnyRole('SUBSCRIBED', 'ADMIN')")
  @PostMapping("/{id}/join")
  public ResponseEntity<EventResponseDTO> join(@PathVariable Long id, @RequestParam Long userId) {
    return ResponseEntity.ok(eventService.joinEvent(id, userId));
  }

  /**
   * Endpoint que cancela la asistencia de un usuario a un evento.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Salir de evento", description = "Elimina la asociacion del usuario con el evento solicitado.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasAnyRole('SUBSCRIBED', 'ADMIN')")
  @DeleteMapping("/{id}/leave")
  public ResponseEntity<EventResponseDTO> leave(@PathVariable Long id, @RequestParam Long userId) {
    return ResponseEntity.ok(eventService.leaveEvent(id, userId));
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
    eventService.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }
}
