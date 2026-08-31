import { useCallback, useMemo, useState } from 'react';
import { useDebounce } from '../../hooks/useDebounce';
import { useFetch } from '../../hooks/useFetch';
import { buildEmployeesUrl, parseEmployeeList } from '../api/employeesApi';
import type { Employee } from '../types/employee';

export const DEFAULT_SEARCH_DEBOUNCE_MS = 300;

export interface UseEmployeeSearchResult {
  query: string;
  setQuery: (value: string) => void;
  employees: Employee[] | null;
  loading: boolean;
  error: Error | null;
}

export function useEmployeeSearch(
  debounceMs: number = DEFAULT_SEARCH_DEBOUNCE_MS
): UseEmployeeSearchResult {
  const [query, setQueryState] = useState('');
  const debouncedQuery = useDebounce(query, debounceMs);
  const url = useMemo(
    () => buildEmployeesUrl(debouncedQuery),
    [debouncedQuery]
  );
  const { data, loading, error } = useFetch(url, parseEmployeeList);

  const setQuery = useCallback((value: string) => {
    setQueryState(value);
  }, []);

  return {
    query,
    setQuery,
    employees: data,
    loading,
    error,
  };
}
