/**
 * api/services/productsService.js — Servicio de Productos.
 *
 * Consume los endpoints del backend:
 *   GET  /api/products  → lista todos los productos
 *   POST /api/products  → crea un producto
 */

import apiClient from '@/api/axios'

export const productsService = {
  /**
   * Lista todos los productos disponibles.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/products')
    return Array.isArray(data) ? data : data.content ?? []
  },

  async crear(payload) {
    const { data } = await apiClient.post('/products', payload)
    return data
  },
}
