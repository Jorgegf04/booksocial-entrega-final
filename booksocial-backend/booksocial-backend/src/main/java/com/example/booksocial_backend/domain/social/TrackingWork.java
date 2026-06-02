package com.example.booksocial_backend.domain.social;

import java.time.LocalDateTime;

import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad que representa el seguimiento de una obra por parte de un usuario.
 *
 * Esta entidad forma parte del sistema social de BookSocial y permite a los
 * usuarios
 * seguir obras para recibir información, consultar contenido o marcar interés.
 *
 * Las relaciones principales de la entidad son uno a muchos con la entidad
 * usuario a TrackingWork y la relacion de work a TrackingWork que de uno a
 * mucho
 *
 * 
 * Se guarda en la tabla TRACKING_WORK y funciona como entidad intermedia con
 * datos propios, como el estado y la fecha de seguimiento.
 *
 * @author Jorge
 * @since 2026
 * @version 1.0
 */
@Entity
@Table(name = "TRACKING_WORK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingWork {

  /**
   * ID único del seguimiento.
   * El id es autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Estado del seguimiento de la obra.
   * No puede ser nulo
   * En la columna no puede ser nulo
   */
  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrackingWorkStatus status = TrackingWorkStatus.PENDING;

  /**
   * Usuario que realiza el seguimiento de la obra.
   * Relación muchos a uno obligatoria con {@link User}. Un usuario puede seguir
   * muchas obras
   * No puede ser nulo.
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * Obra que está siendo seguida.
   * Relación muchos a uno obligatoria con {@link Work}. Una obra puede ser
   * seguida por muchos usuarios.
   * No puede ser nulo
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  /**
   * Fecha en la que el usuario comienza a seguir la obra.
   * No puede ser nulo
   */
  @NotNull
  @Column(nullable = false)
  private LocalDateTime date;
}
