import { isEmployeeList, type Employee } from '../types/employee';

export const EMPLOYEES_ENDPOINT = '/api/employees/search';

export function getApiBaseUrl(): string {
  return (process.env.REACT_APP_API_BASE_URL ?? '').replace(/\/$/, '');
}

export function buildEmployeesUrl(search: string): string {
  const trimmed = search.trim();
  const path = trimmed
    ? `${EMPLOYEES_ENDPOINT}?${new URLSearchParams({ search: trimmed }).toString()}`
    : EMPLOYEES_ENDPOINT;
  console.log(`${getApiBaseUrl()}${path}`);
  return `${getApiBaseUrl()}${path}`;
}

export function parseEmployeeList(payload: unknown): Employee[] {
  if (!isEmployeeList(payload)) {
    throw new Error('Invalid employee list response');
  }

  return payload;
}
