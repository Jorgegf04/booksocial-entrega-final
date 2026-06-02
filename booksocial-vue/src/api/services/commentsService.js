/**
 * api/services/commentsService.js — Servicio de Comentarios.
 *
 * Consume los endpoints del backend:
 *   GET    /api/comments       → lista todos los comentarios
 *   DELETE /api/comments/{id}  → elimina un comentario
 */

import apiClient from '@/api/axios'

export const commentsService = {
  /**
   * Lista todos los comentarios.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/comments')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Elimina un comentario por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/comments/${id}`)
  },
}
