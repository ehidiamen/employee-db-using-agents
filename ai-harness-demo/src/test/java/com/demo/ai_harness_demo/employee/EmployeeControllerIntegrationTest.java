package com.demo.ai_harness_demo.employee;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EmployeeControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	@WithMockUser(roles = "ADMIN")
	void createThenGetEmployee_persistsInH2() throws Exception {
		CreateEmployeeRequest request = CreateEmployeeRequest.builder()
				.firstName("Katherine")
				.lastName("Johnson")
				.email("katherine@example.com")
				.department("Mathematics")
				.jobTitle("Mathematician")
				.build();

		String body = mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.email").value("katherine@example.com"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		EmployeeResponse created = jsonMapper.readValue(body, EmployeeResponse.class);

		mockMvc.perform(get("/api/employees/" + created.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("Katherine"))
				.andExpect(jsonPath("$.department").value("Mathematics"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createEmployee_validationErrorDoesNotPersist() throws Exception {
		long before = employeeRepository.count();
		String invalidJson = """
				{
				  "firstName": "",
				  "lastName": "Hopper",
				  "email": "invalid",
				  "department": "Navy"
				}
				""";

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Failed"))
				.andExpect(jsonPath("$.fieldErrors.firstName").exists())
				.andExpect(jsonPath("$.fieldErrors.email").exists());

		org.assertj.core.api.Assertions.assertThat(employeeRepository.count()).isEqualTo(before);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createEmployee_duplicateEmailReturnsConflict() throws Exception {
		employeeRepository.save(Employee.builder()
				.firstName("Existing")
				.lastName("Person")
				.email("dup@example.com")
				.department("HR")
				.build());

		CreateEmployeeRequest request = CreateEmployeeRequest.builder()
				.firstName("New")
				.lastName("Hire")
				.email("dup@example.com")
				.department("HR")
				.build();

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.message").value("An employee with this email already exists"));
	}

	@Test
	@WithMockUser
	void getEmployee_missingIdReturnsNotFound() throws Exception {
		mockMvc.perform(get("/api/employees/9999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	@WithMockUser
	void findAll_returnsPersistedEmployees() throws Exception {
		employeeRepository.save(Employee.builder()
				.firstName("Alan")
				.lastName("Turing")
				.email("alan@example.com")
				.department("Research")
				.build());

		mockMvc.perform(get("/api/employees"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].email").value("alan@example.com"));
	}

	@Test
	void unauthenticatedRequestsAreRejected() throws Exception {
		mockMvc.perform(get("/api/employees"))
				.andExpect(status().isUnauthorized());
	}
}
