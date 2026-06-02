<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { productsService } from '@/api/services/productsService'
import { editionsService } from '@/api/services/editionsService'

const toast = useToast()

const productos = ref([])
const ediciones = ref([])
const cargando = ref(false)
const guardando = ref(false)

const refModalCrear = ref(null)
let bsModalCrear

const form = reactive({
  editionId: '',
  price: '',
  stock: '',
})

onMounted(async () => {
  bsModalCrear = new Modal(refModalCrear.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    const [productosData, edicionesData] = await Promise.all([
      productsService.listar(),
      editionsService.listar(),
    ])
    productos.value = productosData
    ediciones.value = edicionesData
  } catch {
    toast.error('No se pudo cargar el inventario.')
  } finally {
    cargando.value = false
  }
}

function resetForm() {
  Object.assign(form, {
    editionId: '',
    price: '',
    stock: '',
  })
}

function abrirModalCrear() {
  resetForm()
  bsModalCrear.show()
}

async function guardarProducto() {
  if (!form.editionId) {
    toast.warning('Debes seleccionar una edicion.')
    return
  }
  if (form.price === '' || Number(form.price) <= 0) {
    toast.warning('El precio debe ser mayor que 0.')
    return
  }
  if (form.stock === '' || Number(form.stock) < 0) {
    toast.warning('El stock no puede ser negativo.')
    return
  }

  guardando.value = true
  try {
    const nuevo = await productsService.crear({
      editionId: Number(form.editionId),
      price: Number(form.price),
      stock: Number(form.stock),
    })
    productos.value.unshift(nuevo)
    toast.success('Producto creado correctamente.')
    bsModalCrear.hide()
    resetForm()
  } catch {
    // El interceptor ya muestra el error del backend.
  } finally {
    guardando.value = false
  }
}

function claseStock(stock) {
  if (stock === 0) return 'admin-stock-empty'
  if (stock <= 5) return 'admin-stock-low'
  return 'admin-stock-ok'
}

function formatPrecio(precio) {
  return `€${(precio ?? 0).toFixed(2)}`
}
</script>

<template>
  <div>
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Inventario</h1>
        <p class="admin-page-subtitle">Productos disponibles y estado de stock</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Producto
      </button>
    </div>

    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Productos disponibles
          <span class="admin-table-count">({{ productos.length }} total)</span>
        </p>
      </div>

      <div v-if="cargando" class="text-center py-4">
        <div class="spinner-border text-secondary" role="status"></div>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Obra</th>
              <th>Editorial</th>
              <th>ISBN</th>
              <th>Precio</th>
              <th>Stock</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="producto in productos" :key="producto.id">
              <td>
                <div class="admin-table-name">{{ producto.workTitle }}</div>
                <div v-if="producto.editionTitle" class="admin-inv-edition">
                  {{ producto.editionTitle }}
                </div>
              </td>
              <td class="admin-table-meta">{{ producto.editorialName ?? '—' }}</td>
              <td class="admin-inv-isbn">{{ producto.editionIsbn ?? '—' }}</td>
              <td class="admin-inv-price">{{ formatPrecio(producto.price) }}</td>
              <td>
                <span class="admin-stock-badge" :class="claseStock(producto.stock ?? 0)">
                  {{ producto.stock ?? 0 }} uds.
                </span>
              </td>
            </tr>
            <tr v-if="!cargando && productos.length === 0">
              <td colspan="5" class="text-center py-4 admin-table-empty">
                No hay productos disponibles
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div ref="refModalCrear" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Nuevo Producto</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Edicion <span class="text-danger">*</span>
                </label>
                <select v-model="form.editionId" class="form-select form-select-sm">
                  <option value="">— Selecciona una edicion —</option>
                  <option v-for="edicion in ediciones" :key="edicion.id" :value="edicion.id">
                    {{ edicion.title }} — {{ edicion.workTitle ?? 'Sin obra' }}
                  </option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Precio (€) <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.price"
                  type="number"
                  min="0"
                  step="0.01"
                  class="form-control form-control-sm"
                  placeholder="Ej: 12.99"
                />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Stock <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.stock"
                  type="number"
                  min="0"
                  class="form-control form-control-sm"
                  placeholder="Ej: 50"
                />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="guardando"
              @click="guardarProducto"
            >
              <span v-if="guardando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Crear Producto
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
