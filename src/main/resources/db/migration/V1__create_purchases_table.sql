CREATE TABLE purchases
(
    id               VARCHAR(36) PRIMARY KEY,
    description      VARCHAR(50)    NOT NULL,
    transaction_date DATE           NOT NULL,
    purchase_amount  DECIMAL(12, 2) NOT NULL
);

CREATE INDEX idx_purchases_id ON purchases (id);
CREATE INDEX idx_purchases_transaction_date ON purchases (transaction_date);
