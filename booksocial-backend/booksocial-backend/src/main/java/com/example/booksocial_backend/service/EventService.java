package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.social.EventRequestDTO;
import com.example.booksocial_backend.DTO.social.EventResponseDTO;

/**
 * Contrato de servicio para la gestion de eventos.
 * Define las operaciones para crear, consultar, actualizar, eliminar y
 * gestionar asistencia a eventos.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface EventService {

  /**
   * Crea un nuevo evento dentro de la aplicación
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del evento creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EventResponseDTO createEvent(EventRequestDTO request);

  /**
   * Obtiene un recurso por su ID.
   *
   * @param id id del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del evento solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EventResponseDTO getEventById(Long id);

  /**
   * Obtiene todos los eventos disponibles dentro de la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<EventResponseDTO> getAllEvents();

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<EventResponseDTO> getUpcomingEvents();

  /**
   * Actualiza un recurso existente con los datos recibidos.
   *
   * @param id      identificador del evento sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EventResponseDTO updateEvent(Long id, EventRequestDTO request);

  /**
   * Elimina, cancela o deshace la relacion indicada segun la regla de negocio.
   *
   * @param id id del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteEvent(Long id);

  /**
   * Ejecuta la operacion de servicio joinEvent.
   *
   * @param eventId valor utilizado por la operacion
   * @param userId  identificador del usuario asociado a la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EventResponseDTO joinEvent(Long eventId, Long userId);

  /**
   * Ejecuta la operacion de servicio leaveEvent.
   *
   * @param eventId valor utilizado por la operacion
   * @param userId  identificador del usuario asociado a la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EventResponseDTO leaveEvent(Long eventId, Long userId);
}
