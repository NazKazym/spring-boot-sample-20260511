# Spring Boot Microservices Test Task

This project implements a dual-service architecture for user authentication, inter-service communication, and containerized deployment.

## Architecture Overview

*   **Service A (auth-api)**: Built with Spring Boot (Web, Security, JPA). Handles registration, login, and exposes a protected /process endpoint.
*   **Service B (data-api)**: A specialized service exposing a /transform endpoint. Validates requests via X-Internal-Token.
*   **Postgres**: Centralized data store for users and processing logs.

## Getting Started

### 1. Environment Configuration
Create a .env file in the root directory:

POSTGRES_USER=myuser
POSTGRES_PASSWORD=mypassword
POSTGRES_DB=app_db
JWT_SECRET=your_secure_random_jwt_secret_key
INTERNAL_TOKEN=super-secret-internal-token

### 2. Build the Applications
Build the artifacts on your host machine:

mvn clean install -DskipTests

### 3. Run with Docker Compose
docker compose up -d --build

---

## Testing the Services

### 1. User Registration
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"dev@example.com", "password":"password123"}'

**Response (201 Created):**

### 2. User Login
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"dev@example.com", "password":"password123"}'

**Response (200 OK):**
{
"token": "eyJhbGciOiJIUzI1NiJ9..."
}

### 3. Protected Text Processing
Replace <token> with the value from the login response.

curl -X POST http://localhost:8080/api/process \
-H "Authorization: Bearer <token>" \
-H "Content-Type: application/json" \
-d '{"text":"hello world"}'

**Response (200 OK):**
{
"result": "DLROW OLLEH"
}

---

## Technical Details

*   Security: BCrypt hashing and JJWT 0.12.6.
*   Validation: Jakarta Validation for text constraints.
*   Healthchecks: Docker ensures Postgres readiness before service start.

## Project Structure
├── auth-api/            
├── data-api/            
├── docker-compose.yml   
└── README.md