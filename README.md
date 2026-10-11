# The Coffee Kartel — Spring Boot REST Backend

Production-oriented, enterprise-grade backend service powering **The Coffee Kartel** café application.

---

## 1. Environment & Prerequisites

- **Java Version:** OpenJDK 21 LTS (`21.0.11`+)
- **Build Tool:** Apache Maven 3.9+
- **Framework:** Spring Boot `3.3.5`
- **Database:** MySQL 8.0+
- **Security:** Spring Security 6, JWT (`jjwt 0.12.6`), BCrypt (strength 12)
- **Documentation:** Springdoc OpenAPI 3 / Swagger UI (`2.6.0`)

---

## 2. Project Architecture & Package Structure

```text
backend/
├── pom.xml
├── .env.example
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/coffeekartel/
    │   │   ├── CoffeeKartelApplication.java
    │   │   ├── config/
    │   │   │   ├── CorsConfig.java
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── SecurityConfig.java
    │   │   ├── controller/
    │   │   │   └── HealthController.java
    │   │   ├── dto/
    │   │   │   └── response/
    │   │   │       ├── ApiResponse.java
    │   │   │       ├── ErrorResponse.java
    │   │   │       └── HealthResponse.java
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   └── security/
    │   │       ├── CustomUserDetailsService.java
    │   │       ├── JwtAuthenticationFilter.java
    │   │       └── JwtService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/coffeekartel/
        │   └── CoffeeKartelApplicationTests.java
        └── resources/
            └── application.properties
```

---

## 3. Configuration & Environment Variables

Create your local `.env` file from the provided `.env.example`:

```bash
cp .env.example .env
```

| Variable | Description | Default (Local Dev) |
| :--- | :--- | :--- |
| `PORT` | HTTP port for backend service | `8080` |
| `DB_HOST` | MySQL hostname | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | MySQL database name | `coffee_kartel_db` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | *(local password)* |
| `JWT_SECRET` | 256-bit secret key for signing JWTs | *(ephemeral key generated if omitted)* |
| `JWT_EXPIRATION_MS` | JWT validity in milliseconds | `86400000` (24 hours) |
| `CORS_ALLOWED_ORIGINS`| Permitted origins for browser clients | `http://localhost:3000` |

---

## 4. How to Build & Run

### Step 1: Run Automated Tests
```bash
mvn clean test
```

### Step 2: Package JAR File
```bash
mvn clean package
```

### Step 3: Start the Backend Service
```bash
mvn spring-boot:run
```
Or run the packaged JAR:
```bash
java -jar target/coffee-kartel-backend-1.0.0.jar
```

---

## 5. Verification Endpoints

Once running, access the following URLs in your browser or HTTP client:

- **Health Check API:** [http://localhost:8080/api/health](http://localhost:8080/api/health)
  - Returns `{"status":"UP","service":"The Coffee Kartel Backend"}` (HTTP 200, public)
- **Swagger UI Documentation:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI v3 Specification:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 6. Security Architecture

1. **Stateless Authentication:** Stateless JWT verification via `JwtAuthenticationFilter`.
2. **Password Security:** Salted BCrypt hashing with logarithmic work factor of 12.
3. **CORS Hardening:** Strict origin matching (`http://localhost:3000`), credentials permitted, wildcards blocked.
4. **Normalized Error Handling:** `GlobalExceptionHandler` intercepts exceptions and formats uniform JSON responses without exposing internal stack traces or database schema information.

---

## 7. Next Development Phase (Phase 2)

- Define JPA Entities (`User`, `Role`, `Category`, `MenuItem`, `Order`, `OrderItem`).
- Set up MySQL schema synchronization and initial database seed data for the 5 confirmed signature items.
- Implement User Authentication with JWT generation and Spring Security integration.
