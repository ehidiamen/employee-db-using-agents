import type { Employee } from '../employees/types/employee';

export function createEmployee(
  overrides: Partial<Employee> = {}
): Employee {
  return {
    id: 1,
    firstName: 'Ada',
    lastName: 'Lovelace',
    email: 'ada.lovelace@example.com',
    department: 'Engineering',
    jobTitle: 'Software Engineer',
    ...overrides,
  };
}
