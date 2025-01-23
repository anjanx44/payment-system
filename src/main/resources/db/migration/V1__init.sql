CREATE TABLE payment
(
    id         UUID PRIMARY KEY,
    request_id VARCHAR(255),
    amount     DECIMAL(19, 2),
    currency   VARCHAR(3),
    status     VARCHAR(255),
    provider   VARCHAR(255)
);