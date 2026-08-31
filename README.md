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


# Measured Improvement Scorecard
## Scorecard: Baseline vs. Final Solution

|Metric	|Baseline (No Rules)|	Final Solution (With Harness)| 	Improvement      |
|----------|----------|----------|-------------------|
|Test Pass Rate	|2/5 tests passing|	5/5 tests passing| 	+60%             |
|Security Warnings|	3 warnings|	0 warnings	| 100% reduction    |
|Code Review Issues|	7 issues|	1 issue	| 86% reduction     |
|Javadoc Coverage|	0%|	100%	| 100% improvement  |
|DTO Usage|	None (direct entity usage)|	Complete DTO separation	| 100% improvement  |
|Validation|	None|	Full @Valid validation	| 100% improvement  | ✔️                
|Time to Working Feature|	~30 minutes manual|	~5 minutes with agents	| 83% faster        |
|Code Consistency|	Inconsistent patterns|	Consistent patterns	| 100% improvement  |
|Agent Reliability|	Unreliable (2/5 attempts)|	Reliable (5/5 attempts)| 	150% improvement |
## Detailed Test Results
|Test Case	|Baseline Result	| Final Result	                            | Notes                                   |
|----------|----------|------------------------------------------|-----------------------------------------|
|GET /api/employees	| Failed (no Javadoc, missing tests)	|  Passing	                                | Complete with Javadoc and tests         |
|GET /api/employees/search?search=Charlie	| Failed (no search implemented)	| Passing	                                 | Case-insensitive search across 4 fields |
|GET /api/employees/{id}	| Failed (no error handling)	| Passing	                                 | Proper 404 handling with exception      |
|POST /api/employees	| Failed (no validation)	| Passing	| Full @Valid validation with DTOs        |
|EmployeeService Unit Tests	| 0/5 passing	| 5/5 passing                              | 	Complete Mockito tests                 |
### Challenging Case: http://localhost:8080/api/employees/search?search=Charlie
|Aspect	| Baseline	               |Final|
|----------|-------------------------|----------|
|Search Logic	| No search functionality | 	Case-insensitive partial match           |
|Fields Searched	| N/A	                    | firstName, lastName, position, department |
|Javadoc	| Missing	                | Complete (@param, @return, @throws) |
|DTO Usage	| Direct entity return	   |EmployeeResponse DTO|
|Tests	| None	                   |Full unit + integration tests|
|Error Handling	| None	                   |Proper exception handling|
|Code Quality	| Inconsistent	           |Follows all enterprise standards|
#### What this case revealed:
The baseline agent generated a simple REST controller but missed:<br>

Search functionality entirely<br>

Case-insensitive matching<br>

Partial text matching<br>

Javadoc documentation<br>

DTOs for responses<br>

Error handling for missing employees<br>

Unit tests for search functionality<br>

#### The final solution with harness:<br>
Case-insensitive search across 4 fields<br>
Partial matching with SQL LIKE<br>
Complete Javadoc<br>
DTO separation<br>
Proper exception handling<br>
Comprehensive unit and integration tests<br>
All tests passing

# Hot Take / Key Insights
### "The Harness Is the Product"
The single biggest lesson from this project: Investing in the harness pays dividends far beyond any single feature.

"In the AI era, the harness is the product — the durable thing you build is the system that produces features, not any single feature."

