//REF-05.BOOKSOCIA-VUE/SRC/API/AXIOS.JS
/**
 * Este archivo es el punto central desde donde el frontend se comunica con el backend
 *
 * La idea principal es esta: en vez de usar axios directamente en cada componente Vue, se crea una instancia llamada apliente,
 * y todos los servicios como authorService, workService, orderService, etc usan esa misma isntancia
 * Desde este archivo configura la comunicación con el backend, añade automaticamente el token JWT, centraliza los errores, muestra mensajes de error,
 * muestra mensajes al usuario y evita repetir la misma lógica en todos los servicios
 */

import axios from 'axios' // sirve para hacer peticiones HTTP al backend
import { useToast } from 'vue-toastification' // sirve para mostrar mensajes visualess al usuario cuando hay errores
import { logger } from '@/utils/logger' // sirve para registrar avisos o errores en consola de forma controlada

/**
 * Cliente HTTP base de la aplicación.
 *
 * La URL base nunca va hardcoded: siempre se lee desde variables de entorno.
 * La URL base esta en .env.development y .env.production
 * @type {import('axios').AxiosInstance}
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

/**
 * Es donde se guarda la sesión del usuario en localStorage. Aqui es donde esta almacenado el token JWT cuando el usuario ha iniciado sesión
 *
 * @type {string}
 */
const AUTH_STORAGE_KEY = 'booksocial-auth'

/**
 * Recupera el token del localStorage si no encuentra devuelve nulo
 *
 * Funciona de la siguiente manera, busca en localStorage el varlo de booksocial-auth
 * Si existe lo convierte en JSON a objeto y delve el .token
 * Si no existe devuelve nulo
 * Si hay algún error de parseo tambien devuelve nulo
 * Esto evita que el la aplicación se rompa si no hay localStorage o esta corrupto
 * @returns {string|null}
 */
function getStoredToken() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw).token : null
  } catch {
    return null
  }
}

function formatBackendError(data, fallback) {
  const fieldErrors = Array.isArray(data?.fieldErrors)
    ? data.fieldErrors.map((item) => item?.message).filter(Boolean)
    : []

  if (fieldErrors.length > 0) {
    return [...new Set(fieldErrors)].join(' ')
  }

  return data?.message || data?.error || fallback
}

/**
 * Interceptor de REQUEST.
 *
 * Añade el token JWT a cada petición autenticada cuando existe sesión guardada.
 */
apiClient.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/**
 * Interceptor de RESPONSE.
 *
 * Centraliza la gestión de errores HTTP, los mensajes al usuario y el cierre
 * de sesión cuando el backend responde con 401.
 */
apiClient.interceptors.response.use(
  // Respuestas 2xx: las pasamos sin modificar
  (response) => response,

  // Respuestas de error
  (error) => {
    // Peticiones marcadas como silent no muestran toasts (ej: polling, lookups opcionales)
    if (error.config?.silent) {
      return Promise.reject(error)
    }

    const toast = useToast()
    const status = error.response?.status
    const backendMsg = formatBackendError(error.response?.data)
    const ctx = {
      method: error.config?.method?.toUpperCase(),
      url: error.config?.url,
      status,
    }

    if (status === 401) {
      logger.warn('Sesión expirada o no autorizado — cerrando sesión', ctx)
      localStorage.removeItem(AUTH_STORAGE_KEY)
      window.location.href = '/login'
    } else if (status === 403) {
      logger.warn('Acceso denegado', ctx)
      toast.error('No tienes permisos para realizar esta acción.')
    } else if (status === 404) {
      logger.warn('Recurso no encontrado', ctx)
      toast.error(backendMsg || 'El recurso solicitado no existe.')
    } else if (status === 409) {
      logger.warn('Conflicto de datos', ctx)
      toast.error(backendMsg || 'Ya existe un registro con esos datos.')
    } else if (status === 422) {
      logger.warn('Datos no procesables', ctx)
      toast.error(backendMsg || 'Los datos enviados no son válidos.')
    } else if (status >= 400 && status < 500) {
      logger.warn('Error de cliente', ctx)
      toast.error(
        backendMsg || 'Revisa los datos introducidos. Hay algun campo incorrecto o incompleto.'
      )
    } else if (status >= 500) {
      logger.error('Error del servidor', ctx)
      toast.error('Error del servidor. Inténtalo de nuevo más tarde.')
    } else if (error.code === 'ECONNABORTED' || error.message?.toLowerCase().includes('timeout')) {
      logger.error('Timeout de red', ctx)
      toast.error('La solicitud ha tardado demasiado. Comprueba tu conexión.')
    } else if (!status) {
      // Sin respuesta: red caída, CORS, backend no disponible
      logger.error('Sin respuesta del servidor — posible problema de red o CORS', ctx)
      toast.error('No se pudo conectar con el servidor. Comprueba tu conexión.')
    }

    return Promise.reject(error)
  }
)

export default apiClient
