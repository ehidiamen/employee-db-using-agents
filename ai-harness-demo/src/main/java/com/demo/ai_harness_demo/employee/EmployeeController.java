package com.demo.ai_harness_demo.employee;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = {"http://localhost:3000"})
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	/**
	 * Creates an employee from a validated request body.
	 *
	 * @param request create payload; bean-validation constraints are enforced
	 * @return HTTP 201 with the created employee
	 * @throws com.demo.ai_harness_demo.employee.DuplicateEmployeeEmailException if the email is already used
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
		EmployeeResponse created = employeeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	/**
	 * Returns a single employee by identifier.
	 *
	 * @param id employee identifier
	 * @return HTTP 200 with the employee DTO
	 * @throws EmployeeNotFoundException if the employee does not exist
	 */
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.findById(id));
	}

	/**
	 * Returns all employees.
	 *
	 * @return HTTP 200 with the employee list
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<EmployeeResponse>> findAll() {
		System.out.println("findAll");
		return ResponseEntity.ok(employeeService.findAll());
	}

	 /**
     * Get all employees or search by term.
     *
     * @param search optional search term to filter employees
     * @return list of employees
     */
	 @GetMapping("/search")
	 public ResponseEntity<List<EmployeeResponse>> getEmployees(
			 @RequestParam(required = false) String search) {
		 
		 
		 List<EmployeeResponse> employees;
		 if (search != null && !search.trim().isEmpty()) {
			 employees = employeeService.searchEmployees(search.trim());
		 } else {
			 employees = employeeService.findAll();
		 }
		 return ResponseEntity.ok(employees);
		
	}
}
