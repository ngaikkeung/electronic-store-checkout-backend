# Electronic Store Checkout Backend

A Spring Boot backend application for electronic store checkout functionality.

## Features

- RESTful API for product management
- JPA/Hibernate for data persistence
- H2 in-memory database for development
- Lombok for reducing boilerplate code
- Maven build system

## Technology Stack

- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Project Structure

```
src/
├── main/
│   ├── java/io/github/kkngai/estorecheckout/
│   │   ├── EstorecheckoutApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       ├── application.yaml
│       └── data.sql
└── test/
    └── java/io/github/kkngai/estorecheckout/
        ├── service/
        └── EstorecheckoutApplicationTests.java
```

## Diagrams

For system diagrams, see [Diagram.md](Diagram.md).

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

You can run the application using Maven or Docker.

#### Using Maven

1. Clone the repository.
2. Navigate to the project directory.
3. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

#### Using Docker

1. Ensure Docker and Docker Compose are installed.
2. Clone the repository.
3. Navigate to the project directory.
4. Build and run the Docker containers:

```bash
docker-compose up --build
```

The application will be accessible at `http://localhost:8080` (or the port configured in `docker-compose.yml`).


### API Endpoints

For a detailed list of API endpoints, please refer to the [API Design Document](degisn/diagram/api_design.md).

## Building the Application

```bash
./mvnw clean package
```

## Running Tests

```bash
./mvnw test
``` 