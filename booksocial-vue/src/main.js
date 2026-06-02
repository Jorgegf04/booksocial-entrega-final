//REF-04.BOOKSOCIAL-VUE/SRC/MAIN.JS
/**
 * main.js — Punto de entrada de la aplicación Vue.
 *
 * Registra en orden:
 *   1. Bootstrap 5 (CSS + JS bundle con Popper incluido)
 *   2. Estilos globales del proyecto (variables, admin panel, auth)
 *   3. Toastification (notificaciones)
 *   4. Pinia (estado global) con persistedstate en localStorage
 *   5. Vue Router (rutas + guards de autenticación)
 *   6. Error handler global → pasa los errores al logger
 *   7. Monta la app en #app
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// Bootstrap 5 (CSS + JS con Popper.js incluido)
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Bootstrap Icons
import 'bootstrap-icons/font/bootstrap-icons.css'

// Estilos globales del panel admin (variables, layout, tabla, badges…)
import '@/assets/styles/main.css'
import '@/assets/styles/admin-shared.css'

// Notificaciones toast
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import App from './App.vue'
import router from './router'
import { logger } from '@/utils/logger'

const app = createApp(App)

// ─── Pinia ────────────────────────────────────────────────────────────────────
// Debe registrarse ANTES que el router para que los guards puedan acceder a stores
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// ─── Router ───────────────────────────────────────────────────────────────────
app.use(router)

// ─── Toastification ───────────────────────────────────────────────────────────
app.use(Toast, {
  timeout: 3500,
  closeOnClick: true,
  pauseOnHover: true,
  position: 'top-right',
  maxToasts: 4,
})

// ─── Error handler global ─────────────────────────────────────────────────────
// Captura errores no manejados en componentes y los pasa al logger
app.config.errorHandler = (err, _instance, info) => {
  logger.error('Error global de Vue', { mensaje: err.message, info })
}

app.mount('#app')
