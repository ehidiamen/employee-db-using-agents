import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

beforeEach(() => {
  global.fetch = jest.fn().mockResolvedValue({
    ok: true,
    json: async () => [],
  }) as typeof fetch;
});

test('renders employee search', async () => {
  render(<App />);
  expect(
    screen.getByRole('heading', { name: 'Employee search' })
  ).toBeInTheDocument();

  await waitFor(() => {
    expect(screen.getByText('No employees found.')).toBeInTheDocument();
  });
});
