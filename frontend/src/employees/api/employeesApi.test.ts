import { buildEmployeesUrl, parseEmployeeList } from './employeesApi';
import { createEmployee } from '../../test-utils/employeeFactory';

describe('employeesApi', () => {
  describe('buildEmployeesUrl', () => {
    it('returns the collection endpoint when search is empty', () => {
      expect(buildEmployeesUrl('')).toBe('/api/employees');
      expect(buildEmployeesUrl('   ')).toBe('/api/employees');
    });
  
    it('adds an encoded search query parameter', () => {
      expect(buildEmployeesUrl('Ada Lovelace')).toBe(
        '/api/employees?search=Ada+Lovelace'
      );
    });
  
    it('prefixes the EmployeeController origin from REACT_APP_API_BASE_URL', () => {
      // Save original value
      const original = process.env.REACT_APP_API_BASE_URL;
      
      // Use Object.assign to bypass TypeScript read-only restriction
      Object.assign(process.env, { 
        REACT_APP_API_BASE_URL: 'http://localhost:8080' 
      });
  
      expect(buildEmployeesUrl('Ada')).toBe(
        'http://localhost:8080/api/employees?search=Ada'
      );
  
      // Restore original value
      Object.assign(process.env, { 
        REACT_APP_API_BASE_URL: original 
      });
    });
  });

  describe('parseEmployeeList', () => {
    it('returns a typed employee list', () => {
      const employees = [createEmployee()];
      expect(parseEmployeeList(employees)).toEqual(employees);
    });

    it('throws when the payload is not a valid employee list', () => {
      expect(() => parseEmployeeList([{ id: 'bad' }])).toThrow(
        'Invalid employee list response'
      );
    });
  });
});