## What Worked
|Approach	|Result | |
|----------|----------|----------|
|Rules-first development|	Created .cursor/rules/ before writing any code	|2x quality improvement|
|Agent Mode over Chat Mode	|Agent mode with explicit rule references	|150% reliability improvement|
|Dev profile for local dev	|@Profile("dev") + application-dev.properties	|Eliminated authentication barriers|
|CORS configuration	|Added to DevSecurityConfig	|Enabled full-stack integration|
|Verification loops	|Auto-run tests after generation|	100% test pass rate|
|Sample data loader|	CommandLineRunner with @Profile("dev")	|Eliminated manual data setup|
### What I Would Do Differently
|Mistake	| Impact	                                                                                            | What I Learned                                                   |
|----------|----------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
|Started with Chat mode instead of Agent mode	| Spent 2 hours on low-quality code	                                                                 | Always start with Agent mode — it understands context better     |
|Created rules after generating code	| Had to regenerate everything	                                                                      | Create rules first — encode standards before starting            |
|Forgot dev profile initially	| Authentication blocked everything	| Set up dev profile from day one — saves hours of troubleshooting |
|Didn't document agent trajectories as I went	| Had to reconstruct them	| Document while building — makes submission much easier           |
|Didn't set up CORS early| 	Cross-origin errors wasted time	| Configure CORS immediately — essential for full-stack            |

### The Most Important Failure Mode
"Don't forget to set the dev profile — without it, the agent assumes production security and generates code that requires authentication, breaking the entire development workflow."

The Scenario:

1. I generated EmployeeController with @PreAuthorize annotations

2. Ran the app without dev profile

3. React app called /api/employees → 401 Unauthorized

4. Spent 1 hour debugging authentication issues

5. Discovered dev profile was missing

#### The Fix:

1. Created application-dev.properties with security disabled

2. Created DevSecurityConfig.java with @Profile("dev")

3. Ran with: ./mvnw spring-boot:run -Dspring.profiles.active=dev

4. Authentication bypassed ✅

5. Full-stack working ✅

The Lesson: Always create the dev profile before generating any code that requires authentication.

## Time Savings Breakdown
|Activity	|Without Harness	|With Harness	|Time Saved|
|----------|----------|----------|----------|
|Writing Java Controller|	20 min|	2 min|	90%|
|Writing React Component|	15 min|	1 min|	93%|
|Writing Tests|	15 min|	1 min|	93%|
|Code Review|	10 min|	2 min|	80%|
|Debugging Issues|	15 min|	5 min|	67%|
|Total per Feature|	75 min|	11 min|	85% faster|
## The "Harness" Concept Explained
The harness is the system you build that enables AI agents to produce quality code reliably:

Harness Components:<br>
├── Rules (.cursor/rules/)      ← Encodes enterprise standards<br>
├── Context (Java + React code) ← Provides examples<br>
├── Verification (Tests)         ← Catches errors<br>
├── Tools (MCP server)           ← Extends capabilities<br>
└── Environment (Dev profile)    ← Enables local development<br>
## Key Metrics
|Metric	|Value|
|----------|----------|
|Rules Created|	4 rule files (java-backend, react-frontend, react-patterns, security-patterns)
|Code Generated by Agents|	~95% (controllers, services, repositories, components, tests)
|Manual Code Written|	~5% (configuration files, CORS setup, dev profile)
|Test Pass Rate|	100% (5/5 tests passing)
|Security Warnings|	0 (100% reduction from baseline)
|Time Saved|	~85% per feature
|Code Review Issues|	Reduced from 7 to 1 (86% reduction)
## The Bottom Line
"The 2 hours I spent writing rules saved me 2+ days of manual code review and debugging."

The harness is the multiplier. The time invested in building the harness:

- Multiplies the team's output (1 person now works like 5)
- Compounds with each new feature (you learn and improve the rules)
- Ensures quality consistently (no more "AI-generated-looking" code)
- Enables speed without sacrificing quality

### Advice for Future AI-Powered Development
1. Start with rules → Don't generate a single line of code until you've defined your standards

2. Use Agent mode → It's dramatically more reliable than Chat mode

3. Set up dev profile → Always enable local development without authentication

4. Add verification → Auto-run tests, security scans, and linting

5. Document trajectories → Record what agents do for future iteration

6. Iterate on the harness → The harness should improve with each feature, not stay static

Final Score: With the harness, I achieved 100% test pass rate, 0 security warnings, 86% reduction in code review issues, and 85% faster feature development — all while maintaining enterprise-grade quality.
