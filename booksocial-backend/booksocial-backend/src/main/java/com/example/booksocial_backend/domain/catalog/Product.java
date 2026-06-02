package com.example.booksocial_backend.domain.catalog;

import java.util.ArrayList;
import java.util.List;

import com.example.booksocial_backend.domain.commerce.OrderLine;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Entidad que representa un producto dentro
 * del sistema de compra de de aplicación.
 *
 * Un producto está asociado a una edición concreta y contiene
 * la información necesaria para su venta como precio y stock.
 *
 * La entidad se guarda en PRODUCT y actúa como punto de unión entre catálogo y
 * pedidos.
 *
 * @author Jorge
 * @since 16/03/2026
 * @version 1.0
 */

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  /**
   * ID único del producto.
   * El id es autogenerado
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Precio actual del producto.
   * No puede ser nulo, tiene que ser un precio positivo
   */
  @NotNull
  @Positive
  @Column(nullable = false)
  private Double price;

  /**
   * Cantidad disponible en stock.
   * Tiene un minimo de 0 para que no puedas poner negativos
   * La columna en la base de datos no puede ser falsa
   */
  @NotNull
  @Min(0)
  @Column(nullable = false)
  private Integer stock;

  /**
   * Edición a la que pertenece el producto.
   * Relación muchos a uno obligatoria: una edición puede tener uno o varios
   * productos comerciales asociados.
   * No puede ser nulo
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "edition_id", nullable = false)
  private Edition edition;

  /**
   * Líneas de pedido en las que aparece este producto.
   * Relación uno a muchos con {@link OrderLine}. Se mantiene para poder consultar
   * el histórico de ventas del producto sin borrar pedidos ya realizados.
   */
  @Builder.Default
  @OneToMany(mappedBy = "product", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private List<OrderLine> orderLines = new ArrayList<>();
}
