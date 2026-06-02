//REF-06.BOOKSOCIAL-VUE/ROUTER/INDEX.JS
/**
 * Este archivo define la navegación del frontend de Vue, que URL carga cada vista, que rutas estan protegidas y que pasa si alguien intenta entrar sin permiso
 */

import { createRouter, createWebHistory } from 'vue-router' // Crea el router de view y permite usar urls normales como /admin/dashboard
import { jwtDecode } from 'jwt-decode' // sirve para leer el contenido del token JWT
import { useAuthStore } from '@/stores/auth' // permite consultar el estado del login del usuario

// ARRAY DE RUTAS
const routes = [
  // Ruta pública: login
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false },
  },

  // Rutas protegidas: panel administración
  {
    path: '/admin',
    component: () => import('@/components/layout/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
      },
      {
        path: 'obras',
        name: 'Works',
        component: () => import('@/views/admin/WorksView.vue'),
      },
      {
        path: 'autores',
        name: 'Authors',
        component: () => import('@/views/admin/AuthorsView.vue'),
      },
      {
        path: 'editoriales',
        name: 'Editorials',
        component: () => import('@/views/admin/EditorialsView.vue'),
      },
      {
        path: 'ediciones',
        name: 'Editions',
        component: () => import('@/views/admin/EditionsView.vue'),
      },
      {
        path: 'tomos',
        name: 'Tomes',
        component: () => import('@/views/admin/TomesView.vue'),
      },
      {
        path: 'capitulos',
        name: 'Chapters',
        component: () => import('@/views/admin/ChaptersView.vue'),
      },
      {
        path: 'volumenes',
        name: 'Volumes',
        component: () => import('@/views/admin/VolumesView.vue'),
      },
      {
        path: 'inventario',
        name: 'Inventory',
        component: () => import('@/views/admin/InventoryView.vue'),
      },
      {
        path: 'pedidos',
        name: 'Orders',
        component: () => import('@/views/admin/OrdersView.vue'),
      },
      {
        path: 'usuarios',
        name: 'Users',
        component: () => import('@/views/admin/UsersView.vue'),
      },
      {
        path: 'comentarios',
        name: 'Comments',
        component: () => import('@/views/admin/CommentsView.vue'),
      },
      {
        path: 'eventos',
        name: 'Events',
        component: () => import('@/views/admin/EventsView.vue'),
      },
    ],
  },

  // Redige al dashboar del admin
  { path: '/', redirect: '/admin/dashboard' },

  // Cualquier ruta que no encuentra la manda directamente al login
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

// Aqui sea crea el router de la aplicación
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

/**
 * Verifica que el token JWT sea válido: existe, no ha expirado y el role es ADMIN.
 * @param {string|null} token
 * @param {string|null} role  — rol almacenado en el store
 * @returns {boolean}
 */
function isValidAdminToken(token, role) {
  if (!token) return false
  try {
    const { exp } = jwtDecode(token)
    const noExpirado = exp * 1000 > Date.now()
    const esAdmin = role === 'ADMIN'
    return noExpirado && esAdmin
  } catch {
    return false
  }
}

// Lo que hace este código es comprobar si los cambios de rutas, con los datos del usuario comprueba si tiene el rol para poder acceder a esas rutas
router.beforeEach((to) => {
  const authStore = useAuthStore()

  const requiereAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiereAuth) {
    const esValido = isValidAdminToken(authStore.token, authStore.user?.role)
    if (!esValido) {
      if (authStore.token) authStore.logout()
      return { name: 'Login' }
    }
  } else if (to.name === 'Login') {
    if (isValidAdminToken(authStore.token, authStore.user?.role)) {
      return { name: 'Dashboard' }
    }
  }
})

export default router
