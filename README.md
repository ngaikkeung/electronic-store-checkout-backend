# Electronic Store Checkout Backend

A Spring Boot backend application for electronic store checkout functionality.

## Features

- RESTful API for product management
- JPA/Hibernate for data persistence
- H2 in-memory database for development
- Lombok for reducing boilerplate code
- Maven build system

## Technology Stack

- Java 21
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
│   │   ├── controller/
│   │   │   └── ProductController.java
│   │   ├── service/
│   │   │   └── ProductService.java
│   │   ├── repository/
│   │   │   └── ProductRepository.java
│   │   └── model/
│   │       └── Product.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/io/github/kkngai/estorecheckout/
        └── EstorecheckoutApplicationTests.java
```

## Diagrams

This project includes two diagrams located in the `diagram/` folder:

- **High Level Design**: 

```mermaid
flowchart TD
    %% === Frontend ===
    A[Frontend]

    %% === Backend Components ===
    subgraph Backend
        B[Web Server]
        C[(Cache)]
        D[(Database)]
        E{{Kafka Message Queue}}
    end

    %% === Connections ===
    A -->|REST API| B
    B -->|Read & Write| C
    B -->|Query & Update| D
    B -->|Publish & Consume| E
    E -->|Event Notification| B
    C -->|Cache Refresh| D
```

- **Sequence Diagram**: 

```mermaid
sequenceDiagram
    participant F as Frontend
    participant W as Web Server
    participant C as Cache
    participant D as Database
    participant K as Kafka (Message Queue)

    F->>W: REST API
    W->>C: Read from Cache
    alt Cache Miss
        C->>D: Fetch from Database
        D-->>C: Return Data
        C-->>W: Cached Response
    else Cache Hit
        C-->>W: Return Cached Data
    end
    W->>K: Publish Event
    K-->>W: Event Notification
    W-->>F: Response to Frontend

```

- **Class Diagram**:

```mermaid
classDiagram
    class User {
        +String userId
        +String name
        +String email
    }

    class Admin {
        +createProduct(productDetails)
        +removeProduct(productId)
        +addDiscount(productId, discountDetails)
    }

    class Customer {
        +addProductToBasket(product, quantity)
        +removeProductFromBasket(product)
        +checkout() Order
    }

    class Product {
        +String productId
        +String name
        +double price
        +int stock
        +String category
    }

    class Discount {
        +String discountId
        +double percentage
        +Date expirationDate
    }

    class Basket {
        +String basketId
        -List~BasketItem~ items
        +calculateTotal() double
    }

    class BasketItem {
        +Product product
        +int quantity
    }
    
    class Order {
        +String orderId
        +List~Product~ purchasedItems
        +List~Discount~ appliedDeals
        +double totalPrice
    }

    %% --- Relationships ---
    User <|-- Admin
    User <|-- Customer
    
    Customer "1" -- "1" Basket : has
    Basket "1" *-- "*" BasketItem : contains
    BasketItem "1" --> "1" Product
    Product "1" -- "0..*" Discount : has
    Customer ..> Order : creates
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### API Endpoints

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update a product
- `DELETE /api/products/{id}` - Delete a product

### H2 Database Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Building the Application

```bash
./mvnw clean package
```

## Running Tests

```bash
./mvnw test
``` 