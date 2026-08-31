# employee-db-using-agents
Employee Search Tool using Agent


> A full-stack enterprise application built with AI agents using a "harness" approach.

## 🎯 Project Overview

This project demonstrates how AI agents can build production-ready enterprise software when given the right "harness"—rules, context, verification loops, and guardrails. It's a complete employee management system with:

- **Backend**: Java Spring Boot REST API with JPA, validation, and search
- **Frontend**: React with TypeScript, debounced search, and real-time filtering
- **AI Harness**: `.cursor/rules/` files that encode enterprise coding standards

### The Problem It Solves

Developers using AI coding assistants often get low-quality, inconsistent code that doesn't follow team standards, lacks tests, or introduces security issues. This project shows how a well-designed harness (rules + verification + context) enables AI agents to produce enterprise-grade code reliably.


## 🚀 Quick Start

### Prerequisites
- **Java**: OpenJDK 17+
- **Node.js**: 18+ with npm
- **Maven**: 3.6+ (or use included wrapper)

### Backend Setup

cd ai-harness-demo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

The backend will run on http://localhost:8080.

### Frontend Setup

cd frontend
npm install
npm start

The frontend will run on http://localhost:3000

## Test the API

#### Get all employees
curl http://localhost:8080/api/employees

#### Search for employees
curl "http://localhost:8080/api/employees/search?search=Charlie"

#### Search by position
curl "http://localhost:8080/api/employees/search?search=Developer"

### Development Profile
The project uses a development profile to disable authentication and enable CORS for local development.


### Always run the backend with dev profile
./mvnw spring-boot:run -Dspring.profiles.active=dev

|Features of Dev Profile:|
|----------|
 |No authentication required|
 |CORS enabled for React dev servers (ports 3000)|
 |Auto-loads sample employee data|
 |Debug logging for security|

## AI Harness: The .cursor Rules
This project's .cursor/rules/ directory is the "harness" that guides AI agents:

| Rule File	     |   Purpose|
|----------|----------|
|java-backend.mdc	|Java standards: Javadoc, @Transactional, DTOs, validation|
|react-patterns.mdc	|React patterns: functional components, custom hooks, TypeScript|
|react-frontend.mdc	|Frontend standards: component organization, testing, accessibility|
 |security-patterns.mdc	|Security: input validation, authentication, logging|
These rules encode 15+ years of enterprise experience into AI-readable format.

## API Endpoints
| Method	| Endpoint	  |   Description |
|----------|----------|----------|
| GET	    | /api/employees	|  Get all employees or search |
 | GET	   | /api/employees/search?search={term}|	  Search by name, position, department|
|GET	    |/api/employees/{id}	|                  Get employee by ID|
 |GET	   | /api/employees/search?search={term}	|  Alternative search endpoint|

## Testing
### Backend Tests

cd backend
./mvnw test

### Frontend Tests

cd frontend
npm test

# Improvement Changelog


|Stage| What I tried | Evidence | Decision | 
|----------|----------|----------|----------|
| Baseline | Started with Cursor Chat (not Agent mode), no rules, manual coding. I used basic Cursor Chat to generate EmployeeController and test classes | Generated basic Java/React code with many issues. It had: no Javadoc, no validation, missing tests, inconsistent patterns | Established starting point |
 |Iteration 1 | Created .cursor/rules/ folder with java-backend.mdc and react-frontend.mdc. I added `.cursor/rules/java-backend.mdc` with Java coding standards (Javadoc, @Transactional, DTOs, validation) | Code quality improved, followed patterns. The generated code now had: Javadoc on all public methods, proper DTOs, @Valid annotations | Kept — improved code quality significantly |
 |Iteration 2 | Added security patterns and validation rules (security-patterns.mdc) | Fewer security issues, proper validation. Code now had: input validation, proper error handling, no security warnings | Kept — essential for enterprise-grade |
 |Iteration 3 |Created react-patterns.mdc for frontend consistency and consistent React development | Consistent React components, proper hooks. React components now: functional with TypeScript, proper hooks, accessibility attributes |Kept — for improved and consistent frontend quality |
  |Iteration 4 | Tried to run with `sudo ./mvnw` | Permission denied errors | Removed — learned to use `chmod +x` and run without sudo|
  |Iteration 5 | Added CORS configuration to fix "Cross-Origin Request Blocked" | Successfully accessed API from React app | Kept — essential for full-stack integration|
  |Iteration 6 | Created `application-dev.properties` and `DevSecurityConfig.java` with `@Profile("dev")`|Bypassed authentication in development environment|Kept — enabled local development without login|
   |Iteration 7 |Switched from Chat to Agent mode with explicit rule references | Agent better understood context and applied all rules | Final — more reliable and consistent|
   |Iteration 8|Added `@CrossOrigin` annotation and MCP server for code verification| Automated quality checks, no CORS errors | Final — established complete verification loop |



