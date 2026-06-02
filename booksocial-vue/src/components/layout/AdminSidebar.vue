<!--
  AdminSidebar.vue — Barra lateral fija del panel de administración.

  Replica fielmente el sidebar del frontend Thymeleaf (admin/fragments/sidebar.html).
  Usa RouterLink de Vue Router en lugar de <a href>, de modo que la navegación
  es SPA (sin recargar la página) y el guard de autenticación actúa en cada cambio.

  Props: ninguna (obtiene el usuario del store de auth directamente).
  Store usado: useAuthStore → username, role, logout().

  Cómo funciona el enlace activo:
    RouterLink añade automáticamente la clase 'router-link-active' cuando la ruta
    coincide. Usamos :class para añadir también 'active' (clase de main.css)
    cuando la ruta coincide exactamente.
-->
<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()

// Inicial del nombre de usuario para el avatar (igual que en Thymeleaf)
const avatarLetra = computed(() => {
  const nombre = authStore.user?.username ?? 'A'
  return nombre.charAt(0).toUpperCase()
})

const nombreUsuario = computed(() => authStore.user?.username ?? 'Admin')

/** Cierra sesión y redirige al login */
function cerrarSesion() {
  authStore.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <aside class="admin-sidebar">
    <!-- Logo del panel -->
    <div class="admin-sidebar-logo">
      <span class="material-symbols-outlined">menu_book</span>
      <span>BookSocial</span>
    </div>
    <div class="admin-frontend-badge">
      <span class="material-symbols-outlined">view_quilt</span>
      <span>Frontend Vue</span>
    </div>

    <nav class="admin-nav">
      <!-- Sección General -->
      <p class="admin-nav-section">General</p>
      <RouterLink
        to="/admin/dashboard"
        class="admin-nav-link"
        :class="{ active: $route.path === '/admin/dashboard' }"
      >
        <span class="material-symbols-outlined">grid_view</span>Dashboard
      </RouterLink>

      <!-- Sección Catálogo -->
      <p class="admin-nav-section">Catálogo</p>
      <RouterLink
        to="/admin/obras"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/obras') }"
      >
        <span class="material-symbols-outlined">menu_book</span>Obras
      </RouterLink>
      <RouterLink
        to="/admin/autores"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/autores') }"
      >
        <span class="material-symbols-outlined">person</span>Autores
      </RouterLink>
      <RouterLink
        to="/admin/editoriales"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/editoriales') }"
      >
        <span class="material-symbols-outlined">business</span>Editoriales
      </RouterLink>
      <RouterLink
        to="/admin/ediciones"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/ediciones') }"
      >
        <span class="material-symbols-outlined">collections_bookmark</span>Ediciones
      </RouterLink>
      <RouterLink
        to="/admin/tomos"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/tomos') }"
      >
        <span class="material-symbols-outlined">import_contacts</span>Tomos
      </RouterLink>
      <RouterLink
        to="/admin/capitulos"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/capitulos') }"
      >
        <span class="material-symbols-outlined">article</span>Capítulos
      </RouterLink>
      <RouterLink
        to="/admin/volumenes"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/volumenes') }"
      >
        <span class="material-symbols-outlined">auto_stories</span>Volúmenes
      </RouterLink>

      <!-- Sección Comercio -->
      <p class="admin-nav-section">Comercio</p>
      <RouterLink
        to="/admin/inventario"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/inventario') }"
      >
        <span class="material-symbols-outlined">inventory_2</span>Inventario
      </RouterLink>
      <RouterLink
        to="/admin/pedidos"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/pedidos') }"
      >
        <span class="material-symbols-outlined">receipt_long</span>Pedidos
      </RouterLink>

      <!-- Sección Comunidad -->
      <p class="admin-nav-section">Comunidad</p>
      <RouterLink
        to="/admin/usuarios"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/usuarios') }"
      >
        <span class="material-symbols-outlined">group</span>Usuarios
      </RouterLink>
      <RouterLink
        to="/admin/comentarios"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/comentarios') }"
      >
        <span class="material-symbols-outlined">comment</span>Comentarios
      </RouterLink>
      <RouterLink
        to="/admin/eventos"
        class="admin-nav-link"
        :class="{ active: $route.path.startsWith('/admin/eventos') }"
      >
        <span class="material-symbols-outlined">event</span>Eventos
      </RouterLink>

    </nav>

    <!-- Parte inferior: accesos rápidos + chip del usuario -->
    <div class="admin-sidebar-bottom">
      <a
        href="http://localhost:8000/catalog"
        target="_blank"
        rel="noopener noreferrer"
        class="admin-nav-link"
      >
        <span class="material-symbols-outlined">library_books</span>Ver catálogo
      </a>
      <button class="admin-nav-link admin-nav-link-danger w-100 text-start border-0 bg-transparent" @click="cerrarSesion">
        <span class="material-symbols-outlined">logout</span>Cerrar sesión
      </button>

      <!-- Chip del usuario: inicial + nombre + rol -->
      <div class="admin-user-chip">
        <div class="admin-user-avatar">{{ avatarLetra }}</div>
        <div>
          <div class="admin-user-name">{{ nombreUsuario }}</div>
          <div class="admin-user-role">Administrador</div>
        </div>
      </div>
    </div>
  </aside>
</template>
