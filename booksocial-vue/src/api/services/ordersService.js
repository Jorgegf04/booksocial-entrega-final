/**
 * api/services/ordersService.js — Servicio de Pedidos y Seguimiento.
 *
 * Consume los endpoints del backend:
 *   GET  /api/orders                         → lista todos los pedidos
 *   GET  /api/tracking-orders/order/{id}/latest  → obtiene el último seguimiento
 *   POST /api/tracking-orders                → crea un nuevo estado de seguimiento
 */

import apiClient from '@/api/axios'

export const ordersService = {
  /**
   * Lista todos los pedidos.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/orders')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Obtiene el último estado de seguimiento de un pedido.
   * Devuelve null si el pedido todavía no tiene seguimiento.
   * @param {number} orderId
   * @returns {Promise<Object|null>}
   */
  async obtenerUltimoTracking(orderId) {
    try {
      // silent: true → el interceptor no muestra toast si el pedido no tiene tracking (404)
      const { data } = await apiClient.get(`/tracking-orders/order/${orderId}/latest`, { silent: true })
      return data
    } catch {
      return null
    }
  },

  /**
   * Registra un nuevo estado de seguimiento para un pedido.
   * @param {number} orderId
   * @param {{ status: string }} payload
   * @returns {Promise<Object>}
   */
  async actualizarTracking(orderId, payload) {
    // Backend: POST /api/tracking-orders con {orderId, trackingStatus}
    // No existe PUT /tracking-orders/{id} en el backend
    const { data } = await apiClient.post('/tracking-orders', {
      orderId,
      trackingStatus: payload.status,
    })
    return data
  },
}
