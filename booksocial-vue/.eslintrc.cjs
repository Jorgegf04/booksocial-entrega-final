/* eslint-env node */
module.exports = {
  root: true,
  extends: ['plugin:vue/vue3-recommended', '@vue/eslint-config-prettier'],
  parserOptions: {
    ecmaVersion: 'latest',
  },
  rules: {
    // Permite componentes con nombre de una sola palabra (ej: LoginView)
    'vue/multi-word-component-names': 'off',
    // Permite props sin valor por defecto cuando son requeridas
    'vue/require-default-prop': 'off',
  },
}
