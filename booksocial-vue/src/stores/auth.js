/**
 * Este archivo define el store de autenticación de la app Vue usando Pinia
 * Este archivo se encarga principalmente de quien esta loggueado, su token JWT, su rol y tiene las funciones para iniciar y cerrar sesión. 

 */

import { defineStore } from 'pinia' // Sirve para crear un store de Pinia
import { ref, computed } from 'vue' // ref crea variables reactivas y computed crea valores calculados
import { authService } from '@/api/services/authService' // llama al backend para hacer login
import { logger } from '@/utils/logger' // registra mensjes como sesión iniciada o sesión cerrada

// Este codigo crea el store llamado auth.

export const useAuthStore = defineStore(
  'auth',
  () => {
    const token = ref(null)
    const user = ref(null)
    const isAuthenticated = computed(() => !!token.value)

    /**
     * Inicia sesión: llama al backend y almacena token + datos de usuario.
     * Lanza un error si las credenciales son incorrectas (lo maneja LoginView).
     * @param {{ username: string, password: string }} credenciales
     */
    async function login(credenciales) {
      const data = await authService.login(credenciales)
      token.value = data.token
      user.value = {
        id: data.userId,
        username: data.username,
        role: data.role,
      }
      logger.info(`Sesión iniciada como ${data.username} (${data.role})`)
    }

    /**
     * Cierra sesión: limpia el estado del store.
     * La navegación al login la gestiona el guard del router o el interceptor 401.
     */
    function logout() {
      token.value = null
      user.value = null
      logger.info('Sesión cerrada')
    }

    return { token, user, isAuthenticated, login, logout }
  },
  {
    // Persistencia en localStorage con clave 'booksocial-auth'
    persist: {
      key: 'booksocial-auth',
      storage: localStorage,
    },
  }
)
