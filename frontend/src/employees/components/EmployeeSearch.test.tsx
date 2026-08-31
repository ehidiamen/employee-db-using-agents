import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { EmployeeSearch } from './EmployeeSearch';
import { createEmployee } from '../../test-utils/employeeFactory';

describe('EmployeeSearch', () => {
  const fetchMock = jest.fn();

  beforeEach(() => {
    global.fetch = fetchMock as typeof fetch;
  });

  afterEach(() => {
    fetchMock.mockReset();
  });

  it('renders a labelled search field', async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => [],
    });

    render(<EmployeeSearch debounceMs={0} />);

    expect(
      screen.getByRole('searchbox', { name: 'Search employees' })
    ).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('No employees found.')).toBeInTheDocument();
    });
  });

  it('shows a loading state while employees are requested', async () => {
    let resolveRequest: ((value: unknown) => void) | undefined;
    fetchMock.mockReturnValue(
      new Promise((resolve) => {
        resolveRequest = resolve;
      })
    );

    render(<EmployeeSearch debounceMs={0} />);

    expect(screen.getByRole('status')).toHaveTextContent('Loading employees');

    resolveRequest?.({
      ok: true,
      json: async () => [],
    });

    await waitFor(() => {
      expect(screen.queryByRole('status')).not.toBeInTheDocument();
    });
  });

  it('renders employees returned from /api/employees', async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => [
        createEmployee(),
        createEmployee({
          id: 2,
          firstName: 'Grace',
          lastName: 'Hopper',
          email: 'grace.hopper@example.com',
          jobTitle: 'Rear Admiral',
        }),
      ],
    });

    render(<EmployeeSearch debounceMs={0} />);

    expect(await screen.findByText('Ada Lovelace')).toBeInTheDocument();
    expect(screen.getByText('Grace Hopper')).toBeInTheDocument();
    expect(screen.getByText(/Software Engineer/)).toBeInTheDocument();
  });

  it('shows an error state when the request fails', async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      status: 503,
    });

    render(<EmployeeSearch debounceMs={0} />);

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Unable to load employees. Request failed with status 503'
    );
  });

  it('requests the search query from EmployeeController', async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => [createEmployee()],
    });

    render(<EmployeeSearch debounceMs={0} />);

    await screen.findByText('Ada Lovelace');

    await userEvent.type(
      screen.getByRole('searchbox', { name: 'Search employees' }),
      'Hopper'
    );

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith(
        '/api/employees?search=Hopper',
        expect.objectContaining({ signal: expect.any(AbortSignal) })
      );
    });
  });
});
