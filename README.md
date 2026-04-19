# Fleet Management System

This project is a full-stack Fleet Management System built with Spring Boot 4 (Java 21) and a modern single-page web frontend. It enables efficient management of vehicles, drivers, assignments, trips, expenses, and maintenance records for transportation fleets.

## Features
- **Backend:**
    - RESTful API using Spring Boot 4
    - PostgreSQL database integration
    - JPA/Hibernate for ORM
    - Secure endpoints with validation and exception handling
    - Comprehensive service and repository layers
    - Integration and unit tests (JUnit 5, Mockito, MockMvc)
- **Frontend:**
    - Single-page application (SPA) served from Spring Boot static resources
    - Dynamic forms with dropdowns for selecting IDs
    - Actions for creating, updating, completing, and deleting records
    - Dashboard with live metrics
    - Responsive and user-friendly UI (HTML/CSS/JS)
- **Other:**
    - Timezone handling (UTC)
    - Schema auto-update for easy deployment

## Getting Started
1. **Build & Run:**
     - `./gradlew clean build`
     - `./gradlew bootRun`
2. **Access the App:**
     - Open [http://localhost:8080](http://localhost:8080) in your browser

By default the application starts with an in-memory H2 database so `bootRun` works without a local PostgreSQL server. To use PostgreSQL instead, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` before starting the app.

## Project Structure
- `src/main/java/com/fleet/` — Backend source code
- `src/main/resources/static/` — Frontend (SPA)
- `src/test/java/com/fleet/` — Tests

## License
MIT
# Fleet Management System

## Overview
This is a Spring Boot based Fleet Management backend for managing:
- Vehicles
- Drivers
- Assignments
- Trips
- Maintenance records
- Expenses
- Reports

The project uses layered architecture with Controller, Service, Repository, DTO, Mapper, Model, Config, and Exception packages.

## Tech Stack
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- MapStruct
- Lombok
- Gradle

## Project Structure
Main source folder:
- src/main/java/com/fleet

Key package responsibilities:
- config: application and framework configuration
- controller: REST API endpoints
- dto: request and response contracts
- exception: centralized exception handling and custom exceptions
- mapper: DTO <-> Entity mapping
- model: domain entities and enums
- repository: persistence contracts
- service: business contracts
- service/impl: business implementations

## SOLID and GRASP in This Codebase
You asked for explicit design comments. The code now includes comments where principles or patterns are used.

How to read those comments:
- SOLID comments explain why a block follows SRP, OCP, DIP, or ISP style boundaries
- GRASP comments explain controller, indirection, information expert, low coupling, and cohesion decisions
- Pattern comments are written immediately above the usage block in code

### SOLID Principles Used (Where + Which)
1. SRP (Single Responsibility Principle)
- Used across layer classes with focused responsibilities:
    - Controllers: src/main/java/com/fleet/controller/*.java
    - Services: src/main/java/com/fleet/service/impl/*.java
    - Repositories: src/main/java/com/fleet/repository/*.java
    - DTOs, Mappers, Models, Config, Exception classes
- Why used:
    - Makes each class easier to understand, test, and modify.
    - Reduces side effects when one concern changes.

2. DIP (Dependency Inversion Principle)
- src/main/java/com/fleet/service/impl/TripServiceImpl.java
    - Method: resolveVehicleViaProxy(...)
    - Usage: service flow relies on indirection point instead of repeated direct lookup logic
- src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java
    - Method: createExpenseViaFactory(...)
    - Usage: high-level expense flow depends on factory abstraction for creation
- Why used:
    - Keeps high-level business logic independent from low-level construction and lookup details.
    - Makes replacing internals safer without touching use-case flow.

3. OCP (Open/Closed Principle)
- src/main/java/com/fleet/service/impl/TripServiceImpl.java
    - Method/Area: TripCompletionFacade.complete(...) and AbstractCompletionCheck
    - Usage: new completion validations can be added as new CompletionCheck classes without changing existing flow
- src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java
    - Method/Area: ExpenseFactory and PrototypeExpenseFactory
    - Usage: new expense-creation strategies can be added via new factory implementations
- src/main/java/com/fleet/service/impl/ReportServiceImpl.java
    - Method/Area: interpretGrouping(...) and GroupingUnit
    - Usage: new report grouping modes can be added with new enum values and mapping
- Why used:
    - Supports future features by extension instead of risky edits to stable logic.
    - Improves maintainability for evolving business rules.

4. ISP (Interface Segregation Principle)
- Service contracts split by capability:
    - src/main/java/com/fleet/service/VehicleService.java
    - src/main/java/com/fleet/service/DriverService.java
    - src/main/java/com/fleet/service/AssignmentService.java
    - src/main/java/com/fleet/service/MaintenanceService.java
    - src/main/java/com/fleet/service/ExpenseService.java
    - src/main/java/com/fleet/service/TripService.java
    - src/main/java/com/fleet/service/ReportService.java
- Why used:
    - Consumers depend only on methods they need.
    - Prevents bloated interfaces and unnecessary coupling.

### GRASP Principles Used (Where + Which)
1. Controller
- REST request coordination in:
    - src/main/java/com/fleet/controller/*.java
- Use-case orchestration in:
    - src/main/java/com/fleet/service/impl/*.java
- Why used:
    - Centralizes request workflow and keeps domain logic out of transport layer code.

2. Information Expert
- src/main/java/com/fleet/service/impl/ReportServiceImpl.java
    - Report aggregation and period logic is placed where report knowledge exists
- src/main/java/com/fleet/model/*.java
    - Domain entities store and manage their own domain data
- Why used:
    - Places behavior near the data/rules it depends on.
    - Reduces duplicated logic and improves correctness.

3. Low Coupling / Indirection
- src/main/java/com/fleet/repository/*.java
    - repositories isolate persistence access
- src/main/java/com/fleet/service/*.java
    - interfaces separate callers from implementations
- src/main/java/com/fleet/service/impl/TripServiceImpl.java
    - proxy/facade helpers reduce direct coupling in completion flow
- Why used:
    - Limits ripple effects when internals change.
    - Keeps component boundaries clean and replaceable.

4. High Cohesion
- Package-level role separation keeps each file focused:
    - config, controller, dto, exception, mapper, model, repository, service
- Why used:
    - Improves readability and maintainability by grouping related responsibilities.

5. Pure Fabrication
- src/main/java/com/fleet/mapper/*.java
    - mapping logic extracted from entities/services
- src/main/java/com/fleet/dto/*.java
    - transport/contract classes separated from domain entities
- Why used:
    - Avoids overloading domain entities with infrastructure concerns.
    - Keeps business model clean and reusable.

## Patterns Used and Where
The implementation intentionally uses a focused subset in existing files (no extra files were created for pattern scaffolding).

### Creational Patterns
1. Singleton
- File: src/main/java/com/fleet/service/impl/TripServiceImpl.java
- Usage: FleetClockSingleton for a shared time provider in trip completion
- Why used:
    - Ensures one consistent time source in the completion flow.
    - Avoids repeated ad-hoc time provider creation.

2. Factory
- File: src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java
- Usage: ExpenseFactory and PrototypeExpenseFactory to create expense objects
- Why used:
    - Centralizes object creation rules in one place.
    - Makes future creation strategy changes easier.

3. Builder
- File: src/main/java/com/fleet/service/impl/ReportServiceImpl.java
- Usage: ReportQuery.Builder style construction for report query context
- Why used:
    - Makes complex parameter construction readable and less error-prone.
    - Keeps method calls clean when optional filters grow.

4. Prototype
- File: src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java
- Usage: ExpenseTemplateRegistry clones template expense objects by ExpenseType
- Why used:
    - Reuses default templates and reduces repetitive initialization code.
    - Supports consistent defaults for new expense instances.

### Structural Patterns
1. Adapter
- File: src/main/java/com/fleet/service/impl/ReportServiceImpl.java
- Usage: adaptTripDistance converts trip odometer and direct-distance variations into one distance value
- Why used:
    - Normalizes multiple trip distance representations behind one usage path.
    - Keeps aggregation logic simple and robust.

2. Facade
- File: src/main/java/com/fleet/service/impl/TripServiceImpl.java
- Usage: TripCompletionFacade orchestrates completion validation and command execution
- Why used:
    - Hides multi-step workflow complexity behind one clear operation.
    - Improves readability of service-level use-case logic.

3. Proxy
- Files:
  - src/main/java/com/fleet/service/impl/TripServiceImpl.java
  - src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java
- Usage: controlled lookup wrappers before repository access
- Why used:
        - Adds a controlled access layer for critical lookups.
        - Helps keep validation and lookup behavior consistent.

4. Flyweight
- File: src/main/java/com/fleet/service/impl/ReportServiceImpl.java
- Usage: periodFlyweightPool reuses canonical period label instances
- Why used:
        - Reuses repeated period label objects during aggregation.
        - Reduces duplicated in-memory strings for common keys.

### Behavioral Patterns
1. Chain of Responsibility
- File: src/main/java/com/fleet/service/impl/TripServiceImpl.java
- Usage: completion validation chain (date check, odometer checks)
- Why used:
    - Breaks validation into small composable checks.
    - Makes it easy to add/remove rules without rewriting whole validation flow.

2. Command
- File: src/main/java/com/fleet/service/impl/TripServiceImpl.java
- Usage: CompleteTripCommand encapsulates trip completion write operation
- Why used:
    - Encapsulates write action as a dedicated executable unit.
    - Separates action intent from invocation details.

3. Interpreter
- File: src/main/java/com/fleet/service/impl/ReportServiceImpl.java
- Usage: interpretGrouping resolves groupBy expression into GroupingUnit
- Why used:
    - Converts textual grouping input into controlled internal semantics.
    - Prevents scattering parsing logic across report methods.

4. Iterator
- File: src/main/java/com/fleet/service/impl/ReportServiceImpl.java
- Usage: ReportCollection and ReportIterator for explicit report iteration
- Why used:
    - Provides a uniform way to traverse report results.
    - Keeps traversal logic encapsulated from calling code.

## How to Run the Project
## 1. Prerequisites
Install and verify:
- Java 21
- Gradle
- PostgreSQL

Create a PostgreSQL database and update credentials in:
- src/main/resources/application.yml

Current properties to update:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password

## 2. Build
From project root (fleet-management):

    gradle clean build

## 3. Run
From project root:

    gradle bootRun

Application default URL:
- http://localhost:8080

## 4. Run Tests

    gradle test

## API Notes
Main controllers are in:
- src/main/java/com/fleet/controller

Common endpoint groups:
- /api/vehicles
- /api/drivers
- /api/assignments
- /api/maintenance
- /api/expenses
- /api/trips
- /api/reports

## Important Notes
- This project currently uses Spring Boot 4.0.2 with springfox dependency in build.gradle.
- If Swagger-related startup issues appear, align OpenAPI/Swagger dependencies with your Spring Boot version.
- Security config currently permits all requests by default in SecurityConfig.

## Quick Read Path for New Developers
Read in this order:
1. src/main/java/com/fleet/FleetManagementApplication.java
2. src/main/java/com/fleet/controller
3. src/main/java/com/fleet/service
4. src/main/java/com/fleet/service/impl
5. src/main/java/com/fleet/repository
6. src/main/java/com/fleet/model
7. src/main/java/com/fleet/dto and src/main/java/com/fleet/mapper

This path helps you understand request flow from API to business logic to persistence.
