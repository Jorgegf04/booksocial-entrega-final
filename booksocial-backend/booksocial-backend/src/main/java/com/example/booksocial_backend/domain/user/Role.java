package com.example.booksocial_backend.domain.user;

/**
 * Roles disponibles para controlar permisos y funcionalidades de usuario.
 *
 * Se almacenan como texto cuando se usan con
 * {@code @Enumerated(EnumType.STRING)}
 * para mantener valores legibles y estables en base de datos.
 *
 * @author Jorge
 * @since 2026
 * @version 1.0
 */
public enum Role {
  /** Usuario registrado con acceso básico a la plataforma. */
  REGISTERED,
  /** Usuario registrado con suscripción mensual activa. */
  SUBSCRIBED,
  /** Administrador con privilegios completos de gestión. */
  ADMIN
}
