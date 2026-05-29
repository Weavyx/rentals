# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Start MySQL via Docker (required before running the app locally)
docker-compose up -d

# Stop Docker services
docker-compose down
```

On Windows, use `mvnw` instead of `./mvnw`.

## Environment Setup

Copy `.env.example` to `.env` and fill in the values before starting:

| Variable | Purpose |
|---|---|
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `MYSQL_ROOT_PASSWORD` | Root password for the Docker MySQL container |
| `JWT_SECRET` | HS256 signing key — must be at least 64 characters |
| `UPLOAD_DIR` | Directory for uploaded rental images (default: `uploads/`) |
| `BASE_URL` | Frontend origin for CORS (default: `http://localhost:3001`) |

The Docker Compose file starts a MySQL 8.0 instance and auto-runs `src/main/resources/sql/init.sql` to create the schema.

## Architecture

This is a Spring Boot REST API (Java 17, Spring Boot 4.x) for managing property rentals. The application uses Spring Data JPA + MySQL, Spring Security with JWT authentication, and supports multipart file uploads for rental property images.

**Domain model (three entities):**

- **User** — accounts for both owners and renters; owns rentals
- **Rental** — property listing with image, surface, price, description; belongs to a user (owner)
- **Message** — communication tied to a rental and a user

**Typical Spring layering expected:**
- `controller/` — `@RestController` classes, one per entity
- `service/` — business logic, JWT token handling, file storage
- `repository/` — `@Repository` / `JpaRepository` interfaces
- `model/` (or `entity/`) — JPA `@Entity` classes
- `dto/` — request/response payloads (separate from entities)
- `security/` — Spring Security config, JWT filter, `UserDetailsService`

**Authentication flow:** JWT tokens issued on login/register; secured endpoints require a `Bearer` token in the `Authorization` header. The secret is read from the `JWT_SECRET` environment variable.

**File uploads:** Rental pictures are stored on disk under `UPLOAD_DIR` and served as static resources; the stored path/URL is persisted on the `Rental` entity.