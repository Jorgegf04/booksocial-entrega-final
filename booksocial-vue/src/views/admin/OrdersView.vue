<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { ordersService } from '@/api/services/ordersService'

const toast = useToast()

const ESTADOS = [
  { value: 'PREPARING', label: 'En preparación' },
  { value: 'SHIPPED', label: 'Enviado' },
  { value: 'IN_TRANSIT', label: 'En tránsito' },
  { value: 'DELIVERED', label: 'Entregado' },
  { value: 'CANCELED', label: 'Cancelado' },
]

// ─── Estado ───────────────────────────────────────────────────────────────────
const pedidos = ref([])
const trackingActual = ref({}) // { [orderId]: TrackingOrderResponseDTO | null }
const cargando = ref(false)
const guardando = ref(false)

const pedidoTracking = ref(null)
const formTracking = reactive({ status: '' })

// ─── Referencias a modales Bootstrap ─────────────────────────────────────────
const refModalTracking = ref(null)
let bsModalTracking

onMounted(async () => {
  bsModalTracking = new Modal(refModalTracking.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    pedidos.value = await ordersService.listar()
    // Carga el último tracking de cada pedido en paralelo
    const resultados = await Promise.all(
      pedidos.value.map((p) => ordersService.obtenerUltimoTracking(p.id))
    )
    pedidos.value.forEach((p, i) => {
      trackingActual.value[p.id] = resultados[i]
    })
  } catch {
    toast.error('No se pudieron cargar los pedidos.')
  } finally {
    cargando.value = false
  }
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function formatTotal(total) {
  return `€${(total ?? 0).toFixed(2)}`
}

function resumenLineas(lineas) {
  if (!lineas || lineas.length === 0) return '—'
  const preview = lineas.slice(0, 2).map((l) => l.title ?? '—').join(', ')
  return lineas.length > 2 ? `${preview} +${lineas.length - 2} más` : preview
}

function abrirModalTracking(pedido) {
  pedidoTracking.value = pedido
  // Pre-selecciona el estado actual para que el admin sepa el punto de partida
  formTracking.status = trackingActual.value[pedido.id]?.status ?? ''
  bsModalTracking.show()
}

async function guardarTracking() {
  if (!formTracking.status) {
    toast.warning('Selecciona un estado.')
    return
  }
  guardando.value = true
  try {
    await ordersService.actualizarTracking(pedidoTracking.value.id, { status: formTracking.status })
    toast.success(`Estado del pedido #${pedidoTracking.value.id} actualizado correctamente.`)
    bsModalTracking.hide()
    await cargarDatos()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div>
    <!-- Topbar (sin botón: pedidos los crean los usuarios) -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Pedidos</h1>
        <p class="admin-page-subtitle">Historial completo de pedidos</p>
      </div>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los pedidos
          <span class="admin-table-count">({{ pedidos.length }} total)</span>
        </p>
      </div>

      <div v-if="cargando" class="text-center py-4">
        <div class="spinner-border text-secondary" role="status"></div>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Usuario</th>
              <th>Fecha</th>
              <th>Artículos</th>
              <th>Total</th>
              <th>Estado</th>
              <th>Líneas</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pedido in pedidos" :key="pedido.id">
              <td class="admin-order-id">#{{ pedido.id }}</td>
              <td class="admin-table-meta">{{ pedido.username ?? '—' }}</td>
              <td class="admin-table-meta">{{ formatFecha(pedido.date) }}</td>
              <td class="admin-table-meta">{{ pedido.totalItems ?? '—' }}</td>
              <td class="admin-inv-price">{{ formatTotal(pedido.total) }}</td>
              <td>
                <span v-if="trackingActual[pedido.id]" class="admin-badge">
                  {{ trackingActual[pedido.id].statusLabel }}
                </span>
                <span v-else class="admin-table-meta">Sin estado</span>
              </td>
              <td>
                <div class="admin-order-lines">{{ resumenLineas(pedido.orderLines) }}</div>
              </td>
              <td>
                <button
                  class="btn btn-sm btn-outline-secondary p-1"
                  title="Actualizar estado"
                  @click="abrirModalTracking(pedido)"
                >
                  <span class="material-symbols-outlined admin-table-action-icon">local_shipping</span>
                </button>
              </td>
            </tr>
            <tr v-if="!cargando && pedidos.length === 0">
              <td colspan="8" class="text-center py-4 admin-table-empty">
                No hay pedidos registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Actualizar estado del pedido
         ===================================================================== -->
    <div ref="refModalTracking" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Actualizar estado del pedido</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-tracking-order mb-3">
              Pedido <strong>#{{ pedidoTracking?.id }}</strong>
              — Usuario: <span class="admin-tracking-username">{{ pedidoTracking?.username ?? '—' }}</span>
            </p>
            <p class="admin-tracking-note mb-3">
              El nuevo estado quedará registrado en el historial del pedido.
            </p>
            <label class="form-label fw-semibold">
              Nuevo estado <span class="text-danger">*</span>
            </label>
            <select v-model="formTracking.status" class="form-select form-select-sm">
              <option value="">— Seleccionar estado —</option>
              <option v-for="estado in ESTADOS" :key="estado.value" :value="estado.value">
                {{ estado.label }}
              </option>
            </select>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="guardando"
              @click="guardarTracking"
            >
              <span v-if="guardando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Guardar y notificar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
