package com.example.booksocial_backend.domain.social;

import java.time.LocalDateTime;

import com.example.booksocial_backend.domain.catalog.Author;
import com.example.booksocial_backend.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa el seguimiento de autores por parte de usuarios.
 *
 * La restricción única de la tabla evita que un usuario siga dos veces al mismo
 * autor. Actúa como entidad intermedia entre {@link User} y {@link Author} y
 * permite guardar la fecha del seguimiento.
 *
 * @author Jorge
 * @since 2026
 * @version 1.0
 */
@Entity
@Table(name = "AUTHOR_FOLLOW", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "author_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorFollow {

    /**
     * ID único del seguimiento de autor.
     * El id es autogenerado
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que sigue al autor.
     * Relación muchos a uno obligatoria: un usuario puede seguir a varios
     * autores.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Autor seguido por el usuario.
     * Relación muchos a uno obligatoria: un autor puede ser seguido por varios
     * usuarios.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    /**
     * Fecha y hora en la que se creó el seguimiento.
     */
    @Column(nullable = false)
    private LocalDateTime followDate;
}
