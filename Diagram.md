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

```mermaid
sequenceDiagram
    title Admin Creates a New Discount Deal

    actor Admin
    participant DiscountController
    participant DiscountService
    participant Database

    Admin->>DiscountController: POST /api/admin/discounts (discountDetails, productId)
    activate DiscountController

    DiscountController->>DiscountService: createDiscountForProduct(discountDetails, productId)
    activate DiscountService

    DiscountService->>Database: Find Product by productId
    activate Database
    Database-->>DiscountService: Return Product
    deactivate Database

    DiscountService->>Database: Save New Discount
    activate Database
    Database-->>DiscountService: Return Saved Discount w/ ID
    deactivate Database

    DiscountService->>Database: Link Discount to Product
    activate Database
    Database-->>DiscountService: Link Confirmation
    deactivate Database

    DiscountService-->>DiscountController: Return Success Confirmation
    deactivate DiscountService

    DiscountController-->>Admin: 201 Created Response
    deactivate DiscountController
```

```mermaid
sequenceDiagram
    title Admin Creates a New Product

    actor Admin
    participant ProductController
    participant ProductService
    participant Database

    Admin->>ProductController: POST /api/admin/products (productDetails)
    activate ProductController

    ProductController->>ProductService: createProduct(productDetails)
    activate ProductService

    ProductService->>Database: Save New Product
    activate Database
    Database-->>ProductService: Return Saved Product w/ ID
    deactivate Database

    ProductService-->>ProductController: Return Created Product
    deactivate ProductService

    ProductController-->>Admin: 201 Created Response
    deactivate ProductController
```

```mermaid
sequenceDiagram
    title User add product to basket

    actor Customer
    participant BasketController
    participant BasketService
    participant RedisCache
    participant Database

    Customer->>BasketController: POST /api/basket/items (productId, quantity)
    activate BasketController

    BasketController->>BasketService: addItemToBasket(userId, ...)
    activate BasketService

    %% First, atomically decrease stock in Redis
    BasketService->>RedisCache: DECRBY stock:product:123 quantity
    activate RedisCache
    RedisCache-->>BasketService: Return newStockCount
    deactivate RedisCache

    alt newStockCount is less than 0 (Race condition lost)
        %% We took too many, so we must put them back
        BasketService->>RedisCache: INCRBY stock:product:123 quantity
        RedisCache-->>BasketService: OK
        BasketService-->>BasketController: Return Error (Out of Stock)
    else Stock Reserved Successfully
        %% Now that stock is reserved in Redis, update the persistent database
        BasketService->>Database: BEGIN TRANSACTION
        activate Database
        BasketService->>Database: Add Item to User's Basket
        BasketService->>Database: UPDATE Product Stock (Sync)
        BasketService->>Database: COMMIT TRANSACTION
        deactivate Database

        BasketService-->>BasketController: Return Success
    end
    deactivate BasketService

    BasketController-->>Customer: 200 OK or Error Response
    deactivate BasketController
```

```mermaid
sequenceDiagram
    title User remove product from basket

    actor Customer
    participant BasketController
    participant BasketService
    participant StockService
    participant RedisCache
    participant Database

    Customer->>BasketController: DELETE /api/basket/items/{itemId}
    activate BasketController

    BasketController->>BasketService: removeItemFromBasket(userId, itemId)
    activate BasketService

    note over BasketService: Get productId & quantity from removed item

    %% The database operations are wrapped in a transaction
    BasketService->>Database: BEGIN TRANSACTION
    activate Database

    BasketService->>Database: Remove Item from Basket
    Database-->>BasketService: OK

    %% Delegate the stock update to a dedicated service
    BasketService->>StockService: increaseStock(productId, quantity)
    activate StockService
    StockService->>Database: UPDATE Product Stock (+quantity)
    Database-->>StockService: OK
    deactivate StockService
    
    BasketService->>Database: COMMIT TRANSACTION
    deactivate Database

    %% After the DB is successfully updated, sync the change to Redis
    BasketService->>RedisCache: INCRBY stock:product:123 quantity
    activate RedisCache
    RedisCache-->>BasketService: OK
    deactivate RedisCache

    BasketService-->>BasketController: Return Success
    deactivate BasketService

    BasketController-->>Customer: 200 OK (Item Removed)
    deactivate BasketController
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

    Admin ..> Product : creates/removes
    Admin ..> Discount : creates
``` 