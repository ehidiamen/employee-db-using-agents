package com.demo.ai_harness_demo.config;


import com.demo.ai_harness_demo.employee.Employee;
import com.demo.ai_harness_demo.employee.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    public DataLoader(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        // Only load if database is empty
        if (employeeRepository.count() == 0) {
            List<Employee> employees = Arrays.asList(
                createEmployee("John", "Doe", "john.doe@company.com", 
                    "Senior Developer", "Engineering"),
                createEmployee("Jane", "Smith", "jane.smith@company.com",
                    "Product Manager", "Product"),
                createEmployee("Bob", "Johnson", "bob.johnson@company.com",
                    "UX Designer", "Design"),
                createEmployee("Alice", "Williams", "alice.williams@company.com",
                    "DevOps Engineer", "Infrastructure"),
                createEmployee("Charlie", "Brown", "charlie.brown@company.com",
                    "QA Engineer", "Quality")
            );

            employeeRepository.saveAll(employees);
            System.out.println("✅ Loaded " + employees.size() + " sample employees");
        }
    }

    private Employee createEmployee(String firstName, String lastName, String email,
                                    String position, String department) {
        return Employee.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .jobTitle(position)
            .department(department)
            .build();
    }
}
