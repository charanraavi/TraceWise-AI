# TraceWise AI

### AI-Powered API Incident Management & Intelligent Troubleshooting

TraceWise AI is an intelligent API incident management platform built with Java and Spring Boot that helps development teams record, manage, investigate, and troubleshoot API failures. It integrates a locally hosted Large Language Model (LLM) through Ollama to generate AI-assisted incident analysis and maintain a history of previous analyses.

The platform combines secure REST APIs, role-based access control, persistent incident records, and AI-assisted troubleshooting in a single backend application.

---

## 1. Real-World Problem Statement

In real-world software systems, APIs can fail because of database connectivity issues, invalid requests, authentication problems, unexpected server errors, or incorrect application configurations.

When an incident occurs, developers often need to inspect error messages, review logs, identify possible root causes, and determine the next debugging steps. This process can be time-consuming, especially when multiple incidents occur simultaneously.

TraceWise AI addresses this problem by providing a centralized system where teams can record API incidents, organize incident information, and request AI-generated troubleshooting suggestions.

**Example scenario**

Imagine an e-commerce application's order API starts returning HTTP `500 Internal Server Error`. Orders cannot be processed, and the development team needs to investigate the failure.

Using TraceWise AI, a team member can:

1. Record the failing API endpoint, HTTP status code, error message, and stack trace.
2. Store the incident in PostgreSQL for future reference.
3. Request an AI-assisted analysis of the incident.
4. Receive possible causes and suggested troubleshooting steps from the local LLM.
5. Save and retrieve previous analysis results to support continued investigation.

This workflow helps developers organize incident information and begin troubleshooting more efficiently. AI-generated suggestions are intended to assist developers; they do not guarantee the root cause or replace human verification.

---

## 2. Key Features

* **Incident Lifecycle Management:** Create, retrieve, update, and delete API incident records.
* **JWT Authentication:** Secure protected endpoints using JSON Web Tokens and Spring Security.
* **Role-Based Access Control (RBAC):** Apply permissions based on `PLATFORM_ADMIN`, `INCIDENT_ANALYST`, and `DEVELOPER` roles.
* **AI-Powered Incident Analysis:** Use Ollama with the `llama3.2:3b` model to generate troubleshooting suggestions.
* **Persistent AI Analysis History:** Store multiple analysis results for an incident and retrieve historical records.
* **Pagination and Sorting:** Retrieve incident and analysis records in manageable, ordered results.
* **Request Validation:** Validate incoming data to help prevent invalid incident records.
* **Centralized Exception Handling:** Return structured error responses for application and API errors.
* **REST API Documentation:** Explore and test endpoints using Swagger UI and OpenAPI.
* **PostgreSQL Persistence:** Store incident information and AI analysis history in a relational database.

---

## 3. Technology Stack

| Technology                   | Purpose                                        |
| ---------------------------- | ---------------------------------------------- |
| Java 21                      | Core programming language                      |
| Spring Boot                  | Application framework and backend development  |
| Spring Web MVC               | REST endpoints and HTTP request handling       |
| Spring Security              | Authentication and endpoint authorization      |
| JWT / OAuth2 Resource Server | Bearer-token validation and secured API access |
| Spring Data JPA              | Database access and repository operations      |
| Hibernate                    | ORM and entity persistence                     |
| PostgreSQL                   | Persistent relational data storage             |
| Ollama                       | Local LLM runtime                              |
| Llama 3.2 (3B)               | AI-assisted incident analysis                  |
| Maven                        | Dependency management and build automation     |
| Swagger / OpenAPI            | REST API documentation and interactive testing |
| JUnit 5                      | Automated testing                              |
| Mockito                      | Unit testing with mocked dependencies          |
| MockMvc                      | Spring MVC integration testing                 |
| IntelliJ IDEA                | Development environment                        |
| pgAdmin 4                    | PostgreSQL database administration             |
| Postman                      | Manual REST API testing                        |
| Git / GitHub                 | Version control and project hosting            |

---

## 4. System Architecture

TraceWise AI follows a layered architecture that separates HTTP handling, business logic, data access, and security responsibilities.

### Request and Processing Flow

```text
Client / Postman / Swagger UI
              |
              v
       Spring Security
     JWT Authentication
              |
              v
      IncidentController
              |
              v
       IncidentService
         /         \
        v           v
IncidentRepository  OllamaService
        |                |
        v                v
   PostgreSQL       Ollama Runtime
        |            llama3.2:3b
        v                |
 Incident Records        v
                  AI Analysis Result
                         |
                         v
                Incident + Analysis
                      History
```

### Main Components

**Controller Layer**

* Accepts incoming REST API requests.
* Validates request data.
* Returns HTTP responses and structured response objects.

**Service Layer**

* Implements incident management and analysis workflows.
* Coordinates repository operations.
* Communicates with the AI service and manages analysis persistence.

**Repository Layer**

* Uses Spring Data JPA to access PostgreSQL.
* Retrieves incidents and analysis records, including paginated history.

**Entity and DTO Layer**

* Represents persisted incidents and AI analysis history.
* Separates internal database entities from API response structures.

**Security Layer**

* Validates JWT bearer tokens.
* Applies role-based authorization to protected operations.

**AI Integration Layer**

* Sends incident details to the local Ollama API.
* Receives generated troubleshooting suggestions.
* Supports saving the analysis against the incident and in its history.

---

## 5. AI Integration Workflow

TraceWise AI uses a locally running LLM rather than requiring an OpenAI API key for its current AI-analysis implementation.

The integration uses Ollama's local generation API and the `llama3.2:3b` model.

