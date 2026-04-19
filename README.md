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

## SOLID and GRASP Principles in This Codebase

### SOLID Principles Used (Where + Which)

#### 1. SRP (Single Responsibility Principle)
- Each service class has one reason to change
- Used across: TripServiceImpl, ExpenseServiceImpl, ReportServiceImpl
- **createTrip()** (TripServiceImpl): Creates trips and ensures assignment validation
- **completeTrip()** (TripServiceImpl): Completes trips separately from creation
- **createExpense()** (ExpenseServiceImpl): Only creates expenses
- **updateExpense()** (ExpenseServiceImpl): Update logic stays separate from creation
- **getVehicleUsage()** (ReportServiceImpl): Aggregates report data
- Why used: Makes each method focused; changes to trip creation don't affect completion logic

#### 2. OCP (Open/Closed Principle)
- **interpretGrouping()** (ReportServiceImpl): New grouping modes (WEEK, QUARTER) can be added via new switch case without modifying callers
- **updateExpense()** (ExpenseServiceImpl): Only updates provided fields; extensible for future expense types
- **updateTrip()** (TripServiceImpl): Selective field updates support evolving trip requirements
- Why used: Supports future features by extension instead of risky edits to stable logic

#### 3. DIP (Dependency Inversion Principle)
- All services depend on abstractions (interfaces), not concrete implementations
- **TripServiceImpl**: Depends on TripRepository interface, VehicleRepository interface, AssignmentRepository interface
- **ExpenseServiceImpl**: Depends on ExpenseRepository interface, VehicleRepository interface
- **ReportServiceImpl**: Depends on TripRepository interface, VehicleRepository interface
- Why used: High-level business logic is independent from low-level persistence details

#### 4. ISP (Interface Segregation Principle)
- Service contracts split by capability
- **TripService**: Focused on trip operations
- **ExpenseService**: Focused on expense operations
- **ReportService**: Focused on reporting operations
- Why used: Consumers depend only on methods they need, preventing bloated interfaces

#### 5. LSP (Liskov Substitution Principle)
- DTOs and Mappers maintain substitutability
- **TripMapper**, **ExpenseMapper** implementations honor DTO contracts
- Why used: Ensures type safety and predictable behavior across layers

### GRASP Principles Used (Where + Which)

#### 1. Controller (Request Coordinator)
- Controllers in `src/main/java/com/fleet/controller/` coordinate REST requests
- **TripController.completeTrip()**: Routes request to TripService
- Why used: Separates transport/HTTP concerns from business logic

#### 2. Information Expert
- **ReportServiceImpl.getVehicleUsage()**: Report aggregation stays here because report knowledge belongs here
- **TripServiceImpl**: Trip-related business rules are kept here
- **ExpenseServiceImpl**: Expense validation and creation logic stays here
- Why used: Behavior is placed near data/rules it depends on; reduces logic duplication

#### 3. Low Coupling / Indirection
- **validateAndFetchTrip()** (TripServiceImpl, PROXY): Controlled access wrapper for trip lookup
- **resolveVehicleViaProxy()** (ExpenseServiceImpl, PROXY): Validates vehicle before operations
- **adaptTripDistance()** (ReportServiceImpl, ADAPTER): Indirection layer for distance calculation
- Why used: Limits ripple effects when internals change; keeps boundaries clean

#### 4. High Cohesion
- Package structure organizes related functionality:
  - `config`: Application configuration
  - `controller`: REST endpoints
  - `service`: Business contracts
  - `service/impl`: Business implementations
  - `repository`: Data access
  - `model`: Domain entities
  - `dto`: Data transfer objects
  - `mapper`: DTO ↔ Entity mapping
  - `exception`: Error handling
- Why used: Improves readability; related concerns live together

#### 5. Pure Fabrication
- **Mapper classes** (ExpenseMapper, TripMapper): Mapping logic extracted from entities/services
- **DTO classes**: Transport/contract classes separated from domain entities
- Why used: Avoids overloading domain entities with infrastructure concerns

---

## Design Patterns Used and Where

### Creational Patterns

