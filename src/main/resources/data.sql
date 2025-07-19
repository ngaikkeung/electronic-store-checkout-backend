-- Users
INSERT INTO users (name, email, created_at) VALUES
('Alice', 'alice@example.com', CURRENT_TIMESTAMP),
('Bob', 'bob@example.com', CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (role_name) VALUES
('ROLE_ADMIN'),
('ROLE_CUSTOMER');

-- Permissions
INSERT INTO permissions (permission_name) VALUES
('ADMIN_READ'),
('ADMIN_WRITE'),
('PRODUCT_READ'),
('PRODUCT_WRITE'),
('ORDER_READ'),
('ORDER_WRITE'),
('BASKET_READ'),
('BASKET_WRITE');

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- Alice is an Admin
(2, 2); -- Bob is a Customer

-- Role Permissions
-- Admin
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), -- ADMIN_READ
(1, 2), -- ADMIN_WRITE
(1, 3), -- PRODUCT_READ
(1, 4), -- PRODUCT_WRITE
(1, 5), -- ORDER_READ
(1, 6), -- ORDER_WRITE
(1, 7), -- BASKET_READ
(1, 8); -- BASKET_WRITE

-- Customer
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 3), -- PRODUCT_READ
(2, 5), -- ORDER_READ
(2, 6), -- ORDER_WRITE
(2, 7), -- BASKET_READ
(2, 8); -- BASKET_WRITE


-- Products
INSERT INTO products (name, price, stock, category) VALUES
('Laptop', 1200.00, 50, 'Electronics'),
('Mouse', 25.00, 200, 'Accessories'),
('Keyboard', 75.00, 150, 'Accessories'),
('Monitor', 300.00, 100, 'Electronics'),
('Webcam', 45.00, 75, 'Accessories');

-- Discounts
-- Buy One Get One Free on Mouse
INSERT INTO discounts (product_id, description, discount_type, rules, expiration_date, create_at) VALUES
(2, 'Buy One Get One Free on Mouse', 'BOGO_DEAL', '{"buyQuantity": 1, "getQuantity": 1, "discountPercentage": 100}', '2025-12-31 23:59:59', CURRENT_TIMESTAMP);
