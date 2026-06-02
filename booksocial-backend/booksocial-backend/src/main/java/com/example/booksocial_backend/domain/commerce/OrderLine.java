package com.example.booksocial_backend.domain.commerce;

import com.example.booksocial_backend.domain.catalog.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

/**
 * Entidad que representa una línea dentro de un pedido.
 *
 * Cada línea corresponde a un producto concreto
 * y la cantidad adquirida.
 *
 * Guarda también el precio unitario aplicado en el momento de compra para
 * conservar el histórico aunque cambie el precio actual del producto.
 *
 * @author Jorge
 * @since 12/03/2026
 */

@Entity
@Table(name = "ORDER_LINE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLine {

  /**
   * IDr de la línea de pedido.
   * El id es autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Cantidad comprada.
   * No puede ser nula y tiene que tener un numero positivo
   */
  @NotNull
  @Positive
  private Integer quantity;

  /**
   * Precio unitario del producto en el momento de la compra.
   * No puede ser nulo y tiene que ser positivo o 0
   */
  @NotNull
  @PositiveOrZero
  private Double unitaryPrice;

  /**
   * Pedido al que pertenece esta línea.
   * Relación muchos a uno obligatoria: un pedido puede contener varias líneas.
   * No puede ser nulo
   * 
   */
  @ManyToOne
  @NotNull
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  /**
   * Producto comprado.
   * Relación muchos a uno obligatoria. La clave foránea explícita facilita
   * identificar la restricción en base de datos
   */
  @ManyToOne
  @NotNull
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderline_product"))
  private Product product;
}