1. A client submits an incident-analysis request.
2. The backend retrieves the incident information.
3. `OllamaService` sends the analysis input to the local Ollama service.
4. The model generates possible causes and troubleshooting suggestions.
5. The backend saves the result to the incident record and records it in the analysis-history table.
6. The client can retrieve the latest incident information and previous analyses through the REST API.

**Why use a local LLM?**

* Avoids a mandatory paid external LLM API for the current setup.
* Enables local experimentation and development.
* Provides a practical way to demonstrate AI integration in a Java backend project.

The local Ollama service must be running and reachable by the application. Model output can be incomplete or incorrect and should be reviewed before making production changes.

---

## 6. Security and Authorization

Spring Security and JWT protect the API endpoints. After successful authentication, the client uses the issued bearer token to access protected resources.

The application includes three roles:

| Role               | Intended responsibility                                 |
| ------------------ | ------------------------------------------------------- |
| `PLATFORM_ADMIN`   | Administrative operations, including incident deletion  |
| `INCIDENT_ANALYST` | Incident investigation and analysis                     |
| `DEVELOPER`        | Development-focused incident access and troubleshooting |

Role-based authorization restricts incident deletion to `PLATFORM_ADMIN` under the configured security rules.

Security behavior has been manually tested, including unauthorized requests and deletion restrictions for Developer and Incident Analyst roles.

---

## 7. Database Design

TraceWise AI uses PostgreSQL for persistent storage.

### Incident

Stores API incident details, including:

* Incident ID
* Endpoint
* HTTP status code
* Error message
* Stack trace
* AI analysis result

### Incident Analysis

Stores individual analysis-history entries, including:

* Analysis ID
* Associated incident
* Analysis text
* Creation timestamp

An incident can have multiple analysis-history records. This preserves earlier troubleshooting results rather than retaining only the latest analysis.

---

## 8. API Capabilities

The backend provides REST endpoints for incident management, authentication, and AI-assisted analysis.

| Capability             | Description                                          |
| ---------------------- | ---------------------------------------------------- |
| Authentication         | Registration and login                               |
| Incident creation      | Record a new API incident                            |
| Incident retrieval     | Retrieve incident information                        |
| Incident update        | Modify an existing incident                          |
| Incident deletion      | Delete an incident subject to authorization          |
| Status filtering       | Retrieve incidents by status                         |
| Pagination and sorting | Retrieve ordered subsets of records                  |
| AI analysis            | Generate troubleshooting suggestions for an incident |
| Analysis history       | Retrieve previous analyses for an incident           |
| API documentation      | Explore endpoints through Swagger UI                 |

Open Swagger UI using the URL configured by the application, commonly:

`http://localhost:8080/swagger-ui/index.html`

The actual port and endpoint paths depend on the application configuration.

---

## 9. Local Development Setup

### Prerequisites

Install or configure:

* JDK 21
* PostgreSQL
* IntelliJ IDEA or another Java IDE
* Ollama
* The `llama3.2:3b` model
* Git (optional, for repository management)

### Step 1: Prepare the database

Create a PostgreSQL database named:

```sql
CREATE DATABASE tracewise_ai;
```

Configure the datasource URL, database username, and password in your local application configuration or environment variables.

### Step 2: Prepare Ollama

Install Ollama and download the model:

```bash
ollama pull llama3.2:3b
```

Start Ollama and confirm that the model is available:

```bash
ollama list
```

The application must be configured to communicate with the Ollama service at its configured local URL.

### Step 3: Configure application secrets

Configure the JWT signing secret and database credentials using local configuration or environment variables. Never commit actual secrets or production credentials to GitHub.

### Step 4: Run the application

From the project root, execute:

**Windows PowerShell**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

### Step 5: Explore the API

Open the configured Swagger UI URL or use Postman to register a user, authenticate, authorize protected requests with a JWT, create incidents, and test the analysis workflow.

---

## 10. Testing

The project includes automated tests using JUnit 5, Mockito, Spring Boot testing support, and MockMvc.

Testing covers application context loading, service-layer behavior, JWT-protected requests, invalid or missing tokens, role-based deletion restrictions, and the Platform Admin deletion workflow.

Run the full test suite on Windows:

```powershell
.\mvnw.cmd clean test
```

The complete Maven test suite has passed in the development environment.

Manual API testing has also been performed using Postman and Swagger UI.

---

## 11. Engineering Highlights

This project demonstrates practical experience with:

* Building layered REST APIs using Java and Spring Boot.
* Implementing CRUD operations with Spring Data JPA and PostgreSQL.
* Applying JWT authentication and role-based access control.
* Integrating a locally hosted LLM into a backend service.
* Persisting AI-generated results and maintaining analysis history.
* Implementing request validation and centralized exception handling.
* Supporting pagination and sorting.
* Testing application behavior using JUnit, Mockito, and MockMvc.

---

## 12. Current Status and Future Improvements

The core backend capabilities—including incident management, authentication, authorization, AI-assisted analysis, analysis history, validation, exception handling, and pagination/sorting—have been implemented and tested.

Potential next improvements include:

* A dedicated web dashboard for incident monitoring.
* Containerization with Docker and Docker Compose.
* CI/CD automation.
* Centralized logging, metrics, and monitoring.
* Improved AI prompts, structured model output, and analysis quality evaluation.
* Production deployment with a securely hosted AI service.
* Additional automated tests for CRUD, validation, and failure scenarios.

These enhancements are future work unless explicitly implemented in the current repository.

---

## License

Choose and add an appropriate open-source license before publicly distributing the project.
