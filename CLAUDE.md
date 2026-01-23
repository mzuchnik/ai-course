# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Lexpage is a Spring Boot 4.0.1 web application using:
- Java 25
- Spring Data JPA with PostgreSQL
- Liquibase for database migrations
- Thymeleaf for server-side templating
- Spring Web MVC

Package structure: `pl.klastbit.lexpage`

## Build and Development Commands

### Building the project
```bash
./gradlew build
```

### Running the application
```bash
./gradlew bootRun
```

### Running tests
```bash
./gradlew test
```

### Running a single test class
```bash
./gradlew test --tests pl.klastbit.lexpage.SpecificTestClass
```

### Running a single test method
```bash
./gradlew test --tests pl.klastbit.lexpage.SpecificTestClass.testMethod
```

### Clean build
```bash
./gradlew clean build
```

## Database

- **Database**: PostgreSQL
- **Migrations**: Managed by Liquibase
- **Configuration**: `src/main/resources/application.properties`

Database connection details must be configured in `application.properties` before running the application.

## Detailed Implementation Guidelines

**IMPORTANT:** This project has comprehensive coding standards and best practices:

### Backend Implementation Rules
**File:** `.ai/rules/backend.md`

This file contains all detailed rules for backend Java code implementation:
- Lombok usage (mandatory annotations, patterns)
- Java Records for immutable classes (DTOs, Value Objects, Commands)
- Domain-Driven Design principles (Aggregates, Value Objects, Domain Services, Domain Events)
- Hexagonal Architecture implementation (Ports & Adapters, package structure)
- REST API conventions and global exception handling with ProblemDetail (RFC 7807)
- Logging best practices with SLF4J
- Testing conventions and patterns
- SOLID principles and clean code practices

**ALWAYS refer to `.ai/rules/backend.md` when implementing backend features.**

### Frontend Implementation Rules
**File:** `.ai/rules/frontend.md`

This file contains all detailed rules for frontend implementation:
- Material Tailwind HTML components usage
- Thymeleaf template patterns (layouts, fragments, components)
- Server-Side Rendering (SSR) architecture
- Build process and development workflow
- Tailwind CSS configuration and conventions
- Responsive design patterns (mobile-first)
- Accessibility and SEO guidelines
- Component creation and page structure
- Critical: `<th:block th:replace>` usage to prevent infinite loops

**ALWAYS refer to `.ai/rules/frontend.md` when implementing frontend features.**

## Architecture Notes

### Hexagonal Architecture (Ports and Adapters) with DDD

This project follows **Hexagonal Architecture** principles based on **Domain-Driven Design (DDD)**:

**Core Principles:**
- **Domain Layer**: Contains the business logic, domain entities, value objects, and domain services. This is the heart of the application and should be independent of any framework or infrastructure concerns.
- **Application Layer**: Contains use cases and application services that orchestrate domain logic. Defines ports (interfaces) for communication with the outside world.
- **Infrastructure Layer**: Contains adapters that implement the ports defined by the application layer. This includes:
  - **Inbound Adapters** (Primary/Driving): REST controllers, web interfaces, CLI commands
  - **Outbound Adapters** (Secondary/Driven): Database repositories, external API clients, message queue publishers

**Package Structure:**
- `domain/` - Domain entities, value objects, domain services, domain events
- `application/` - Use cases, application services, port interfaces
- `infrastructure/` - Infrastructure layer:
  - `web/` - REST controllers, web MVC controllers (inbound adapters)
  - `adapters/` - Implementations of domain ports (outbound adapters):
    - Database repositories (JPA)
    - External API clients
    - Message queue publishers
    - Other outbound integrations
  - `config/` - Spring configuration classes

**Key Guidelines:**
- Domain layer must not depend on infrastructure or application layers
- Dependencies point inward: Infrastructure → Application → Domain
- Use dependency inversion: infrastructure implements interfaces defined in application/domain layers
- Domain entities should be rich with behavior, not anemic data holders
- Aggregate roots enforce consistency boundaries
- Value objects are immutable
- Domain events communicate state changes

**Technology Mapping:**
- Spring Boot framework provides the infrastructure foundation
- JPA/Hibernate for database adapters in `infrastructure/adapters/`
- Liquibase manages database schema migrations
- Thymeleaf templates render views in the web adapter
- Adapters in `infrastructure/adapters/` implement port interfaces defined in the domain layer