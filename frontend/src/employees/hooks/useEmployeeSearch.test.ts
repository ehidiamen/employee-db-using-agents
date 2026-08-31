import { renderHook, act, waitFor } from '@testing-library/react';
import { useEmployeeSearch } from './useEmployeeSearch';
import { createEmployee } from '../../test-utils/employeeFactory';

describe('useEmployeeSearch', () => {
  const fetchMock = jest.fn();

  beforeEach(() => {
    jest.useFakeTimers();
    global.fetch = fetchMock as typeof fetch;
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => [createEmployee()],
    });
  });

  afterEach(() => {
    fetchMock.mockReset();
    jest.useRealTimers();
  });

  async function flushFetch() {
    await act(async () => {
      jest.runOnlyPendingTimers();
      await Promise.resolve();
    });
  }

  it('returns data, loading, and error from useFetch', async () => {
    const { result } = renderHook(() => useEmployeeSearch(0));

    await flushFetch();

    expect(result.current.loading).toBe(false);
    expect(result.current.employees).toEqual([createEmployee()]);
    expect(result.current.error).toBeNull();
  });

  it('debounces search terms before requesting /api/employees', async () => {
    const { result } = renderHook(() => useEmployeeSearch(300));

    await flushFetch();

    act(() => {
      result.current.setQuery('Ad');
    });
    act(() => {
      result.current.setQuery('Ada');
    });

    expect(fetchMock).toHaveBeenCalledWith(
      '/api/employees',
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );

    await act(async () => {
      jest.advanceTimersByTime(300);
      await Promise.resolve();
    });

    expect(fetchMock).toHaveBeenCalledWith(
      '/api/employees?search=Ada',
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
  });
});
