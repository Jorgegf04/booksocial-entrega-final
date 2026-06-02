import { useAuthStore } from '@/stores/auth' // con esto recuperamos por ejemplo el rol que tiene el usuario

// Codigo de la ilustracion 51
/**
 * Este archivo sirve para centralizar los permiso del frontend.
 Es decir: en vez de comprobar roles directamente en cada componente Vue, se usa este composable para preguntar cosas como “¿este usuario puede comentar?”, “¿puede entrar al panel admin?”, “¿puede apuntarse a eventos?”, etc.
 Un composable es una función reutilizable en diferentes apartados de la aplicacion
 */
export function usePermissions() {
  const auth = useAuthStore() // Con esto se sabe que usuario y que ROL tiene el usuario
  const role = () => auth.user?.role ?? null // devuelve el rol del usuario actual
  const isAuthenticated = () => auth.isAuthenticated // devuelve true si el usuario ha iniaido sesión
  const isSubscribed = () => ['SUBSCRIBED', 'ADMIN'].includes(role()) // devuelve true si el usuario esta suscrito
  const isAdmin = () => role() === 'ADMIN' // solo si el usuario es administrador
  const canComment = () => isAuthenticated() // permite comentar si el usuario esta logueado
  const canFollowWorks = () => isAuthenticated() // permite seguir obras si el usuario esta autenticado
  const canFollowUsers = () => isAuthenticated() // permite seguir usuarios si el usuario esta autentificad
  const canJoinEvents = () => isSubscribed() // permite unirse a eventos si esta suscrito
  const canAccessProfile = () => isAuthenticated() // permite acceder al la vista de perfil de usuario si estas autentificado
  const canManageSubscription = () => isSubscribed() // permite manegar la suscripción del usuario
  const canAccessAdmin = () => isAdmin() // permite acceder al panel de administración
  const canPurchase = () => true // permite comprar aunque el usuario sea invitado

  return {
    isAuthenticated,
    isSubscribed,
    isAdmin,
    canComment,
    canFollowWorks,
    canFollowUsers,
    canJoinEvents,
    canAccessProfile,
    canManageSubscription,
    canAccessAdmin,
    canPurchase,
  }
}
