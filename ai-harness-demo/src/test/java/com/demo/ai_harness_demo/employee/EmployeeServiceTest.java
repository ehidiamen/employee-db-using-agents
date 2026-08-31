package com.demo.ai_harness_demo.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demo.ai_harness_demo.utils.EmployeeUtilities;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private EmployeeService employeeService;

	private CreateEmployeeRequest request;

	private List<Employee> sampleEmployees;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
		request = CreateEmployeeRequest.builder()
				.firstName("Ada")
				.lastName("Lovelace")
				.email("ada@example.com")
				.department("Engineering")
				.jobTitle("Engineer")
				.build();

        employee1 = Employee.builder()
                .id(1L)
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie.brown@company.com")
                .jobTitle("QA Engineer")
                .department("Quality")
                .build();

        employee2 = Employee.builder()
                .id(2L)
                .firstName("Charlie")
                .lastName("Chaplin")
                .email("charlie.chaplin@company.com")
                .jobTitle("Senior Developer")
                .department("Engineering")
                .build();

        sampleEmployees = List.of(employee1, employee2);
    }

	@Test
	void create_savesEmployeeAndReturnsDto() {
		when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
		when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
			Employee employee = invocation.getArgument(0);
			employee.setId(42L);
			return employee;
		});

		EmployeeResponse response = employeeService.create(request);

		assertThat(response.getId()).isEqualTo(42L);
		assertThat(response.getFirstName()).isEqualTo("Ada");
		assertThat(response.getEmail()).isEqualTo("ada@example.com");

		ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
		verify(employeeRepository).save(captor.capture());
		assertThat(captor.getValue().getLastName()).isEqualTo("Lovelace");
		assertThat(captor.getValue().getDepartment()).isEqualTo("Engineering");
	}

	@Test
	void create_throwsWhenEmailExists() {
		when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(true);

		assertThatThrownBy(() -> employeeService.create(request))
				.isInstanceOf(DuplicateEmployeeEmailException.class)
				.hasMessage("An employee with this email already exists");

		verify(employeeRepository, never()).save(any());
	}

	@Test
	void findById_returnsDtoWhenPresent() {
		Employee employee = Employee.builder()
				.id(5L)
				.firstName("Grace")
				.lastName("Hopper")
				.email("grace@example.com")
				.department("R&D")
				.jobTitle("Rear Admiral")
				.build();
		when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));

		EmployeeResponse response = employeeService.findById(5L);

		assertThat(response.getId()).isEqualTo(5L);
		assertThat(response.getFirstName()).isEqualTo("Grace");
		assertThat(response.getJobTitle()).isEqualTo("Rear Admiral");
	}

	@Test
	void findById_throwsWhenMissing() {
		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> employeeService.findById(99L))
				.isInstanceOf(EmployeeNotFoundException.class)
				.hasMessage("Employee not found: 99");
	}

	@Test
	void findAll_mapsEntitiesToDtos() {
		Employee employee = Employee.builder()
				.id(1L)
				.firstName("Alan")
				.lastName("Turing")
				.email("alan@example.com")
				.department("Research")
				.build();
		when(employeeRepository.findAll()).thenReturn(List.of(employee));

		List<EmployeeResponse> results = employeeService.findAll();

		assertThat(results).hasSize(1);
		assertThat(results.get(0).getEmail()).isEqualTo("alan@example.com");
	}

	@Test
    void searchEmployees_WithMatchingFirstName_ReturnsMatchingEmployees() {
        String searchTerm = "Charlie";
        when(employeeRepository.searchEmployees(searchTerm)).thenReturn(sampleEmployees);

        List<EmployeeResponse> result = employeeService.searchEmployees(searchTerm);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(emp -> emp.getFirstName().equals("Charlie"));
    }

    @Test
    void searchEmployees_WithMatchingLastName_ReturnsMatchingEmployees() {
        String searchTerm = "Brown";
        when(employeeRepository.searchEmployees(searchTerm)).thenReturn(List.of(employee1));

        List<EmployeeResponse> result = employeeService.searchEmployees(searchTerm);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Brown");
    }

    @Test
    void searchEmployees_WithMatchingPosition_ReturnsMatchingEmployees() {
        String searchTerm = "Developer";
        when(employeeRepository.searchEmployees(searchTerm)).thenReturn(List.of(employee2));

        List<EmployeeResponse> result = employeeService.searchEmployees(searchTerm);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getJobTitle()).contains("Developer");
    }

    @Test
    void searchEmployees_WithMatchingDepartment_ReturnsMatchingEmployees() {
        String searchTerm = "Quality";
        when(employeeRepository.searchEmployees(searchTerm)).thenReturn(List.of(employee1));

        List<EmployeeResponse> result = employeeService.searchEmployees(searchTerm);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment()).isEqualTo("Quality");
    }

    @Test
    void searchEmployees_WithNoMatches_ReturnsEmptyList() {
        String searchTerm = "Nonexistent";
        when(employeeRepository.searchEmployees(searchTerm)).thenReturn(List.of());

        List<EmployeeResponse> result = employeeService.searchEmployees(searchTerm);

        assertThat(result).isEmpty();
    }

    @Test
    void searchEmployees_WithEmptySearchTerm_ReturnsAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(sampleEmployees);

        List<EmployeeResponse> result = employeeService.searchEmployees("");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(EmployeeUtilities.toResponse(employee1), EmployeeUtilities.toResponse(employee2));
    }
}
