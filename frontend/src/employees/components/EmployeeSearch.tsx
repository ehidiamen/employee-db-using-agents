import { ChangeEvent, memo, useCallback } from 'react';
import {
  DEFAULT_SEARCH_DEBOUNCE_MS,
  useEmployeeSearch,
} from '../hooks/useEmployeeSearch';
import type { Employee } from '../types/employee';
import styles from './EmployeeSearch.module.css';

export interface EmployeeSearchProps {
  debounceMs?: number;
}

function formatEmployeeName(employee: Employee): string {
  return `${employee.firstName} ${employee.lastName}`;
}

function EmployeeResultItem({ employee }: { employee: Employee }) {
  return (
    <li>
      <article className={styles.card} aria-label={formatEmployeeName(employee)}>
        <h2 className={styles.name}>{formatEmployeeName(employee)}</h2>
        <p className={styles.meta}>{employee.jobTitle} · {employee.department}</p>
        <p className={styles.meta}>{employee.email}</p>
      </article>
    </li>
  );
}

const MemoizedEmployeeResultItem = memo(EmployeeResultItem);

export function EmployeeSearch({
  debounceMs = DEFAULT_SEARCH_DEBOUNCE_MS,
}: EmployeeSearchProps) {
  const { query, setQuery, employees, loading, error } =
    useEmployeeSearch(debounceMs);

  const handleChange = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      setQuery(event.target.value);
    },
    [setQuery]
  );

  return (
    <section className={styles.page} aria-labelledby="employee-search-heading">
      <h1 id="employee-search-heading" className={styles.title}>
        Employee search
      </h1>
      <p className={styles.subtitle}>
        Search the employee directory. Results update after you pause typing.
      </p>

      <div className={styles.field}>
        <label className={styles.label} htmlFor="employee-search-input">
          Search employees
        </label>
        <input
          id="employee-search-input"
          className={styles.input}
          type="search"
          name="employeeSearch"
          value={query}
          onChange={handleChange}
          placeholder="Name, department, or job title"
          autoComplete="off"
          aria-describedby="employee-search-help"
          aria-busy={loading}
        />
        <span id="employee-search-help" className={styles.subtitle}>
          Matches against the employee directory at /api/employees.
        </span>
      </div>

      {loading ? (
        <p className={styles.status} role="status" aria-live="polite">
          Loading employees…
        </p>
      ) : null}

      {error ? (
        <p className={styles.error} role="alert">
          Unable to load employees. {error.message}
        </p>
      ) : null}

      {!loading && !error && employees && employees.length === 0 ? (
        <p className={styles.empty}>No employees found.</p>
      ) : null}

      {!loading && !error && employees && employees.length > 0 ? (
        <ul className={styles.list} aria-label="Employee search results">
          {employees.map((employee) => (
            <MemoizedEmployeeResultItem key={employee.id} employee={employee} />
          ))}
        </ul>
      ) : null}
    </section>
  );
}

export default memo(EmployeeSearch);
