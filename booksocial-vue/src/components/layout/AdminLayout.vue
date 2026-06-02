<!--
  AdminLayout.vue — Layout base del panel de administración.

  Estructura (igual que el Thymeleaf):
    .admin-body
      .admin-layout
        AdminSidebar (fijo a la izquierda, 240px)
        main.admin-main
          RouterView (cada vista admin se renderiza aquí)

  Todas las rutas bajo /admin/* usan este layout como padre,
  de modo que el sidebar siempre está visible sin re-renderizarse
  al cambiar de vista.
-->
<script setup>
import AdminSidebar from './AdminSidebar.vue'
</script>

<template>
  <div class="admin-body">
    <div class="admin-layout">
      <!-- Sidebar fijo: no se re-monta al cambiar de ruta -->
      <AdminSidebar />

      <!-- Área de contenido: cada vista admin ocupa este espacio -->
      <main class="admin-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>
