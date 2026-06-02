/**
 * api/services/worksService.js — Servicio CRUD de Obras.
 *
 * Consume los endpoints del backend:
 *   GET    /api/works       → lista todas las obras
 *   POST   /api/works       → crea una nueva obra
 *   PUT    /api/works/{id}  → actualiza una obra existente
 *   DELETE /api/works/{id}  → elimina una obra
 *
 * Los componentes Vue nunca llaman a axios directamente;
 * siempre usan este servicio.
 */

// Codigo de la ilustracion 53

import apiClient from '@/api/axios'

export const worksService = {
  /**
   * Lista todas las obras del catálogo.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/works')
    // El backend puede devolver un array o un Page<> — normalizamos
    return Array.isArray(data) ? data : (data.content ?? [])
  },

  /**
   * Crea una nueva obra.
   * @param {{ title, description, type, genre, demographic, publicationDate, img, averageRating, authors }} payload
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/works', payload)
    return data
  },

  /**
   * Actualiza una obra existente.
   * @param {number} id
   * @param {{ title, description, type, genre, demographic, publicationDate, img, averageRating, authors }} payload
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/works/${id}`, payload)
    return data
  },

  /**
   * Elimina una obra por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/works/${id}`)
  },
}
