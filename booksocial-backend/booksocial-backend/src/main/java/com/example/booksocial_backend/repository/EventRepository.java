package com.example.booksocial_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.social.Event;

/**
 * Repositorio de eventos de BookSocial.
 *
 * Maneja la persistencia de {@link Event} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los eventos sociales publicados en la plataforma.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  /**
   * Obtiene eventos cuya fecha es posterior a la indicada.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param date fecha limite usada para filtrar resultados posteriores
   * @return lista de eventos futuros respecto a la fecha recibida
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Event> findByDateAfter(LocalDateTime date);
}
