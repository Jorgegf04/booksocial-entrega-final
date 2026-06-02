package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.example.booksocial_backend.DTO.commerce.OrderLineRequestDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.domain.catalog.Product;
import com.example.booksocial_backend.domain.commerce.Order;
import com.example.booksocial_backend.domain.commerce.OrderLine;
import com.example.booksocial_backend.repository.OrderLineRepository;
import com.example.booksocial_backend.repository.OrderRepository;
import com.example.booksocial_backend.repository.ProductRepository;
import com.example.booksocial_backend.service.impl.OrderLineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderLineServiceImplTest {

  @Mock
  private OrderLineRepository orderLineRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderLineServiceImpl service;

  private Product product;
  private Order order;
  private OrderLine orderLine;

  @BeforeEach
  void setUp() {
    Edition edition = new Edition();
    edition.setTitle("Naruto");

    product = new Product();
    product.setId(1L);
    product.setPrice(10.0);
    product.setStock(10);
    product.setEdition(edition);

    order = new Order();
    order.setId(1L);

    orderLine = new OrderLine();
    orderLine.setId(1L);
    orderLine.setProduct(product);
    orderLine.setOrder(order);
    orderLine.setQuantity(2);
    orderLine.setUnitaryPrice(10.0);
  }

  @Test
  void shouldCreateOrderLineSuccessfully() {
    OrderLineRequestDTO request = new OrderLineRequestDTO(1L, 1L, 2);

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderLineRepository.save(any())).thenReturn(orderLine);

    var result = service.createOrderLine(request);

    assertEquals(1L, result.getProductId());
    assertEquals("Naruto", result.getTitle());
    assertEquals(20.0, result.getSubtotal());
    assertEquals(8, product.getStock());
  }

  @Test
  void shouldThrowExceptionWhenRequestIsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.createOrderLine(null));
  }

  @Test
  void shouldThrowExceptionWhenProductNotFound() {
    OrderLineRequestDTO request = new OrderLineRequestDTO(1L, 1L, 2);

    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createOrderLine(request));
  }

  @Test
  void shouldThrowExceptionWhenOrderNotFound() {
    OrderLineRequestDTO request = new OrderLineRequestDTO(1L, 1L, 2);

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createOrderLine(request));
  }

  @Test
  void shouldThrowExceptionWhenStockIsInsufficient() {
    product.setStock(1);
    OrderLineRequestDTO request = new OrderLineRequestDTO(1L, 1L, 2);

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    assertThrows(IllegalArgumentException.class, () -> service.createOrderLine(request));
  }
}
