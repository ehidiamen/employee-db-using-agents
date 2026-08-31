package com.demo.ai_harness_demo.employee;

public class EmployeeNotFoundException extends RuntimeException {

	public EmployeeNotFoundException(Long employeeId) {
		super("Employee not found: " + employeeId);
	}
}
