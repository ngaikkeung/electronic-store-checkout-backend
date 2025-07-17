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
        C[(Redis)]
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
    title Customer add product to Basket 

    actor Customer
    participant BasketController
    participant BasketService
    participant StockService
    participant RedisCache
    participant Database

    Customer->>BasketController: POST /api/basket/items (productId, quantity)
    activate BasketController

    BasketController->>BasketService: addItemToBasket(userId, ...)
    activate BasketService

    %% 1. Reserve stock in Redis first to handle concurrency
    BasketService->>StockService: reserveStock(productId, quantity)
    activate StockService
    StockService->>RedisCache: DECRBY stock:product:123 quantity
    RedisCache-->>StockService: newStockCount
    
    alt newStockCount is less than 0
        StockService->>RedisCache: INCRBY stock:product:123 quantity
        StockService-->>BasketService: Return Error (Out of Stock)
    else Stock Reserved Successfully
        StockService-->>BasketService: Return Success
    end
    deactivate StockService

    %% 2. If stock was reserved, manage the basket state
    opt Stock Reserved Successfully
        %% Try to get the current basket from the cache
        BasketService->>RedisCache: GET basket:userId
        RedisCache-->>BasketService: Cached Basket (or nil)

        alt Cache Miss (Basket not in Redis)
            BasketService->>Database: Get Basket by User ID
            Database-->>BasketService: Basket Data
        end
        
        note over BasketService: Add new item to basket object in memory

        %% 3. Upsert the entire updated basket back into Redis
        BasketService->>RedisCache: SET basket:userId (updatedBasket)
        RedisCache-->>BasketService: OK

        %% 4. Persist all changes to the primary database
        BasketService->>Database: BEGIN TRANSACTION
        activate Database
        BasketService->>Database: Upsert Basket & Items
        BasketService->>StockService: persistStockUpdate(productId, -quantity)
        activate StockService
        StockService->>Database: UPDATE Product Stock
        deactivate StockService
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
    title Customer remove product from Basket

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

    %% 1. Get basket from cache or DB
    BasketService->>RedisCache: GET basket:userId
    RedisCache-->>BasketService: Cached Basket (or nil)
    
    alt Cache Miss
        BasketService->>Database: Get Basket by User ID
        Database-->>BasketService: Basket Data
    end

    note over BasketService: Remove item from basket object & get details

    %% 2. Upsert updated basket back into Redis
    BasketService->>RedisCache: SET basket:userId (updatedBasket)

    %% 3. Persist changes to the database
    BasketService->>Database: BEGIN TRANSACTION
    activate Database
    BasketService->>Database: Remove Item from Basket
    BasketService->>StockService: persistStockUpdate(productId, +quantity)
    activate StockService
    StockService->>Database: UPDATE Product Stock
    deactivate StockService
    BasketService->>Database: COMMIT TRANSACTION
    deactivate Database

    %% 4. Sync stock back to Redis cache via StockService
    BasketService->>StockService: syncStockToCache(productId, +quantity)
    activate StockService
    StockService->>RedisCache: INCRBY stock:product:123 quantity
    deactivate StockService

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
        +addDiscount(details)
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
    }

    class Discount {
        +String discountId
        +String description
        +String discountType
        +Map(String, Object) rules
        +Date expirationDate
    }

    class PricingService {
        +calculateDiscounts(basket)
    }

    class DiscountStrategy {
        <<interface>>
        +apply(basket, rules) double
    }

    class PercentageDiscountStrategy {
        +apply(basket, rules) double
    }

    class BogoDealStrategy {
        +apply(basket, rules) double
    }

    class FixedAmountOffStrategy {
        +apply(basket, rules) double
    }

    class SpendThresholdStrategy {
        +apply(basket, rules) double
    }
    
    class BundlePriceStrategy {
        +apply(basket, rules) double
    }

    class Basket {
        -List(BasketItem) items
        +addItem(item)
    }

    class Order {
        +String orderId
        +double totalPrice
    }

    %% --- Relationships ---
    User <|-- Admin
    User <|-- Customer
    
    Customer "1" -- "1" Basket : has
    Basket "1" *-- "*" Product : contains
    
    %% A discount can optionally apply to one product
    Discount "0..*" -- "0..1" Product : applies to
    Customer ..> Order : creates

    %% Admin manages products and discounts
    Admin ..> Product : creates/removes
    Admin ..> Discount : creates

    %% Strategy Pattern for Discounts
    PricingService ..> DiscountStrategy : uses
    PricingService ..> Discount : reads
    DiscountStrategy <|.. PercentageDiscountStrategy : implements
    DiscountStrategy <|.. BogoDealStrategy : implements
    DiscountStrategy <|.. FixedAmountOffStrategy : implements
    DiscountStrategy <|.. SpendThresholdStrategy : implements
    DiscountStrategy <|.. BundlePriceStrategy : implements
``` 

ER Diagram
```mermaid
erDiagram
    USERS {
        user_id bigint PK
        name varchar
        email varchar UK
        created_at datetime
    }

    ROLES {
        role_id int PK
        role_name varchar UK
    }

    PERMISSIONS {
        permission_id int PK
        permission_name varchar UK
    }

    PRODUCTS {
        product_id bigint PK
        name varchar
        price decimal
        stock int
        category varchar
    }

    DISCOUNTS {
        discount_id bigint PK
        product_id bigint FK "nullable"
        description varchar
        varchar discount_type "e.g., 'PERCENTAGE', 'BOGO_50_PERCENT_OFF_SECOND'"
        json rules "e.g., {'percentage': 15} or {'buy_quantity': 1, 'get_quantity': 1, 'discount_percentage': 50}"
        expiration_date datetime
    }

    ORDERS {
        order_id bigint PK
        user_id bigint FK
        status varchar
        total_price decimal
        created_at datetime
    }

    ORDER_ITEMS {
        order_item_id bigint PK
        order_id bigint FK
        product_id bigint FK
        quantity int
        price_at_purchase decimal
    }

    %% --- Join Tables for Many-to-Many Relationships ---
    USER_ROLES {
        user_id bigint FK
        role_id int FK
    }

    ROLE_PERMISSIONS {
        role_id int FK
        permission_id int FK
    }

    %% --- Relationships ---
    USERS ||--o{ USER_ROLES : "has"
    ROLES ||--o{ USER_ROLES : "has"
    ROLES ||--o{ ROLE_PERMISSIONS : "grants"
    PERMISSIONS ||--o{ ROLE_PERMISSIONS : "is granted by"
    USERS ||--o{ ORDERS : "places"
    ORDERS ||--o{ ORDER_ITEMS : "contains"
    PRODUCTS ||--o{ ORDER_ITEMS : "appears in"
    PRODUCTS ||--o{ DISCOUNTS : "can have"
```