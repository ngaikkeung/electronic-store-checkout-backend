# E-Store API Design

This document outlines the RESTful API endpoints for the E-Store Checkout System.

---

## Authentication

All requests must include a valid JSON Web Token (JWT) in the `Authorization` header.

**Format:**

```
Authorization: Bearer <your_jwt>
```

---

## Pagination (Spring Data)

Endpoints that return lists are compatible with Spring Data's `Pageable` object. You can control pagination using the following query parameters:

- `page`: The page number to retrieve (0-indexed, default is 0).
- `size`: The number of items per page (default is 20).
- `sort`: A comma-separated list of properties to sort by, e.g., `name,asc`.

---

## Admin Endpoints

Endpoints for administrators to manage the store.

| Method | Endpoint               | Description                                   | Required Role |
| :----- | :--------------------- | :-------------------------------------------- | :------------ |
| `POST` | `/api/admin/products`  | Creates one or more new products.             | `ROLE_ADMIN`  |
| `GET`  | `/api/admin/products`  | Gets a paginated list of all products.        | `ROLE_ADMIN`  |
| `PUT`  | `/api/admin/products/{id}` | Updates an existing product.                  | `ROLE_ADMIN`  |
| `DELETE` | `/api/admin/products/{id}` | Deletes a product.                            | `ROLE_ADMIN`  |
| `POST` | `/api/admin/discounts` | Creates one or more new discounts.            | `ROLE_ADMIN`  |
| `PUT`  | `/api/admin/discounts/{discountId}`   | Edits an existing discount.                   | `ROLE_ADMIN`  |
| `GET`  | `/api/admin/orders`    | Gets a paginated list of all customer orders. | `ROLE_ADMIN`  |

### **`POST /api/admin/products`**

Creates one or more new products.

**Request Body:**

```json
[
  {
    "name": "High-Performance Gaming Mouse",
    "price": 79.99,
    "stock": 150,
    "category": "Peripherals"
  },
  {
    "name": "Ergonomic Office Chair",
    "price": 349.99,
    "stock": 50,
    "category": "Office Furniture"
  }
]
```

**Response (201 Created):**

```json
[
  {
    "productId": "102",
    "name": "High-Performance Gaming Mouse",
    "price": 79.99,
    "stock": 150,
    "category": "Peripherals"
  },
  {
    "productId": "103",
    "name": "Ergonomic Office Chair",
    "price": 349.99,
    "stock": 50,
    "category": "Office Furniture"
  }
]
```

### **`GET /api/admin/products`**

Gets a paginated list of all products.

**Example Request:** `GET /api/admin/products?page=0&size=5`
**Response (200 OK):**

```json
{
  "content": [
    {
      "productId": "101",
      "name": "Wireless Mechanical Keyboard",
      "price": 79.99,
      "stock": 50,
      "category": "Peripherals"
    },
    {
      "productId": "102",
      "name": "High-Performance Gaming Mouse",
      "price": 79.99,
      "stock": 150,
      "category": "Peripherals"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5
  },
  "totalPages": 1,
  "totalElements": 2
}
```

### **`POST /api/admin/discounts`**

Creates one or more new discounts. It can be a simple percentage, a complex rule-based deal, or a site-wide offer.

**Request Body:**

```json
[
  {
    "productId": null,
    "description": "10% Off Sitewide",
    "discountType": "PERCENTAGE",
    "rules": {
      "percentage": 10
    },
    "expirationDate": "2025-12-31T23:59:59Z"
  },
  {
    "productId": "101",
    "description": "Buy 1 Get 1 50% Off",
    "discountType": "BOGO_50_PERCENT_OFF_SECOND",
    "rules": {
      "buyQuantity": 1,
      "getQuantity": 1,
      "discountPercentage": 50
    },
    "expirationDate": "2025-12-31T23:59:59Z"
  }
]
```

**Response (201 Created):**

```json
[
  {
    "discountId": "58",
    "productId": "101",
    "description": "Buy 1 Get 1 50% Off",
    "discountType": "BOGO_50_PERCENT_OFF_SECOND"
  },
  {
    "discountId": "59",
    "productId": null,
    "description": "10% Off Sitewide",
    "discountType": "PERCENTAGE"
  }
]
```

### **`PUT /api/admin/discounts/{discountId}`**

Edits an existing discount. Allows updating the description, type, rules, expiration date, or associated product.

**Request Body:**

```json
{
  "productId": "101",
  "description": "Buy 2 Get 1 Free",
  "discountType": "BUY_2_GET_1_FREE",
  "rules": {
    "buyQuantity": 2,
    "getQuantity": 1,
    "discountPercentage": 100
  },
  "expirationDate": "2026-01-01T23:59:59Z"
}
```

**Response (200 OK):**

```json
{
  "discountId": "58",
  "productId": "101",
  "description": "Buy 2 Get 1 Free",
  "discountType": "BUY_2_GET_1_FREE"
}
```

### **`GET /api/admin/orders`**

Gets a paginated list of all customer orders.

**Example Request:** `GET /api/admin/orders?page=0&size=10&sort=createdAt,desc`
**Response (200 OK):**

```json
{
  "content": [
    {
      "orderId": "901",
      "userId": "45",
      "status": "SHIPPED",
      "totalPrice": 129.99
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 1,
  "totalElements": 1
}
```

---

## Customer Endpoints

Endpoints for customer interactions.

