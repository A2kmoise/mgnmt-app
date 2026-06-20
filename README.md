# Spring Boot Authentication Template

## Overview

This repository provides a **secure, production‑ready Spring Boot template** featuring JWT‑based authentication and role‑based access control. It includes:

- A `User` entity with UUID, email, and password fields
- Two roles: `USER` and `ADMIN`
- Spring Security configuration with CORS, CSRF protection, and request matchers
- A custom `JwtAuthenticationFilter` (`OncePerRequestFilter`) for extracting and validating JWTs
- Utility classes for password encoding and JWT handling (`JwtUtil`)
- An email service abstraction ready for SMTP or third‑party providers

The code follows best practices for **security**, **clean architecture**, and **testability**.

## Prerequisites

- JDK 21 (or higher) installed and `JAVA_HOME` configured
- Maven 3.9+ (`mvn --version` to verify)
- Docker (optional, for running a MySQL/PostgreSQL container locally)

## Getting Started

### 1. Clone the repository
```bash
git clone <repository-url>
cd app
```

### 2. Configure the database
Create a local MySQL (or PostgreSQL) container, or use an existing instance. Example with Docker:
```bash
docker run --name auth-db -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=authdb -p 3306:3306 -d mysql:8.0
```
Update `src/main/resources/application.yml` with your DB credentials.

### 3. Build the project
```bash
./mvnw clean package
```

### 4. Run the application
```bash
java -jar target/*.jar
```
The service will start on `http://localhost:8080`.

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/login` | Authenticate user, receive JWT |
| `POST` | `/api/auth/register` | Register new user (default role `USER`) |
| `GET`  | `/api/users/me` | Retrieve authenticated user details |
| `GET`  | `/api/admin/**` | Endpoints restricted to `ADMIN` role |

All protected endpoints require an `Authorization: Bearer <jwt>` header.

## Security Highlights
- **Password hashing** using BCrypt (`PasswordEncoder` bean)
- **Stateless JWT** authentication with short expiry (configurable)
- **CORS** whitelist defined in `SecurityConfig`
- **CSRF** disabled for stateless APIs but can be enabled for form‑based flows
- **Input validation** performed in DTOs via `javax.validation` annotations

## Testing
Run unit and integration tests with:
```bash
./mvnw test
```
Coverage reports are generated under `target/site/jacoco`.

## Docker (Optional)
A Dockerfile is provided for containerised deployment.
```bash
docker build -t spring-auth-app .
docker run -p 8080:8080 spring-auth-app
```

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feat/your-feature`)
3. Ensure `mvn verify` passes
4. Submit a Pull Request

## License
This project is licensed under the **MIT License** – see `LICENSE` for details.

---
