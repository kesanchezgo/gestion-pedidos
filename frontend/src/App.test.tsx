import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import App from './App'

describe('App', () => {
  it('renderiza el título y el contador', () => {
    render(<App />)
    expect(screen.getByText('Get started')).toBeTruthy()
    expect(screen.getByRole('button', { name: /count is 0/i })).toBeTruthy()
  })
})