| Method   | Endpoint                     | Description                                                 | Required Role   |
| :------- | :--------------------------- | :---------------------------------------------------------- | :-------------- |
| `GET`    | `/api/products`              | Gets a paginated list of available products with filtering. | `ROLE_CUSTOMER` |
| `GET`    | `/api/products/{productId}`  | Gets details for a single product.                          | `ROLE_CUSTOMER` |
| `POST`   | `/api/basket/items`          | Adds an item to the current user's basket.                  | `ROLE_CUSTOMER` |
| `DELETE` | `/api/basket/items/{itemId}` | Removes an item from the current user's basket.             | `ROLE_CUSTOMER` |
| `GET`    | `/api/basket`                | Retrieves the current user's basket contents.               | `ROLE_CUSTOMER` |
| `POST`   | `/api/orders`                | Creates an order from the current user's basket.            | `ROLE_CUSTOMER` |
| `GET`    | `/api/orders`                | Gets a paginated list of the current user's past orders.    | `ROLE_CUSTOMER` |
| `GET`    | `/api/orders/{orderId}`      | Gets the details of a single past order.                    | `ROLE_CUSTOMER` |
| `GET`    | `/api/orders/{orderId}/receipt`      | Gets a receipt for an order, including items, deals applied, and total price. | `ROLE_CUSTOMER` |

### **`GET /api/products`**

Gets a paginated list of available products with filtering.

**Example Request (Combined Filters):**
`GET /api/products?category=peripherals&priceMin=50&priceMax=100&inStock=true`

This request asks for products in the "peripherals" category, with a price between $50 and $100, that are currently in stock.

**Response (200 OK):**

```json
{
  "content": [
    {
      "productId": "102",
      "name": "High-Performance Gaming Mouse",
      "price": 79.99,
      "stock": 150
    },
    {
      "productId": "102",
      "name": "High-Performance Gaming Mouse",
      "price": 79.99,
      "stock": 49,
      "category": "Peripherals"
    }
  ],
  "totalPages": 1,
  "totalElements": 1
}
```

### **`GET /api/products/{productId}`**

Gets details for a single product.

**Example Request:** `GET /api/products/101`
**Response (200 OK):**

```json
{
  "productId": "101",
  "name": "Wireless Mechanical Keyboard",
  "price": 129.99,
  "stock": 49,
  "category": "Peripherals"
}
```

### **`POST /api/basket/items`**

Adds an item to the current user's basket.

**Request Body:**

```json
{
  "productId": "101",
  "quantity": 1
}
```

**Response (200 OK):**

```json
{
  "message": "Item added to basket successfully"
}
```

### **`DELETE /api/basket/items/{itemId}`**

Removes an item from the current user's basket.

**Example Request:** `DELETE /api/basket/items/789`
**Response (200 OK):**

```json
{
  "message": "Item removed from basket successfully"
}
```

### **`GET /api/basket`**

Retrieves the current user's basket contents.

**Example Request:** `GET /api/basket`
**Response (200 OK):**

```json
{
  "items": [
    {
      "itemId": 789,
      "productId": "101",
      "name": "Wireless Mechanical Keyboard",
      "quantity": 1,
      "price": 129.99
    }
  ],
  "totalPrice": 129.99
}
```

### **`POST /api/orders`**

Creates an order from the current user's basket.

**Request Body:** (Empty, as it uses the session's basket)
**Response (201 Created):**

```json
{
  "orderId": "902",
  "status": "PROCESSING",
  "totalPrice": 129.99,
  "message": "Order created successfully"
}
```

### **`GET /api/orders`**

Gets a paginated list of the current user's past orders.

**Example Request:** `GET /api/orders?page=0&size=5`
**Response (200 OK):**

```json
{
  "content": [
    {
      "orderId": "902",
      "status": "PROCESSING",
      "totalPrice": 129.99,
      "createdAt": "2025-07-17T20:16:00Z"
    }
  ],
  "totalPages": 1,
  "totalElements": 1
}
```

### **`GET /api/orders/{orderId}`**

Gets the details of a single past order.

**Example Request:** `GET /api/orders/902`
**Response (200 OK):**

```json
{
  "orderId": "902",
  "status": "PROCESSING",
  "totalPrice": 129.99,
  "items": [
    {
      "productId": "101",
      "name": "Wireless Mechanical Keyboard",
      "quantity": 1,
      "price": 129.99
    }
  ]
}
```

### **`GET /api/orders/{orderId}/receipt`**

Gets a receipt for a specific order, including all purchased items, deals applied, and the total price.

**Example Request:** `GET /api/orders/902/receipt`

**Response (200 OK):**

```json
{
  "orderId": "902",
  "purchasedAt": "2025-07-17T20:16:00Z",
  "items": [
    {
      "productId": "101",
      "name": "Wireless Mechanical Keyboard",
      "quantity": 1,
      "unitPrice": 129.99,
      "total": 129.99
    },
    {
      "productId": "102",
      "name": "High-Performance Gaming Mouse",
      "quantity": 2,
      "unitPrice": 79.99,
      "total": 159.98
    }
  ],
  "dealsApplied": [
    {
      "description": "Buy 2 Get 1 Free",
      "discount": 79.99
    },
    {
      "description": "10% Off Sitewide",
      "discount": 28.00
    }
  ],
  "subtotal": 289.97,
  "totalDiscount": 107.99,
  "totalPrice": 181.98
}
```
