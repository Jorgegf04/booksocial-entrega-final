<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { worksService } from '@/api/services/worksService'
import { productsService } from '@/api/services/productsService'
import { eventsService } from '@/api/services/eventsService'
import { ordersService } from '@/api/services/ordersService'
import { usersService } from '@/api/services/usersService'

const authStore = useAuthStore()
const nombreAdmin = computed(() => authStore.user?.username ?? 'Administrador')

const cargando = ref(true)

const obras = ref([])
const productos = ref([])
const eventos = ref([])
const pedidos = ref([])
const usuarios = ref([])

onMounted(async () => {
  try {
    const [obrasData, productosData, eventosData, pedidosData, usuariosData] = await Promise.all([
      worksService.listar(),
      productsService.listar(),
      eventsService.listar(),
      ordersService.listar(),
      usersService.listar(),
    ])
    obras.value = obrasData
    productos.value = productosData
    eventos.value = eventosData
    pedidos.value = pedidosData
    usuarios.value = usuariosData
  } catch {
    // Errores ya gestionados por interceptor
  } finally {
    cargando.value = false
  }
})

// ─── Métricas derivadas ───────────────────────────────────────────────────────
const ventasTotales = computed(() => {
  const total = pedidos.value.reduce((sum, p) => sum + (p.total ?? 0), 0)
  return `€${total.toFixed(2)}`
})

const nuevosMiembrosMes = computed(() => {
  const ahora = new Date()
  return usuarios.value.filter((u) => {
    if (!u.registrationDate) return false
    const d = new Date(u.registrationDate)
    return d.getFullYear() === ahora.getFullYear() && d.getMonth() === ahora.getMonth()
  }).length
})

const alertasStock = computed(() => productos.value.filter((p) => (p.stock ?? 0) <= 5).length)

const totalPedidos = computed(() => pedidos.value.length)

const metricas = computed(() => [
  { label: 'Ventas totales',       valor: ventasTotales.value,          icono: 'euro',            clase: 'admin-metric-green'  },
  { label: 'Nuevos miembros (mes)', valor: nuevosMiembrosMes.value,     icono: 'person_add',      clase: 'admin-metric-blue'   },
  { label: 'Alertas de stock',      valor: alertasStock.value,          icono: 'warning',         clase: 'admin-metric-amber'  },
  { label: 'Pedidos totales',       valor: totalPedidos.value,          icono: 'pending_actions', clase: 'admin-metric-purple' },
])

// ─── Listas filtradas para las tarjetas ──────────────────────────────────────
const ultimasObras = computed(() => obras.value.slice(0, 6))

const stockBajo = computed(() =>
  productos.value.filter((p) => (p.stock ?? 0) <= 5).slice(0, 5)
)

const proximosEventos = computed(() => {
  const ahora = new Date()
  return eventos.value
    .filter((e) => e.date && new Date(e.date) > ahora)
    .sort((a, b) => new Date(a.date) - new Date(b.date))
    .slice(0, 5)
})

const pedidosRecientes = computed(() => pedidos.value.slice(0, 8))

// ─── Helpers de formato ───────────────────────────────────────────────────────
function formatEnum(str) {
  if (!str) return '—'
  return str.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase())
}

function formatFechaCorta(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' })
}

