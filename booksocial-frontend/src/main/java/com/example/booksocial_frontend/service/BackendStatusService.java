package com.example.booksocial_frontend.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import jakarta.annotation.PostConstruct;

/**
 * Comprueba si el backend esta disponible para mostrar avisos globales en las vistas.
 */
@Service
public class BackendStatusService {

  private static final long CACHE_MILLIS = 10_000L;
  private static final int FAILURES_BEFORE_DOWN = 2;

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;
  private volatile boolean backendDown;
  private volatile long cacheUntil;
  private int consecutiveFailures;

  @PostConstruct
  public void init() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(2));
    requestFactory.setReadTimeout(Duration.ofSeconds(3));

    this.restClient = RestClient.builder()
        .baseUrl(resolveBackendBaseUrl())
        .requestFactory(requestFactory)
        .build();
  }

  public boolean isBackendDown() {
    long now = System.currentTimeMillis();
    if (now < cacheUntil) {
      return backendDown;
    }

    synchronized (this) {
      now = System.currentTimeMillis();
      if (now < cacheUntil) {
        return backendDown;
      }

      if (canReachBackend()) {
        consecutiveFailures = 0;
        backendDown = false;
      } else {
        consecutiveFailures++;
        backendDown = consecutiveFailures >= FAILURES_BEFORE_DOWN;
      }
      cacheUntil = now + CACHE_MILLIS;
      return backendDown;
    }
  }

  private boolean canReachBackend() {
    try {
      restClient.get()
          .uri("/actuator/health")
          .retrieve()
          .toBodilessEntity();
      return true;
    } catch (RestClientException ex) {
      return false;
    }
  }

  private String resolveBackendBaseUrl() {
    String normalized = apiBaseUrl == null ? "http://localhost:9999/api" : apiBaseUrl.trim();
    while (normalized.endsWith("/")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    if (normalized.endsWith("/api")) {
      return normalized.substring(0, normalized.length() - 4);
    }
    return normalized;
  }
}
