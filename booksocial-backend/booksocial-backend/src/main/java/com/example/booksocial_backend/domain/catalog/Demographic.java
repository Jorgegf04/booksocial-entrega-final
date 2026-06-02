package com.example.booksocial_backend.domain.catalog;

/**
 * Demografía de una obra del catálogo.
 *
 * <p>
 * La demografía la he utilizado la poder distinguir sobretodo tipso de mangas
 * </p>
 * <ul>
 * <li>{@code SHONEN} — chicos jóvenes (12-18 años)</li>
 * <li>{@code SEINEN} — hombres adultos</li>
 * <li>{@code JOSEI} — mujeres adultas</li>
 * <li>{@code KODOMO} — niños</li>
 * <li>{@code SHOJO} — chicas jóvenes</li>
 * </ul>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
public enum Demographic {
  SHONEN,
  SEINEN,
  JOSEI,
  KODOMO,
  SHOJO
}