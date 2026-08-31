package com.demo.ai_harness_demo.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 100, message = "First name must be at most 100 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100, message = "Last name must be at most 100 characters")
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be a well-formed address")
	@Size(max = 255, message = "Email must be at most 255 characters")
	private String email;

	@NotBlank(message = "Department is required")
	@Size(max = 100, message = "Department must be at most 100 characters")
	private String department;

	@Size(max = 150, message = "Job title must be at most 150 characters")
	private String jobTitle;
}
