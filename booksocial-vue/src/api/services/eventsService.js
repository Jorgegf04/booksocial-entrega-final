/**
 * api/services/eventsService.js — Servicio CRUD de Eventos.
 *
 * Consume los endpoints del backend:
 *   GET    /api/events       → lista todos los eventos
 *   POST   /api/events       → crea un nuevo evento
 *   PUT    /api/events/{id}  → actualiza un evento
 *   DELETE /api/events/{id}  → elimina un evento
 */

import apiClient from '@/api/axios'

export const eventsService = {
  /**
   * Lista todos los eventos.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/events')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea un nuevo evento.
   * @param {Object} payload Datos del evento.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/events', payload)
    return data
  },

  /**
   * Actualiza un evento existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados del evento.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/events/${id}`, payload)
    return data
  },

  /**
   * Elimina un evento por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/events/${id}`)
  },
}
