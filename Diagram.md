# System Diagrams

This page contains the system diagrams for the Electronic Store Checkout Backend. The diagrams are sourced from the files in the `diagram/` folder.

## High Level Design

**Source:** `diagram/high_level_design.mmd`

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

## Sequence Diagram

**Source:** `diagram/sequence_diagram.mmd`

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

## Class Diagram

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