package com.demo.ai_harness_demo.employee;

public class DuplicateEmployeeEmailException extends RuntimeException {

	public DuplicateEmployeeEmailException(String email) {
		super("An employee with this email already exists");
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("email is required");
		}
	}
}