#### 1. Factory Pattern
- **File**: `src/main/java/com/fleet/service/impl/ExpenseServiceImpl.java`
- **Method**: `createExpenseViaFactory(CreateExpenseRequest request)`
- **Usage**: Encapsulates expense creation logic; called by `createExpense()`
- **Why**: Centralizes object creation; future expense-type strategies (fuel, maintenance, insurance) can have different defaults without changing callers
- **Code Comment**: "FACTORY PATTERN: Encapsulates expense creation logic in one place."

### Structural Patterns

#### 1. Proxy Pattern
- **Files**: 
  - `TripServiceImpl.validateAndFetchTrip(String tripId)` — private controlled access wrapper
  - `ExpenseServiceImpl.resolveVehicleViaProxy(String vehicleId)` — vehicle validation before operations
- **Usage**: Acts as a controlled access layer before repository/resource access
- **Why**: Adds consistent validation and error handling; ensures lookups always validate before use
- **Code Comment**: "PROXY PATTERN (Private Controlled Access): Wraps repository access with validation logic."

#### 2. Adapter Pattern
- **File**: `src/main/java/com/fleet/service/impl/ReportServiceImpl.java`
- **Method**: `adaptTripDistance(Trip trip)`
- **Usage**: Normalizes different distance representations (explicit distance vs. odometer difference) into one standard BigDecimal
- **Why**: Trip distance may come from different sources; adapter unifies them for aggregation logic
- **Code Comment**: "ADAPTER PATTERN: Adapts different distance representations into one standard BigDecimal."

#### 3. Facade Pattern
- **File**: `src/main/java/com/fleet/service/impl/TripServiceImpl.java`
- **Method**: `completeTrip(String tripId, String endLocation, Long endOdometer)`
- **Usage**: Orchestrates multi-step trip completion workflow (validation, distance calculation, persistence)
- **Why**: Hides complexity of trip completion behind one simple, coherent operation
- **Code Comment**: "FACADE PATTERN: completeTrip orchestrates multi-step trip completion workflow."

### Behavioral Patterns

#### 1. Chain of Responsibility Pattern
- **File**: `src/main/java/com/fleet/service/impl/TripServiceImpl.java`
- **Method**: `validateTripCompletionData(String endLocation, Long endOdometer)`
- **Usage**: Sequential validation checks before trip completion
- **Why**: Each validation rule is independent; rules can be added/removed without changing other rules
- **Code Comment**: "CHAIN OF RESPONSIBILITY PATTERN: Sequential validation checks before trip completion."

#### 2. Interpreter Pattern
- **File**: `src/main/java/com/fleet/service/impl/ReportServiceImpl.java`
- **Method**: `interpretGrouping(Trip trip, String groupBy)`
- **Usage**: Converts grouping expression (DAY/MONTH/YEAR) into concrete period strings
- **Why**: Encapsulates parsing/interpretation logic; new grouping modes (WEEK, QUARTER) can be added via new switch case
- **Code Comment**: "INTERPRETER PATTERN: Interprets grouping expression into concrete period string."

#### 3. Iterator Pattern
- **File**: `src/main/java/com/fleet/service/impl/ReportServiceImpl.java`
- **Method**: `getVehicleUsage()` — uses standard for-loop traversal
- **Usage**: Provides uniform way to iterate over trips during aggregation
- **Why**: Stream API provides standard traversal without exposing internal structure
- **Code Comment**: "ITERATOR PATTERN (Implicit): StreamAPI provides standard traversal over trips"

---

### Summary: How These Patterns and Principles Work Together

1. **SRP + INFORMATION EXPERT**: Each service (TripServiceImpl, ExpenseServiceImpl, ReportServiceImpl) has one primary responsibility
2. **DIP + ISP**: High-level logic depends on abstractions; callers use only needed methods
3. **OCP + Interpreter/Adapter**: New features (grouping modes, distance sources) extend without modifying existing code
4. **GRASP Low Coupling**: Proxy and Adapter patterns reduce direct dependencies
5. **GRASP High Cohesion**: Related logic stays together in focused methods

This architecture makes the codebase:
- **Testable**: Each piece can be tested in isolation
- **Maintainable**: Changes are localized to responsible classes
- **Extensible**: New features fit patterns already established
- **Clear**: Intent is explicit via comments and pattern structure

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
