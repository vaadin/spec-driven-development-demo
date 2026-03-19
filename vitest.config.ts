import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import { resolve } from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      'Frontend': resolve(__dirname, 'src/main/frontend'),
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    root: '.',
    include: ['src/test/frontend/**/*.test.{ts,tsx}'],
    setupFiles: ['src/test/frontend/setup.ts'],
  },
});
