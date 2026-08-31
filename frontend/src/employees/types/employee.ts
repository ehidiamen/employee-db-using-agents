/**
 * Mirrors the Java Employee entity / Employee DTO returned by
 * EmployeeController (GET /api/employees).
 *
 * Java source of truth (typical mapping):
 * - Long id
 * - String firstName, lastName, email, department, jobTitle
 * - LocalDate hireDate  -> ISO-8601 date string
 * - BigDecimal salary    -> JSON number
 * - EmploymentStatus status (enum)
 */
export type EmploymentStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE';

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
}

const EMPLOYMENT_STATUSES: readonly EmploymentStatus[] = [
  'ACTIVE',
  'INACTIVE',
  'ON_LEAVE',
];

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

export function isEmploymentStatus(value: unknown): value is EmploymentStatus {
  return (
    typeof value === 'string' &&
    (EMPLOYMENT_STATUSES as readonly string[]).includes(value)
  );
}

export function isEmployee(value: unknown): value is Employee {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'number' &&
    typeof value.firstName === 'string' &&
    typeof value.lastName === 'string' &&
    typeof value.email === 'string' &&
    typeof value.department === 'string' &&
    typeof value.jobTitle === 'string'
  );
}

export function isEmployeeList(value: unknown): value is Employee[] {
  return Array.isArray(value) && value.every(isEmployee);
}
