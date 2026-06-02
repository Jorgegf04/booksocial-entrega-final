package com.example.booksocial_backend.domain.commerce;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entidad que representa el seguimiento de un pedido.
 *
 * Permite registrar el estado logístico del pedido
 * durante el proceso de envío.
 *
 * Cada registro representa un cambio de estado del pedido, por lo que un pedido
 * puede tener varios registros de tracking ordenados por fecha.
 *
 * @author Jorge
 * @since 15/03/2026
 * @version 1.0
 */

@Entity
@Table(name = "TRACKING_ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingOrder {

  /**
   * Identificador del tracking.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Estado actual del envío.
   *
   * <p>
   * Se almacena como texto para que los valores de la base de datos sean
   * legibles y no dependan del orden del enum.
   * </p>
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS", nullable = false)
  private TrackingOrderStatus status;

  /**
   * Fecha del cambio de estado.
   */
  @NotNull
  @PastOrPresent
  @Column(nullable = false)
  private LocalDateTime date;

  /**
   * Pedido asociado.
   *
   * <p>
   * Relación muchos a uno obligatoria: un pedido puede acumular varios cambios
   * de estado durante su ciclo logístico.
   * </p>
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
}