function formatFechaHora(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function claseStock(stock) {
  return (stock ?? 0) === 0 ? 'admin-stock-empty' : 'admin-stock-low'
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Dashboard</h1>
        <p class="admin-page-subtitle">Bienvenido, {{ nombreAdmin }}. Resumen general del sistema.</p>
      </div>
      <a
        href="https://localhost/catalog"
        target="_blank"
        rel="noopener noreferrer"
        class="btn btn-primary dashboard-catalog-btn d-flex align-items-center gap-1"
      >
        <span class="material-symbols-outlined dashboard-catalog-icon">open_in_new</span>
        Ver catálogo
      </a>
    </div>

    <!-- Estado de carga global -->
    <div v-if="cargando" class="text-center py-5">
      <div class="spinner-border text-secondary" role="status"></div>
    </div>

    <template v-else>
      <!-- Tarjetas de métricas -->
      <div class="admin-metrics">
        <div
          v-for="m in metricas"
          :key="m.label"
          class="admin-metric-card"
          :class="m.clase"
        >
          <div class="admin-metric-icon">
            <span class="material-symbols-outlined">{{ m.icono }}</span>
          </div>
          <div class="admin-metric-value">{{ m.valor }}</div>
          <div class="admin-metric-label">{{ m.label }}</div>
        </div>
      </div>

      <!-- Grid: obras + columna lateral -->
      <div class="admin-content-grid">

        <!-- Tabla de obras del catálogo -->
        <div class="admin-card">
          <div class="admin-card-header">
            <p class="admin-card-title">Catálogo de obras</p>
            <RouterLink to="/admin/obras" class="admin-card-action">Ver todo →</RouterLink>
          </div>
          <div class="table-responsive">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>Obra</th>
                  <th>Género</th>
                  <th>Tipo</th>
                  <th>Valoración</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="obra in ultimasObras" :key="obra.id">
                  <td>
                    <div class="admin-work-cell">
                      <div
                        class="admin-work-thumb"
                        :style="obra.img ? `background-image:url(${obra.img})` : ''"
                      >
                        <span v-if="!obra.img" class="material-symbols-outlined dashboard-work-thumb-icon">menu_book</span>
                      </div>
                      <div>
                        <div class="admin-work-title">{{ obra.title }}</div>
                        <div class="admin-work-authors">
                          {{ obra.authors?.join(', ') || '—' }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="admin-badge">{{ formatEnum(obra.genre) }}</span>
                  </td>
                  <td class="admin-table-meta">{{ obra.type ?? '—' }}</td>
                  <td>
                    <span v-if="obra.averageRating != null" class="admin-work-rating">
                      {{ obra.averageRating.toFixed(1) }} ★
                    </span>
                    <span v-else>—</span>
                  </td>
                </tr>
                <tr v-if="ultimasObras.length === 0">
                  <td colspan="4" class="text-center py-4 dashboard-empty-cell">
                    No hay obras disponibles
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Columna lateral -->
        <div class="d-flex flex-column gap-4">

          <!-- Stock bajo -->
          <div class="admin-card">
            <div class="admin-card-header">
              <p class="admin-card-title">Stock bajo</p>
              <RouterLink to="/admin/inventario" class="admin-card-action">Ver inventario →</RouterLink>
            </div>
            <div class="d-flex flex-column gap-2">
              <div v-for="(p, idx) in stockBajo" :key="idx" class="admin-stock-row">
                <div>
                  <div class="dashboard-stock-title">{{ p.workTitle }}</div>
                  <div class="dashboard-stock-editorial">{{ p.editorialName }}</div>
                </div>
                <span class="admin-stock-badge" :class="claseStock(p.stock)">
                  {{ p.stock ?? 0 }} uds.
                </span>
              </div>
              <div v-if="stockBajo.length === 0" class="text-center py-3 dashboard-card-empty">
                Sin alertas de stock
              </div>
            </div>
          </div>

          <!-- Próximos eventos -->
          <div class="admin-card">
            <div class="admin-card-header">
              <p class="admin-card-title">Próximos eventos</p>
              <RouterLink to="/admin/eventos" class="admin-card-action">Ver todos →</RouterLink>
            </div>
            <div class="d-flex flex-column gap-3">
              <div v-for="evento in proximosEventos" :key="evento.id" class="admin-event-row">
                <div class="admin-event-dot"></div>
                <div class="flex-grow-1">
                  <div class="dashboard-event-title">{{ evento.title }}</div>
                  <div class="dashboard-event-date">{{ formatFechaCorta(evento.date) }}</div>
                </div>
                <span class="dashboard-event-participants">
                  {{ evento.totalParticipants ?? 0 }} inscritos
                </span>
              </div>
              <div v-if="proximosEventos.length === 0" class="text-center py-3 dashboard-card-empty">
                No hay eventos próximos
              </div>
            </div>
          </div>

          <!-- Resumen de usuarios -->
          <div class="admin-card">
            <div class="admin-card-header">
              <p class="admin-card-title">Usuarios</p>
              <RouterLink to="/admin/usuarios" class="admin-card-action">Ver todos →</RouterLink>
            </div>
            <div class="d-flex flex-column gap-2">
              <div class="d-flex justify-content-between align-items-center">
                <span class="dashboard-stat-label">Total registrados</span>
                <span class="dashboard-stat-value">{{ usuarios.length }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="dashboard-stat-label">Nuevos este mes</span>
                <span class="dashboard-stat-value--positive">{{ nuevosMiembrosMes }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="dashboard-stat-label">Total pedidos</span>
                <span class="dashboard-stat-value">{{ totalPedidos }}</span>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- Tabla de pedidos recientes -->
      <div class="admin-card mt-4">
        <div class="admin-card-header">
          <p class="admin-card-title">Pedidos recientes</p>
          <RouterLink to="/admin/pedidos" class="admin-card-action">Ver todos →</RouterLink>
        </div>
        <div class="table-responsive">
          <table class="admin-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Usuario</th>
                <th>Fecha</th>
                <th>Total</th>
                <th>Artículos</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="pedido in pedidosRecientes" :key="pedido.id">
                <td class="admin-order-id">#{{ pedido.id }}</td>
                <td class="admin-table-meta">{{ pedido.username ?? '—' }}</td>
                <td class="admin-table-meta">{{ formatFechaHora(pedido.date) }}</td>
                <td class="admin-inv-price">€{{ (pedido.total ?? 0).toFixed(2) }}</td>
                <td class="admin-table-meta">
                  {{ pedido.totalItems != null ? `${pedido.totalItems} uds.` : '—' }}
                </td>
              </tr>
              <tr v-if="pedidosRecientes.length === 0">
                <td colspan="5" class="text-center py-4 dashboard-empty-cell">
                  No hay pedidos registrados
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>
