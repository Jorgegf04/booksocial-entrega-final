package com.example.booksocial_backend.service.impl;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.user.SubscriptionRequestDTO;
import com.example.booksocial_backend.DTO.user.SubscriptionResponseDTO;
import com.example.booksocial_backend.domain.user.Role;
import com.example.booksocial_backend.domain.user.Subscription;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.exception.SubscriptionAlreadyActiveException;
import com.example.booksocial_backend.exception.SubscriptionNotFoundException;
import com.example.booksocial_backend.exception.UserNotFoundException;
import com.example.booksocial_backend.repository.SubscriptionRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de suscripciones.
 *
 * Define operaciones para activar, consultar y cancelar suscripciones de usuario. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar suscripciones. */
  private final SubscriptionRepository subscriptionRepository;
  /** Repositorio JPA utilizado para actualizar el rol del usuario suscrito. */
  private final UserRepository userRepository;

  /**
   * Ejecuta la operacion de servicio activateSubscription.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public SubscriptionResponseDTO activateSubscription(SubscriptionRequestDTO request) {

    log.info("[SUBSCRIPTION] [ACTIVATE] [START] userId={}", request.getUserId());
    if (subscriptionRepository.existsByUserIdAndActivatedTrue(request.getUserId())) {
      throw new SubscriptionAlreadyActiveException(request.getUserId());
    }

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

    user.setRole(Role.SUBSCRIBED);
    userRepository.save(user);

    // Reutilizar el registro existente (puede estar inactivo por cancelación previa)
    // para no violar la constraint UNIQUE en user_id
    Subscription subscription = subscriptionRepository.findByUserId(request.getUserId())
        .orElse(new Subscription());

    subscription.setStartDate(LocalDate.now());
    subscription.setEndDate(LocalDate.now().plusDays(30));
    subscription.setActivated(true);
    subscription.setUser(user);

    Subscription saved = subscriptionRepository.save(subscription);

    log.info("[SUBSCRIPTION] [ACTIVATE] [SUCCESS] userId={}", request.getUserId());
    return mapToDTO(saved);
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void cancelSubscription(Long userId) {

    log.info("[SUBSCRIPTION] [CANCEL] [START] userId={}", userId);
    Subscription subscription = subscriptionRepository.findByUserId(userId)
        .orElseThrow(() -> new SubscriptionNotFoundException(userId));

    subscription.setActivated(false);

    User user = subscription.getUser();
    user.setRole(Role.REGISTERED);
    userRepository.save(user);

    log.info("[SUBSCRIPTION] [CANCEL] [SUCCESS] userId={}", userId);
  }

  /**
   * Obtiene recursos asociados a un usuario.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return lista de DTOs asociados al usuario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public SubscriptionResponseDTO getSubscriptionByUserId(Long userId) {

    Subscription subscription = subscriptionRepository.findByUserId(userId)
        .orElseThrow(() -> new SubscriptionNotFoundException(userId));

    return mapToDTO(subscription);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param subscription valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private SubscriptionResponseDTO mapToDTO(Subscription subscription) {
    return new SubscriptionResponseDTO(
        subscription.getId(),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getActivated(),
        subscription.getUser() != null ? subscription.getUser().getId() : null);
  }
}
