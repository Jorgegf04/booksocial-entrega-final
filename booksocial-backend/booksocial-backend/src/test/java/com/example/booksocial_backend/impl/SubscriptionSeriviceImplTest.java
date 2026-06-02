package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.example.booksocial_backend.DTO.user.SubscriptionRequestDTO;
import com.example.booksocial_backend.domain.user.Subscription;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.repository.SubscriptionRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.service.impl.SubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SubscriptionServiceImpl service;

  private Subscription subscription;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setId(1L);

    subscription = new Subscription();
    subscription.setId(1L);
    subscription.setUser(user);
    subscription.setStartDate(LocalDate.now());
    subscription.setEndDate(LocalDate.now().plusDays(30));
    subscription.setActivated(true);
  }

  @Test
  void shouldActivateSubscriptionSuccessfully() {
    SubscriptionRequestDTO request = new SubscriptionRequestDTO(1L);

    when(subscriptionRepository.existsByUserIdAndActivatedTrue(1L)).thenReturn(false);
    when(userRepository.findById(1L)).thenReturn(Optional.of(subscription.getUser()));
    when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());
    when(subscriptionRepository.save(any())).thenReturn(subscription);

    var result = service.activateSubscription(request);

    assertTrue(result.getActivated());
    assertEquals(1L, result.getUserId());
  }

  @Test
  void shouldThrowExceptionWhenSubscriptionAlreadyExists() {
    SubscriptionRequestDTO request = new SubscriptionRequestDTO(1L);

    when(subscriptionRepository.existsByUserIdAndActivatedTrue(1L)).thenReturn(true);

    assertThrows(RuntimeException.class, () -> service.activateSubscription(request));
  }

  @Test
  void shouldCancelSubscription() {
    when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(subscription));
    when(userRepository.save(any())).thenReturn(subscription.getUser());

    service.cancelSubscription(1L);

    assertFalse(subscription.getActivated());
  }

  @Test
  void shouldThrowExceptionWhenSubscriptionNotFoundOnCancel() {
    when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.cancelSubscription(1L));
  }

  @Test
  void shouldGetSubscriptionByUserId() {
    when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(subscription));

    var result = service.getSubscriptionByUserId(1L);

    assertEquals(1L, result.getUserId());
    assertTrue(result.getActivated());
  }

  @Test
  void shouldThrowExceptionWhenSubscriptionNotFound() {
    when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.getSubscriptionByUserId(1L));
  }
}
