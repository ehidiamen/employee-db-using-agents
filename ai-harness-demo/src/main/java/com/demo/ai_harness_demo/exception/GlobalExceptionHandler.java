package com.demo.ai_harness_demo.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.demo.ai_harness_demo.employee.DuplicateEmployeeEmailException;
import com.demo.ai_harness_demo.employee.EmployeeNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Maps bean-validation failures from {@code @Valid} request bodies to a JSON error payload.
	 *
	 * @param exception the validation exception thrown by Spring MVC
	 * @param request the current HTTP request
	 * @return HTTP 400 with field-level error details
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Validation Failed")
				.message("Request validation failed")
				.path(request.getRequestURI())
				.fieldErrors(fieldErrors)
				.build();
		return ResponseEntity.badRequest().body(body);
	}

	/**
	 * Maps missing-employee lookups to HTTP 404.
	 *
	 * @param exception the domain exception
	 * @param request the current HTTP request
	 * @return HTTP 404 JSON error payload
	 */
	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(EmployeeNotFoundException exception,
			HttpServletRequest request) {
		log.warn("Employee lookup failed: {}", exception.getMessage());
		return build(HttpStatus.NOT_FOUND, exception.getMessage(), request);
	}

	/**
	 * Maps duplicate-email conflicts to HTTP 409.
	 *
	 * @param exception the domain exception
	 * @param request the current HTTP request
	 * @return HTTP 409 JSON error payload
	 */
	@ExceptionHandler(DuplicateEmployeeEmailException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateEmployeeEmailException exception,
			HttpServletRequest request) {
		log.warn("Duplicate employee email rejected");
		return build(HttpStatus.CONFLICT, exception.getMessage(), request);
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message)
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(status).body(body);
	}
}
