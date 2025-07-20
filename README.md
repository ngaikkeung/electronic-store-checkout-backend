# Electronic Store Checkout Backend

A Spring Boot backend application designed to handle electronic store checkout functionality, including product management, basket operations, order processing,
and discount application.

## Features

- **Product Management**: RESTful API for creating, retrieving, updating, and deleting products.
- **Basket Operations**: Functionality to add/remove items from a user's shopping basket.
- **Order Processing**: Create orders from baskets and retrieve order details, including receipts with applied discounts.
- **Discount System**: Flexible discount application using a strategy pattern (e.g., Percentage, BOGO).
- **User & Role Management**: Basic user and role models for access control.
- **Data Persistence**: JPA/Hibernate for robust data storage.
- **In-memory Database**: H2 in-memory database for easy development and testing.
- **Redis Caching**: Utilizes Redis for caching basket data to improve performance.
- **Lombok**: Reduces boilerplate code for cleaner and more concise Java classes.
- **Maven**: Standardized build automation and dependency management.

## Technology Stack

- **Java**: OpenJDK 17
- **Spring Boot**: 3.5.3 (Web, Data JPA, Redis, Validation)
- **Spring Data JPA**: For repository layer abstraction.
- **H2 Database**: Embedded in-memory database for development and testing.
- **Redis**: For caching and session management (if applicable).
- **Lombok**: For reducing boilerplate code.
- **Maven**: Build automation tool.
- **JUnit 5 & Mockito**: For unit and integration testing.

## Architecture & Design Decisions

- **Layered Architecture**: Follows a traditional layered architecture (Controller -> Service -> Repository) for clear separation of concerns.
- **RESTful API**: Designed with REST principles for clear, stateless communication.
- **Discount Strategy Pattern**: Implemented a strategy pattern for discounts (`DiscountStrategy` interface and concrete implementations
  like `PercentageDiscountStrategy`, `BogoDealStrategy`). This allows for easy extension with new discount types without modifying existing code.
- **Unified Response**: Uses a `UnifiedResponse` DTO for consistent API response structure (code, message, data, success status).
- **Global Exception Handling**: Centralized exception handling using `@RestControllerAdvice` to provide consistent error responses.
- **H2 Database**: Chosen for simplicity and ease of setup during development and testing. For production, a more robust database like PostgreSQL or MySQL would
  be used.
- **Lombok**: Used to reduce boilerplate code (getters, setters, constructors) and improve readability.

## Project Structure

```
src/
├── main/
│   ├── java/io/github/kkngai/estorecheckout/
│   │   ├── EstorecheckoutApplication.java
│   │   ├── config/             # Spring configurations (e.g., RedisConfig)
│   │   ├── controller/         # REST API endpoints (admin and public)
│   │   ├── dto/                # Data Transfer Objects (requests, responses)
│   │   ├── exception/          # Custom exceptions and global handler
│   │   ├── model/              # JPA Entities (database models)
│   │   ├── repository/         # Spring Data JPA repositories
│   │   └── service/            # Business logic and service implementations
│   │       └── discount/       # Discount strategy implementations
│   └── resources/
│       ├── application.yaml    # Application properties and configurations
│       └── data.sql            # Initial data for H2 database
└── test/
    └── java/io/github/kkngai/estorecheckout/
        ├── controller/         # Unit tests for controllers
        ├── service/            # Unit tests for services
        └── EstorecheckoutApplicationTests.java # Main application test
```

## Design Diagrams

For system diagrams (API Design, Class Diagram, ER Diagram, Sequence Diagrams), please refer to [Design Diagram](DesignDiagram.md) and the `degisn/diagram` directory.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose

### Running the Application

You can run the application using Maven or Docker Compose.

#### Using Maven

1. Clone the repository:
   ```bash
   git clone https://github.com/kkngai/electronic-store-checkout-backend.git
   cd electronic-store-checkout-backend
   ```
2. Build the project:
   ```bash
   ./mvnw clean install
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

#### Using Docker Compose

1. Ensure Docker and Docker Compose are installed and running.
2. Clone the repository.
3. Navigate to the project root directory.
4. Build and run the Docker containers:
   ```bash
   docker-compose up --build
   ```
   The backend application will be accessible at `http://localhost:8080`. Redis will be accessible at `http://localhost:6379`.

### API Endpoints

For a detailed list of API endpoints and their specifications, please refer to the OpenAPI documentation located at `api_document/openapi.yaml` and
the [API Design Document](degisn/diagram/api_design.md).

Key endpoints include:

- `/api/products` (GET: list products, GET /{id}: get product by ID)
- `/api/basket` (GET: get basket, POST /items: add item, DELETE /items/{itemId}: remove item)
- `/api/orders` (POST: create order from basket, GET /{orderId}/receipt: get order receipt)
- `/api/admin/products` (POST: create, PUT /{id}: update, DELETE /{id}: delete products)
- `/api/admin/discounts` (POST: create discounts)

## Building the Application

To build the JAR file:

```bash
./mvnw clean package
```

The executable JAR will be located in the `target/` directory.

## Running Tests

To run all unit and integration tests:

```bash
./mvnw clean test
```