package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.example.booksocial_backend.DTO.catalog.ProductRequestDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.domain.catalog.Editorial;
import com.example.booksocial_backend.domain.catalog.Product;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.repository.EditionRepository;
import com.example.booksocial_backend.repository.ProductRepository;
import com.example.booksocial_backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private EditionRepository editionRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private ProductServiceImpl service;

  private Product product;
  private Edition edition;

  @BeforeEach
  void setUp() {
    Editorial editorial = new Editorial();
    editorial.setName("Planeta");

    Work work = new Work();
    work.setId(1L);
    work.setTitle("Naruto");

    edition = new Edition();
    edition.setId(1L);
    edition.setTitle("Naruto Vol 1");
    edition.setIsbn("123");
    edition.setTotalTomes(72);
    edition.setWork(work);
    edition.setEditorial(editorial);

    product = new Product();
    product.setId(1L);
    product.setPrice(10.0);
    product.setStock(10);
    product.setEdition(edition);
  }

  @Test
  void shouldCreateProductSuccessfully() {
    ProductRequestDTO request = new ProductRequestDTO(10.0, 10, 1L);

    when(modelMapper.map(request, Product.class)).thenReturn(product);
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));
    when(productRepository.save(any())).thenReturn(product);

    var result = service.createProduct(request);

    assertEquals(10.0, result.getPrice());
    assertEquals("Naruto Vol 1", result.getEditionTitle());
  }

  @Test
  void shouldThrowExceptionWhenEditionNotFound() {
    ProductRequestDTO request = new ProductRequestDTO(10.0, 10, 1L);

    when(modelMapper.map(request, Product.class)).thenReturn(product);
    when(editionRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createProduct(request));
  }

  @Test
  void shouldThrowExceptionWhenInvalidPrice() {
    product.setPrice(0.0);
    ProductRequestDTO request = new ProductRequestDTO(0.0, 10, 1L);

    when(modelMapper.map(request, Product.class)).thenReturn(product);
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));

    assertThrows(IllegalArgumentException.class, () -> service.createProduct(request));
  }

  @Test
  void shouldGetProductById() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    var result = service.getProductById(1L);

    assertEquals(1L, result.getId());
    assertEquals("Naruto", result.getWorkTitle());
  }

  @Test
  void shouldThrowExceptionWhenProductNotFound() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.getProductById(1L));
  }

  @Test
  void shouldReturnAllProducts() {
    when(productRepository.findAll()).thenReturn(List.of(product));

    var result = service.getAllProducts();

    assertEquals(1, result.size());
  }

  @Test
  void shouldReturnAvailableProducts() {
    when(productRepository.findByStockGreaterThan(0)).thenReturn(List.of(product));

    var result = service.getAvailableProducts();

    assertEquals(1, result.size());
  }

  @Test
  void shouldReturnProductsByEdition() {
    when(productRepository.findByEditionId(1L)).thenReturn(List.of(product));

    var result = service.getProductsByEdition(1L);

    assertEquals(1, result.size());
  }

  @Test
  void shouldUpdateProductSuccessfully() {
    ProductRequestDTO request = new ProductRequestDTO(20.0, 5, 1L);
    Product updatedProduct = new Product();
    updatedProduct.setPrice(20.0);
    updatedProduct.setStock(5);

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(modelMapper.map(request, Product.class)).thenReturn(updatedProduct);
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));
    when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.updateProduct(1L, request);

    assertEquals(20.0, result.getPrice());
    assertEquals(5, result.getStock());
  }
}
