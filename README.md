# SkillConnect

SkillConnect is a Spring Boot application that provides a REST endpoint for registering users with validated profiles and roles. User passwords are encoded before persistence, and registration errors are returned as structured API responses.

## Requirements

- Java 21
- MySQL

Create a MySQL database named `skill_connect`, then configure the connection with environment variables. The URL and username default to `jdbc:mysql://localhost:3306/skill_connect` and `root`; set `DB_PASSWORD` to the database password. `DB_URL` and `DB_USERNAME` can override the other defaults.

On Windows, start the application with:

```powershell
.\mvnw.cmd spring-boot:run
```

The registration endpoint is `POST /api/auth/register`. Request fields are `name`, `email`, `password`, `contact`, and `role`. Supported roles are `RECRUITER`, `FREELANCER`, `CLIENT`, and `ADMIN`; public admin registration is rejected.

Run the test suite with:

```powershell
.\mvnw.cmd test
```

## File Guide

| File | Description |
| --- | --- |
| `.gitattributes` | Declares Git text and line-ending handling for consistent checkouts. |
| `.gitignore` | Excludes generated build output and IDE-local files from version control. |
| `.github/modernize/java-upgrade/.gitignore` | Keeps local Java-upgrade workflow artifacts out of the repository. |
| `.mvn/wrapper/maven-wrapper.properties` | Selects the Maven distribution used by the Maven Wrapper. |
| `mvnw` | Unix shell entry point for running Maven through the wrapper. |
| `mvnw.cmd` | Windows command entry point for running Maven through the wrapper. |
| `pom.xml` | Declares the Java version, Spring Boot dependencies, and Maven build configuration. |
| `HELP.md` | Generated Spring Boot reference links and getting-started material. |
| `src/main/java/com/project/skill_connect/SkillConnectApplication.java` | Spring Boot application entry point. |
| `src/main/java/com/project/skill_connect/config/SecurityConfig.java` | Configures request authorization and the BCrypt password encoder. |
| `src/main/java/com/project/skill_connect/controllers/AuthController.java` | Exposes the user registration REST endpoint and maps request/response data. |
| `src/main/java/com/project/skill_connect/dto/LoginRequest.java` | Defines the email and password fields for a login request payload. |
| `src/main/java/com/project/skill_connect/dto/RegisterRequest.java` | Defines and validates the registration request payload. |
| `src/main/java/com/project/skill_connect/dto/UserResponse.java` | Defines the user details returned after successful registration. |
| `src/main/java/com/project/skill_connect/entities/Role.java` | Lists the roles assignable to a user. |
| `src/main/java/com/project/skill_connect/entities/User.java` | JPA user entity, including profile fields and creation/update timestamps. |
| `src/main/java/com/project/skill_connect/exceptions/AdminRegistrationNotAllowedException.java` | Signals attempts to register through the public endpoint as an admin. |
| `src/main/java/com/project/skill_connect/exceptions/ApiError.java` | Response model for API errors and field-validation details. |
| `src/main/java/com/project/skill_connect/exceptions/EmailAlreadyExistsException.java` | Signals registration with an email already in use. |
| `src/main/java/com/project/skill_connect/exceptions/GlobalExceptionHandler.java` | Converts validation and registration exceptions into HTTP error responses. |
| `src/main/java/com/project/skill_connect/repositories/UserRepository.java` | Provides JPA persistence and email-existence lookup for users. |
| `src/main/java/com/project/skill_connect/services/UserService.java` | Applies registration rules, hashes passwords, and persists users. |
| `src/main/resources/application.properties` | Configures the application and reads database connection details from environment variables. |
| `src/test/java/com/project/skill_connect/SkillConnectApplicationTests.java` | Verifies that the Spring application context starts successfully. |

Compiled files under `target/`, workspace-specific VS Code settings, and local modernization hook artifacts under `.github/modernize/java-upgrade/` are intentionally excluded from source control by the ignore rules.