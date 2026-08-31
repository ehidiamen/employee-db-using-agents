package com.demo.ai_harness_demo.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import com.demo.ai_harness_demo.exception.GlobalExceptionHandler;
import com.demo.ai_harness_demo.security.JwtService;
import com.demo.ai_harness_demo.security.SecurityConfig;

import java.util.Arrays;
import java.util.List;

import tools.jackson.databind.json.JsonMapper;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(controllers = EmployeeController.class)
@Import({ GlobalExceptionHandler.class, SecurityConfig.class })
class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JsonMapper jsonMapper;

	@MockitoBean
	private EmployeeService employeeService;

	@MockitoBean
	private JwtService jwtService;

	private List<Employee> sampleEmployees;
    private List<EmployeeResponse> sampleResponses;

    @BeforeEach
    void setUp() {
        Employee emp1 = Employee.builder()
                .id(1L)
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie.brown@company.com")
                .jobTitle("QA Engineer")
                .department("Quality")
                .build();

        Employee emp2 = Employee.builder()
                .id(2L)
                .firstName("Charlie")
                .lastName("Chaplin")
                .email("charlie.chaplin@company.com")
                .jobTitle("Senior Developer")
                .department("Engineering")
                .build();

        sampleEmployees = Arrays.asList(emp1, emp2);

        sampleResponses = sampleEmployees.stream()
			.map(emp -> EmployeeResponse.builder()
				.id(emp.getId())
				.firstName(emp.getFirstName())
				.lastName(emp.getLastName())
				.email(emp.getEmail())
				.jobTitle(emp.getJobTitle())
				.department(emp.getDepartment())
				.build())
			.toList();
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createEmployee_returnsCreated() throws Exception {
		CreateEmployeeRequest request = validRequest();
		EmployeeResponse response = EmployeeResponse.builder()
				.id(1L)
				.firstName("Ada")
				.lastName("Lovelace")
				.email("ada@example.com")
				.department("Engineering")
				.jobTitle("Engineer")
				.build();
		when(employeeService.create(any(CreateEmployeeRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.email").value("ada@example.com"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void createEmployee_returnsValidationErrors() throws Exception {
		String invalidJson = """
				{
				  "firstName": "",
				  "lastName": "",
				  "email": "not-an-email",
				  "department": ""
				}
				""";

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Validation Failed"))
				.andExpect(jsonPath("$.message").value("Request validation failed"))
				.andExpect(jsonPath("$.path").value("/api/employees"))
				.andExpect(jsonPath("$.fieldErrors.firstName").value("First name is required"))
				.andExpect(jsonPath("$.fieldErrors.lastName").value("Last name is required"))
				.andExpect(jsonPath("$.fieldErrors.email").value("Email must be a well-formed address"))
				.andExpect(jsonPath("$.fieldErrors.department").value("Department is required"));

		verify(employeeService, never()).create(any());
	}

	@Test
	@WithMockUser(roles = "USER")
	void createEmployee_forbiddenForNonAdmin() throws Exception {
		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(validRequest())))
				.andExpect(status().isForbidden());

		verify(employeeService, never()).create(any());
	}

	@Test
	void createEmployee_unauthorizedWhenAnonymous() throws Exception {
		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(validRequest())))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser
	void getEmployee_returnsOk() throws Exception {
		EmployeeResponse response = EmployeeResponse.builder()
				.id(7L)
				.firstName("Grace")
				.lastName("Hopper")
				.email("grace@example.com")
				.department("R&D")
				.build();
		when(employeeService.findById(7L)).thenReturn(response);

		mockMvc.perform(get("/api/employees/7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.firstName").value("Grace"));
	}

	@Test
	@WithMockUser
	void getEmployee_returnsNotFound() throws Exception {
		when(employeeService.findById(99L)).thenThrow(new EmployeeNotFoundException(99L));

		mockMvc.perform(get("/api/employees/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Employee not found: 99"));
	}

	private CreateEmployeeRequest validRequest() {
		return CreateEmployeeRequest.builder()
				.firstName("Ada")
				.lastName("Lovelace")
				.email("ada@example.com")
				.department("Engineering")
				.jobTitle("Engineer")
				.build();
	}

	@Test
    void getEmployees_WhenNoSearchParam_ReturnsAllEmployees() throws Exception {
        when(employeeService.searchEmployees("")).thenReturn(sampleResponses);
		when(employeeService.findAll()).thenReturn(sampleResponses);
       
        mockMvc.perform(get("/api/employees/search"))
                .andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Charlie"))
                .andExpect(jsonPath("$[0].lastName").value("Brown"))
                .andExpect(jsonPath("$[1].firstName").value("Charlie"))
                .andExpect(jsonPath("$[1].lastName").value("Chaplin"));
    }

	@Test
    void getEmployees_WithSearchParam_ReturnsMatchingEmployees() throws Exception {
        String searchTerm = "Charlie";
        when(employeeService.searchEmployees(searchTerm)).thenReturn(sampleResponses);
		
        mockMvc.perform(get("/api/employees/search")
				.param("search", searchTerm))
                .andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Charlie"))
                .andExpect(jsonPath("$[0].lastName").value("Brown"));
                
    }

    @Test
    void getEmployees_WithSearchParam_ReturnsEmptyListWhenNoMatches() throws Exception {
        String searchTerm = "Nonexistent";
        when(employeeService.searchEmployees(searchTerm)).thenReturn(List.of());
        
        mockMvc.perform(get("/api/employees/search")
                .param("search", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
