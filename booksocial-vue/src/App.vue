<!--
  App.vue — Componente raíz de la aplicación.

  Solo contiene <RouterView />. El layout completo (Sidebar + contenido)
  está encapsulado en AdminLayout.vue, que las rutas protegidas cargan
  como componente padre (layout anidado de Vue Router).

  Las rutas públicas (/login) renderizan su propio componente directamente.
-->
<script setup>
// Sin lógica propia: el routing gestiona qué se muestra
</script>

<template>
  <RouterView />
</template>
