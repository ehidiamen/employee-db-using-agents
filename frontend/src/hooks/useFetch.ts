import { useEffect, useState } from 'react';

export type FetchStatus = 'idle' | 'loading' | 'success' | 'error';

export type UseFetchResult<T> =
  | { status: 'idle'; data: null; loading: false; error: null }
  | { status: 'loading'; data: null; loading: true; error: null }
  | { status: 'success'; data: T; loading: false; error: null }
  | { status: 'error'; data: null; loading: false; error: Error };

const idleState = {
  status: 'idle',
  data: null,
  loading: false,
  error: null,
} as const;

/**
 * Generic data-fetching hook. Pass `null` to skip the request (idle).
 * Cleans up in-flight requests with AbortController.
 */
export function useFetch<T>(
  url: string | null,
  parse: (payload: unknown) => T
): UseFetchResult<T> {
  const [state, setState] = useState<UseFetchResult<T>>(idleState);

  useEffect(() => {
    if (url === null) {
      setState(idleState);
      return;
    }

    const controller = new AbortController();
    setState({
      status: 'loading',
      data: null,
      loading: true,
      error: null,
    });

    fetch(url, { signal: controller.signal })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`Request failed with status ${response.status}`);
        }

        const payload: unknown = await response.json();
        return parse(payload);
      })
      .then((data) => {
        if (controller.signal.aborted) {
          return;
        }

        setState({
          status: 'success',
          data,
          loading: false,
          error: null,
        });
      })
      .catch((caught: unknown) => {
        if (controller.signal.aborted) {
          return;
        }

        const error =
          caught instanceof Error ? caught : new Error('Request failed');
        setState({
          status: 'error',
          data: null,
          loading: false,
          error,
        });
      });

    return () => {
      controller.abort();
    };
  }, [url, parse]);

  return state;
}
