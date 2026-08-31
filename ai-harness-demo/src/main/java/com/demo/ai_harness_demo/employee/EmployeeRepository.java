package com.demo.ai_harness_demo.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByEmailIgnoreCase(String email);

	/**
     * Search for employees by first name, last name, position, or department.
     * Uses case-insensitive partial matching.
     *
     * @param searchTerm the term to search for
     * @return list of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employee> searchEmployees(@Param("searchTerm") String searchTerm);
}
