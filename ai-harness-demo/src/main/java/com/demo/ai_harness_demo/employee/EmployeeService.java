package com.demo.ai_harness_demo.employee;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import com.demo.ai_harness_demo.utils.EmployeeUtilities;

@Service
public class EmployeeService {

	private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	/**
	 * Persists a new employee after checking that the email is unique.
	 *
	 * @param request validated create payload
	 * @return the persisted employee as an API DTO
	 * @throws DuplicateEmployeeEmailException if the email is already registered
	 */
	@Transactional
	public EmployeeResponse create(CreateEmployeeRequest request) {
		if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new DuplicateEmployeeEmailException(request.getEmail());
		}

		Employee employee = Employee.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.department(request.getDepartment())
				.jobTitle(request.getJobTitle())
				.build();

		Employee saved = employeeRepository.save(employee);
		log.info("Created employee with id {}", saved.getId());
		return toResponse(saved);
	}

	/**
	 * Loads a single employee by identifier.
	 *
	 * @param id employee identifier
	 * @return the matching employee DTO
	 * @throws EmployeeNotFoundException if no employee exists for {@code id}
	 */
	@Transactional(readOnly = true)
	public EmployeeResponse findById(Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException(id));
		return toResponse(employee);
	}

	/**
	 * Returns every stored employee.
	 *
	 * @return list of employee DTOs, possibly empty
	 */
	@Transactional(readOnly = true)
	public List<EmployeeResponse> findAll() {
		return employeeRepository.findAll().stream().map(this::toResponse).toList();
	}

	    /**
     * Search employees by search term.
     *
     * @param searchTerm the term to search for
     * @return list of matching employees
     */
	List<EmployeeResponse> searchEmployees(String searchTerm){
		if (!StringUtils.hasText(searchTerm)) {
            log.debug("Search term empty, returning all employees");
            return findAll();
        }

        String trimmedSearchTerm = searchTerm.trim();
        log.debug("Searching employees with term: '{}'", trimmedSearchTerm);
        
        List<Employee> results = employeeRepository.searchEmployees(trimmedSearchTerm);
        log.debug("Found {} matching employees", results.size());
        
        return results.stream().map(this::toResponse).toList();
	}

	private EmployeeResponse toResponse(Employee employee) {
		return EmployeeUtilities.toResponse(employee);
	}
}
