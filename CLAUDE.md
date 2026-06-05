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

**Java version : 17** (`pom.xml` : `<java.version>17</java.version>`)

> **Problème connu — conflit JDK sur Windows :**
> Le système a JDK 25 (Eclipse Adoptium) installé. IntelliJ IDEA bascule automatiquement
> sur Java 25 à chaque redémarrage. Pour forcer Java 17 dans le terminal PowerShell :
>
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
> .\mvnw compile   # ou spring-boot:run, test, etc.
> ```
>
> Dans IntelliJ : `File → Project Structure → Project → SDK` → sélectionner **Java 17**.
> Pour éviter la régression : `File → New Projects Setup → Structure for New Projects → SDK` → **Java 17**.

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

Spring Boot REST API (Java 17) for managing property rentals. Stack: Spring Data JPA + MySQL, Spring Security + JWT, multipart file uploads.

### Package layout

```
com.openclassrooms.rentals/
├── controller/     @RestController — one class per entity, no business logic
├── service/        business logic, JWT, file storage
├── repository/     JpaRepository interfaces only
├── model/          JPA @Entity classes (User, Rental, Message)
├── dto/            request/response payloads, never expose entities directly
├── security/       SecurityConfig, JwtAuthenticationFilter
└── exception/      GlobalExceptionHandler (@RestControllerAdvice)
```

### Domain model

- **User** — owner/renter account; implements `UserDetails` (Spring Security); email is the unique identifier
- **Rental** — property listing (name, surface, price, picture URL, description); belongs to a User via `@ManyToOne owner`
- **Message** — message sent about a rental; linked to both a Rental and a User

### API routes

**Auth** — `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`
**Rentals** — `GET /api/rentals`, `GET /api/rentals/{id}`, `POST /api/rentals` (multipart), `PUT /api/rentals/{id}`
**Messages** — `POST /api/messages`
**Users** — `GET /api/user/{id}`

Public routes (no JWT required): `/api/auth/login`, `/api/auth/register`, `/v3/api-docs/**`, `/swagger-ui/**`.
All other routes require a `Bearer <token>` header.

> **Current state:** Auth endpoints (`/register`, `/login`, `/me`) sont implémentés. Stubs restants : `RentalController`, `RentalService`, `MessageService`.

### Authentication flow

1. Client calls `/api/auth/login` or `/api/auth/register` → receives `{ "token": "..." }`
2. Client includes `Authorization: Bearer <token>` on every subsequent request
3. `JwtAuthenticationFilter` (runs before every request) extracts the token, validates it via `JwtService`, and populates `SecurityContextHolder`
4. Controllers receive the authenticated user via `Authentication authentication` parameter; `authentication.getName()` returns the email

JWT is signed with HS256. Secret comes from `JWT_SECRET` env var (min 64 chars). Expiration: 86400000 ms (24h), configured in `application.properties`.

### Coding conventions

**JSON serialization (Jackson)**
- Property names: `snake_case` (`spring.jackson.property-naming-strategy=SNAKE_CASE`)
- Dates: ISO 8601 with UTC timezone suffix — e.g. `"2024-01-15T10:30:00Z"` (`write-dates-as-timestamps=false`)
- Always use `Instant` (never `LocalDateTime`) for date/time fields in entities and DTOs

**Entities**
- Lombok: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder` — never `@Data` on JPA entities (breaks Hibernate proxies)
- Timestamps: `@CreationTimestamp` / `@UpdateTimestamp` + `Instant` fields — Hibernate fills them automatically, no manual assignment needed
- Passwords are always stored as BCrypt hashes; never include the `password` field in any DTO response

**DTOs**
- Lombok: `@Data @AllArgsConstructor @NoArgsConstructor` is acceptable (plain POJOs, not JPA entities)
- Validation: use `@NotBlank`, `@NotNull`, `@Email` on request DTOs; controllers call `@Valid`
- Timestamps in response DTOs: `Instant` field type

**Services**
- Injected via constructor (no `@Autowired` on fields)
- `authentication.getName()` in controllers gives the current user's email; pass it to service methods

**Error handling**
`GlobalExceptionHandler` maps exceptions to HTTP status codes:
- `UsernameNotFoundException` → 401
- `AccessDeniedException` → 403
- `MethodArgumentNotValidException` → 400
- `RuntimeException` → 400 (ex. email déjà utilisé à l'inscription)
- `Exception` (catch-all) → 500 (logged)
Response bodies for errors are empty (no JSON body).

### File uploads

Rental pictures are saved to disk under `UPLOAD_DIR`. The `Rental.picture` field stores the full public URL (e.g. `http://localhost:3001/uploads/abc.jpg`), not a file path. URL is constructed by `RentalService` after saving the file.
