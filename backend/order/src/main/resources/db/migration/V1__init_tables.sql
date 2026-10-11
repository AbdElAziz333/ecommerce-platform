CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version BIGINT NOT NULL DEFAULT 0,
    order_number VARCHAR(30) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    shipping_street_line VARCHAR(150) NOT NULL,
    shipping_city VARCHAR(30) NOT NULL,
    shipping_state VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20) NOT NULL,
    shipping_address_id BIGINT NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0),
    shipping_amount NUMERIC(12, 2) NOT NULL CHECK (shipping_amount >= 0),
    total_amount NUMERIC(12, 2) NOT NULL CHECK (total_amount >= 0),
    notes VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_orders_payment_status CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_orders_payment_method CHECK (payment_method IN ('VISA', 'VODAFONE_CASH')),
    CONSTRAINT chk_orders_city CHECK (shipping_city IN ('ALEXANDRIA', 'EL_BEHEIRA', 'CAIRO', 'TANTA'))
);

CREATE INDEX idx_orders_user_created ON orders (user_id, created_at DESC);

CREATE TABLE order_item (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    variant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    options JSONB NOT NULL DEFAULT '{}',
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    quantity INT NOT NULL CHECK (quantity > 0),
    CHECK (true)
);

CREATE INDEX idx_order_item_order_id ON order_item (order_id);

CREATE TABLE cart (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT UNIQUE,
    guest_id VARCHAR(36) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_cart_one_owner CHECK ((user_id IS NULL) <> (guest_id IS NULL))
);

CREATE INDEX idx_cart_stale_guests ON cart (updated_at) WHERE guest_id IS NOT NULL;

CREATE TABLE cart_item (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES cart (id) ON DELETE CASCADE,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity BETWEEN 1 AND 99),
    CONSTRAINT uq_cart_item_variant UNIQUE (cart_id, variant_id)
);