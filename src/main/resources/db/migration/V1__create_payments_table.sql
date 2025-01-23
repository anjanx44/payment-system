CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          amount DECIMAL(19,2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          customer_id VARCHAR(255) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          provider VARCHAR(20) NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_customer_id ON payments(customer_id);
CREATE INDEX idx_payments_provider ON payments(provider);
