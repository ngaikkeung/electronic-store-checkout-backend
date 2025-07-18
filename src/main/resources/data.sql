-- Users
INSERT INTO users (name, email, created_at) VALUES ('Alice', 'alice@example.com', CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (role_name) VALUES ('ROLE_CUSTOMER');

-- Permissions
INSERT INTO permissions (permission_name) VALUES ('READ_PRODUCTS');
INSERT INTO permissions (permission_name) VALUES ('CREATE_ORDER');

-- Products
INSERT INTO products (name, price, stock, category) VALUES ('Wireless Mechanical Keyboard', 129.99, 50, 'Peripherals');

-- Discounts (for the last inserted product)
INSERT INTO discounts (product_id, description, discount_type, rules, expiration_date) VALUES (IDENTITY(), '10% Off', 'PERCENTAGE', '{"percentage":10}', '2025-12-31T23:59:59');
INSERT INTO discounts (product_id, description, discount_type, rules, expiration_date) VALUES (NULL, '10% Off Sitewide', 'PERCENTAGE', '{"percentage":10}', '2025-12-31T23:59:59');

-- Orders (for the last inserted user)
INSERT INTO orders (user_id, status, total_price, created_at) VALUES (IDENTITY(), 'SHIPPED', 129.99, CURRENT_TIMESTAMP);

-- Order Items (for the last inserted order and product)
INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (IDENTITY(), IDENTITY(), 1, 129.99);

-- User Roles (for the last inserted user and role)
INSERT INTO user_roles (user_id, role_id) VALUES (IDENTITY(), 1);

-- Role Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 2); 