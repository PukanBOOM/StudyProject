CREATE TABLE IF NOT EXISTS products (
    id          VARCHAR(64)  PRIMARY KEY,
    name        VARCHAR(256) NOT NULL,
    description TEXT         NOT NULL DEFAULT '',
    category    VARCHAR(32)  NOT NULL DEFAULT 'NONE',
    lock        VARCHAR(64)  NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS offers (
    id          VARCHAR(64)     PRIMARY KEY,
    product_id  VARCHAR(64)     NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    shop_name   VARCHAR(256)    NOT NULL DEFAULT '',
    price       DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    url         VARCHAR(512)    NOT NULL DEFAULT ''
);

CREATE INDEX IF NOT EXISTS idx_offers_product_id ON offers(product_id);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);