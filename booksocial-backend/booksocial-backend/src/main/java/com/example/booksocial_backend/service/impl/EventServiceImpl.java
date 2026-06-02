package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.social.EventRequestDTO;
import com.example.booksocial_backend.DTO.social.EventResponseDTO;
import com.example.booksocial_backend.domain.social.Event;
import com.example.booksocial_backend.domain.user.User;

import com.example.booksocial_backend.exception.EventNotFoundException;
import com.example.booksocial_backend.exception.UserNotFoundException;
import com.example.booksocial_backend.repository.EventRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.service.EventService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de eventos.
 *
 * Define las operaciones para crear, consultar, actualizar, eliminar y gestionar asistencia a eventos. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

  private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

  /** Repositorio JPA utilizado para persistir eventos y asistencias. */
  private final EventRepository eventRepository;
  /** Repositorio JPA utilizado para validar usuarios asistentes. */
  private final UserRepository userRepository;

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
  public EventResponseDTO createEvent(EventRequestDTO request) {

    if (request == null) throw new IllegalArgumentException("La request del evento no puede ser nula");
    log.info("[EVENT] [CREATE] [START] title='{}'", request.getTitle());
    Event event = new Event();

    if (request.getTitle() == null || request.getTitle().isBlank())
      throw new IllegalArgumentException("El título del evento es obligatorio");
    event.setTitle(request.getTitle().trim());
    event.setDescription(request.getDescription());
    event.setImg(request.getImg());
    event.setDate(request.getDate());

    // Se usa una lista mutable con usuarios persistidos para mantener la relacion many-to-many.
    if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
      List<User> users = userRepository.findAllById(request.getUserIds());
      event.setUsers(new ArrayList<>(users));
    }

    validateEvent(event);

    if (!event.getDate().isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("La fecha del evento debe ser futura");
    }

    Event saved = eventRepository.save(event);

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
  public EventResponseDTO getEventById(Long id) {
    return mapToDTO(getEventEntityById(id));
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
  public List<EventResponseDTO> getAllEvents() {

    return eventRepository.findAll()
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
  public List<EventResponseDTO> getUpcomingEvents() {

    return eventRepository.findByDateAfter(LocalDateTime.now())
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
  public EventResponseDTO updateEvent(Long id, EventRequestDTO request) {

    log.info("[EVENT] [UPDATE] [START] id={}", id);
    Event existing = getEventEntityById(id);

    if (request.getTitle() != null && !request.getTitle().isBlank())
      existing.setTitle(request.getTitle().trim());
    if (request.getDescription() != null) existing.setDescription(request.getDescription());
    if (request.getImg() != null) existing.setImg(request.getImg());
    if (request.getDate() != null) existing.setDate(request.getDate());

    // Se modifica la coleccion existente para conservar la entidad gestionada por JPA.
    if (request.getUserIds() != null) {

      List<User> users = userRepository.findAllById(request.getUserIds());

      existing.getUsers().clear();
      existing.getUsers().addAll(users);
    }

    validateEvent(existing);

    if (!existing.getDate().isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("La fecha del evento debe ser futura");
    }

    return mapToDTO(eventRepository.save(existing));
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteEvent(Long id) {

    log.info("[EVENT] [DELETE] [START] id={}", id);
    eventRepository.delete(getEventEntityById(id));
    log.info("[EVENT] [DELETE] [SUCCESS] id={}", id);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param event valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private EventResponseDTO mapToDTO(Event event) {

    List<User> safeUsers = event.getUsers() != null ? event.getUsers() : List.of();

    List<Long> userIds = safeUsers.stream()
        .map(User::getId)
        .toList();

    List<String> usernames = safeUsers.stream()
        .map(User::getUsername)
        .toList();

    return new EventResponseDTO(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        event.getImg(),
        event.getDate(),
        userIds,
        usernames,
        userIds.size());
  }

  /**
   * Ejecuta la operacion de servicio joinEvent.
   *
   * @param eventId valor utilizado por la operacion
   * @param userId identificador del usuario asociado a la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public EventResponseDTO joinEvent(Long eventId, Long userId) {
    Event event = getEventEntityById(eventId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    List<User> users = event.getUsers() != null ? event.getUsers() : new ArrayList<>();
    if (event.getUsers() == null) event.setUsers(users);
    boolean alreadyJoined = users.stream().anyMatch(u -> u.getId().equals(userId));
    if (alreadyJoined) {
      throw new IllegalArgumentException("El usuario ya está inscrito en este evento");
    }
    users.add(user);
    return mapToDTO(eventRepository.save(event));
  }

  /**
   * Ejecuta la operacion de servicio leaveEvent.
   *
   * @param eventId valor utilizado por la operacion
   * @param userId identificador del usuario asociado a la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public EventResponseDTO leaveEvent(Long eventId, Long userId) {
    Event event = getEventEntityById(eventId);
    if (event.getUsers() == null) {
      throw new IllegalArgumentException("El usuario no está inscrito en este evento");
    }
    boolean removed = event.getUsers().removeIf(u -> u.getId().equals(userId));
    if (!removed) {
      throw new IllegalArgumentException("El usuario no está inscrito en este evento");
    }
    return mapToDTO(eventRepository.save(event));
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
  private Event getEventEntityById(Long id) {
    return eventRepository.findById(id)
        .orElseThrow(() -> new EventNotFoundException(id));
  }

  /**
   * Valida los datos básicos de un evento.
   */
  private void validateEvent(Event event) {

    if (event == null) {
      throw new IllegalArgumentException("El evento no puede ser nulo");
    }

    if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("El título del evento es obligatorio");
    }

    if (event.getDate() == null) {
      throw new IllegalArgumentException("La fecha del evento es obligatoria");
    }
  }
}
