<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { eventsService } from '@/api/services/eventsService'
import { uploadService } from '@/api/services/uploadService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const eventos = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const eventoEditando = ref(null)
const eventoAEliminar = ref(null)

const form = reactive({
  title: '',
  description: '',
  date: '',
  img: '',
})
const imgFileSeleccionado = ref(null)

// ─── Referencias a modales Bootstrap ─────────────────────────────────────────
const refModalForm = ref(null)
const refModalEliminar = ref(null)
let bsModalForm, bsModalEliminar

onMounted(async () => {
  bsModalForm = new Modal(refModalForm.value)
  bsModalEliminar = new Modal(refModalEliminar.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    eventos.value = await eventsService.listar()
  } catch {
    toast.error('No se pudieron cargar los eventos.')
  } finally {
    cargando.value = false
  }
}

function estadoEvento(fecha) {
  if (!fecha) return { label: '—', clase: 'admin-event-status-none' }
  return new Date(fecha) > new Date()
    ? { label: 'Próximo', clase: 'admin-event-status-upcoming' }
    : { label: 'Finalizado', clase: 'admin-event-status-past' }
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function truncar(texto, max) {
  if (!texto) return '—'
  return texto.length > max ? texto.substring(0, max) + '…' : texto
}

function toDatetimeLocal(fechaStr) {
  if (!fechaStr) return ''
  return fechaStr.length > 16 ? fechaStr.substring(0, 16) : fechaStr
}

function resetForm() {
  Object.assign(form, { title: '', description: '', date: '', img: '' })
  imgFileSeleccionado.value = null
  const inp = document.getElementById('eImgFile')
  if (inp) inp.value = ''
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  eventoEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(evento) {
  eventoEditando.value = evento
  Object.assign(form, {
    title: evento.title ?? '',
    description: evento.description ?? '',
    date: toDatetimeLocal(evento.date),
    img: evento.img ?? '',
  })
  imgFileSeleccionado.value = null
  bsModalForm.show()
}

function abrirModalEliminar(evento) {
  eventoAEliminar.value = evento
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.title.trim()) {
    toast.warning('El título es obligatorio.')
    return
  }
  if (!form.date) {
    toast.warning('La fecha y hora son obligatorias.')
    return
  }
  guardando.value = true
  try {
    if (imgFileSeleccionado.value) {
      form.img = await uploadService.subir(imgFileSeleccionado.value)
    }
    const payload = {
      title: form.title.trim(),
      description: form.description.trim() || null,
      date: form.date,
      img: form.img || null,
    }

    if (eventoEditando.value) {
      const actualizado = await eventsService.actualizar(eventoEditando.value.id, payload)
      const idx = eventos.value.findIndex((e) => e.id === eventoEditando.value.id)
      if (idx !== -1) eventos.value[idx] = actualizado
      toast.success(`"${payload.title}" actualizado correctamente.`)
    } else {
      const nuevo = await eventsService.crear(payload)
      eventos.value.unshift(nuevo)
      toast.success(`"${payload.title}" creado correctamente.`)
    }
    bsModalForm.hide()
    resetForm()
  } catch {
    // El interceptor ya muestra el toast de error
  } finally {
    guardando.value = false
  }
}

// ─── Eliminar ─────────────────────────────────────────────────────────────────
async function confirmarEliminar() {
  if (!eventoAEliminar.value) return
  eliminando.value = true
  try {
    await eventsService.eliminar(eventoAEliminar.value.id)
    eventos.value = eventos.value.filter((e) => e.id !== eventoAEliminar.value.id)
    toast.success(`"${eventoAEliminar.value.title}" eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    eventoAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Eventos</h1>
        <p class="admin-page-subtitle">Gestión de eventos de la comunidad</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Evento
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los eventos
          <span class="admin-table-count">({{ eventos.length }} total)</span>
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
              <th>Imagen</th>
              <th>Título</th>
              <th>Descripción</th>
              <th>Fecha</th>
              <th>Inscritos</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="evento in eventos" :key="evento.id">
              <td class="admin-table-id">{{ evento.id }}</td>
              <td>
                <img
                  v-if="evento.img"
                  :src="evento.img"
                  alt="Imagen evento"
                  class="admin-event-thumb"
                />
                <span v-else class="admin-event-no-img">—</span>
              </td>
              <td>
                <span class="admin-table-name">{{ evento.title }}</span>
              </td>
              <td class="admin-table-meta" style="max-width:20rem;">
                {{ truncar(evento.description, 70) }}
              </td>
              <td class="admin-event-date-cell">{{ formatFecha(evento.date) }}</td>
              <td>
                <span class="admin-badge">
                  {{ evento.totalParticipants ?? 0 }} inscritos
                </span>
              </td>
              <td>
                <span :class="estadoEvento(evento.date).clase">
                  {{ estadoEvento(evento.date).label }}
                </span>
              </td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(evento)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(evento)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && eventos.length === 0">
              <td colspan="8" class="text-center py-4 admin-table-empty">
                No hay eventos registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Evento
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ eventoEditando ? 'Editar Evento' : 'Nuevo Evento' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Título -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Título <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.title"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Club de lectura mensual"
                />
              </div>
              <!-- Descripción -->
              <div class="col-12">
                <label class="form-label fw-semibold">Descripción</label>
                <textarea
                  v-model="form.description"
                  class="form-control form-control-sm"
                  rows="3"
                  placeholder="Describe el evento..."
                ></textarea>
              </div>
              <!-- Fecha y hora -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Fecha y hora <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.date"
                  type="datetime-local"
                  class="form-control form-control-sm"
                />
              </div>
              <!-- Imagen -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Imagen</label>
                <input
                  id="eImgFile"
                  type="file"
                  accept="image/*"
                  class="form-control form-control-sm"
                  @change="imgFileSeleccionado = $event.target.files[0] || null"
                />
                <div v-if="eventoEditando && form.img" class="mt-1 admin-img-preview">
                  Actual: {{ form.img }}
                </div>
                <input
                  v-model="form.img"
                  type="text"
                  class="form-control form-control-sm mt-1"
                  placeholder="O pega una URL de imagen"
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
              @click="guardar"
            >
              <span v-if="guardando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              {{ eventoEditando ? 'Guardar Cambios' : 'Crear Evento' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Evento
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Evento</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar el evento
              <strong>"{{ eventoAEliminar?.title }}"</strong>?
              Esta acción no se puede deshacer.
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-sm btn-danger"
              :disabled="eliminando"
              @click="confirmarEliminar"
            >
              <span v-if="eliminando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
