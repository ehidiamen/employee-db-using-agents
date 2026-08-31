import { renderHook, waitFor } from '@testing-library/react';
import { useFetch } from './useFetch';

function parseStringList(payload: unknown): string[] {
  if (!Array.isArray(payload) || payload.some((item) => typeof item !== 'string')) {
    throw new Error('Invalid list');
  }

  return payload;
}

describe('useFetch', () => {
  const fetchMock = jest.fn();

  beforeEach(() => {
    global.fetch = fetchMock as typeof fetch;
  });

  afterEach(() => {
    fetchMock.mockReset();
  });

  it('returns idle when the url is null', () => {
    const { result } = renderHook(() => useFetch(null, parseStringList));

    expect(result.current).toEqual({
      status: 'idle',
      data: null,
      loading: false,
      error: null,
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('loads data from the given url', async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ['Ada'],
    });

    const { result } = renderHook(() =>
      useFetch('/api/employees', parseStringList)
    );

    expect(result.current.loading).toBe(true);

    await waitFor(() => {
      expect(result.current.status).toBe('success');
    });

    expect(result.current).toEqual({
      status: 'success',
      data: ['Ada'],
      loading: false,
      error: null,
    });
  });

  it('exposes an error when the request fails', async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      status: 500,
    });

    const { result } = renderHook(() =>
      useFetch('/api/employees', parseStringList)
    );

    await waitFor(() => {
      expect(result.current.status).toBe('error');
    });

    expect(result.current.loading).toBe(false);
    expect(result.current.error?.message).toBe(
      'Request failed with status 500'
    );
  });
});
