package com.demo.ai_harness_demo.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String department;
	private String jobTitle;
}
