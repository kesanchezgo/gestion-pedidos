import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // T002 (plan D5): same-origin en dev, evita CORS sin tocar el backend
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
  },
})
