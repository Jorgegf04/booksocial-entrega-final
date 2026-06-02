package com.example.booksocial_backend.domain.commerce;

/**
 * Estados del ciclo de vida logístico en la aplicación.
 * La transición válida es lineal y unidireccional:
 * El servicio {@code TrackingOrderServiceImpl} valida que no se puedan añadir
 * nuevos estados a un pedido ya entregado o cancelado.
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
public enum TrackingOrderStatus {
  CANCELED,
  PREPARING,
  SHIPPED,
  IN_TRANSIT,
  DELIVERED
}
