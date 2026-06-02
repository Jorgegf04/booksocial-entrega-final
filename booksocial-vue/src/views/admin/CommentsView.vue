<script setup>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { commentsService } from '@/api/services/commentsService'

const toast = useToast()

const comentarios = ref([])
const cargando = ref(false)
const eliminando = ref(false)

const comentarioAEliminar = ref(null)

const refModalEliminar = ref(null)
let bsModalEliminar

onMounted(async () => {
  bsModalEliminar = new Modal(refModalEliminar.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    comentarios.value = await commentsService.listar()
  } catch {
    toast.error('No se pudieron cargar los comentarios.')
  } finally {
    cargando.value = false
  }
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

function abrirModalEliminar(comentario) {
  comentarioAEliminar.value = comentario
  bsModalEliminar.show()
}

async function confirmarEliminar() {
  if (!comentarioAEliminar.value) return
  eliminando.value = true
  try {
    await commentsService.eliminar(comentarioAEliminar.value.id)
    comentarios.value = comentarios.value.filter((c) => c.id !== comentarioAEliminar.value.id)
    toast.success(`Comentario de "${comentarioAEliminar.value.username}" eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    comentarioAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar (sin botón: los comentarios los crean los usuarios) -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Comentarios</h1>
        <p class="admin-page-subtitle">Moderación de comentarios de la comunidad</p>
      </div>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los comentarios
          <span class="admin-table-count">({{ comentarios.length }} total)</span>
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
              <th>Obra</th>
              <th>Contenido</th>
              <th>Fecha</th>
              <th>Editado</th>
              <th>Respuestas</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="comentario in comentarios" :key="comentario.id">
              <td class="admin-table-id">{{ comentario.id }}</td>
              <td>
                <span class="admin-comment-username">{{ comentario.username ?? '—' }}</span>
              </td>
              <td class="admin-table-meta">{{ comentario.workTitle ?? '—' }}</td>
              <td class="admin-table-meta" style="max-width:28rem;">
                {{ truncar(comentario.content, 120) }}
              </td>
              <td class="admin-comment-date">{{ formatFecha(comentario.date) }}</td>
              <td>
                <span v-if="comentario.edited" class="admin-comment-edited">Editado</span>
                <span v-else class="admin-comment-not-edited">—</span>
              </td>
              <td>
                <span class="admin-badge">
                  {{ comentario.replies?.length ?? 0 }} resp.
                </span>
              </td>
              <td>
                <button
                  class="btn btn-sm btn-outline-danger p-1"
                  title="Eliminar comentario"
                  @click="abrirModalEliminar(comentario)"
                >
                  <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                </button>
              </td>
            </tr>
            <tr v-if="!cargando && comentarios.length === 0">
              <td colspan="8" class="text-center py-4 admin-table-empty">
                No hay comentarios registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Comentario
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Comentario</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar el comentario de
              <strong>"{{ comentarioAEliminar?.username }}"</strong>?
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
