# Todo Application Guidelines

## Project Overview
This is a Todo application built with Spring Boot and Kotlin, following clean architecture principles. The application provides user authentication, task management, and administrative capabilities.

## Architecture
The application follows a hexagonal (ports and adapters) architecture with clear separation of concerns:

### Layers
1. **Domain Layer** - Contains the core business logic and entities
   - Located in `com.example.todo.domain`
   - Independent of external frameworks and technologies

2. **Application Layer** - Contains use cases that orchestrate the domain
   - Located in `com.example.todo.application`
   - Defines ports (interfaces) for the domain to interact with the outside world

3. **Infrastructure Layer** - Contains adapters that implement the ports
   - Located in `com.example.todo.infrastructure`
   - Includes web controllers, security configuration, and database repositories

## Key Components

### Domain Models
- **User** - Represents a registered user with authentication capabilities
  - Contains business logic for password management, account locking, etc.
  - Uses value objects for identifiers (UserId)

### Application Use Cases
- **UserAuthenticationUseCase** - Handles user login
- **UserRegistrationUseCase** - Handles user registration
- **AdminUseCase** - Provides administrative capabilities like unlocking user accounts

### Infrastructure Components
- **Security**
  - JWT-based authentication
  - Custom filters for token processing
  - Role-based access control
  - BCrypt password encoding

## Development Guidelines

### Code Organization
- Follow the package structure based on clean architecture
- Keep domain models free from framework dependencies
- Use interfaces (ports) to define boundaries between layers

### Naming Conventions
- Use descriptive names for classes and methods
- Follow Kotlin naming conventions
- Use suffixes to indicate the role of a class:
  - `*UseCase` for application services
  - `*Repository` for data access interfaces
  - `*Controller` for web controllers

### Testing
- Write unit tests for domain logic
- Write integration tests for use cases
- Write end-to-end tests for API endpoints

### Security Best Practices
- Never store plain text passwords
- Use JWT tokens with appropriate expiration
- Implement account locking after failed login attempts
- Validate all user input

## Setup Instructions

### Prerequisites
- JDK 17 or higher
- Gradle

### Building the Application
```bash
./gradlew build
```

### Running the Application
```bash
./gradlew bootRun
```

### Configuration
- Application configuration is in `application.properties`
- Security settings are in `SecurityConfig.kt`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate a user and get a JWT token

### Admin
- `GET /api/admin/users` - Get all users (admin only)
- `POST /api/admin/users/{userId}/unlock` - Unlock a user account (admin only)

### User Interface
- `/` - Home page
- `/login` - Login page
- `/signup` - Registration page