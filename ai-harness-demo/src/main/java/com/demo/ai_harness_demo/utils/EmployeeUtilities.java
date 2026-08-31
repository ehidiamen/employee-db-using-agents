package com.demo.ai_harness_demo.utils;

import com.demo.ai_harness_demo.employee.Employee;
import com.demo.ai_harness_demo.employee.EmployeeResponse;

public final class EmployeeUtilities {

    public static EmployeeResponse toResponse(Employee employee) {
		return EmployeeResponse.builder()
				.id(employee.getId())
				.firstName(employee.getFirstName())
				.lastName(employee.getLastName())
				.email(employee.getEmail())
				.department(employee.getDepartment())
				.jobTitle(employee.getJobTitle())
				.build();
	}
}
