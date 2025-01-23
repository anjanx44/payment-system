package org.lidionbank.paymentsystem.infrastructure.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.lidionbank.paymentsystem.domain.Payment;

@ApplicationScoped
public class PaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Payment payment) {
        entityManager.persist(payment);
    }
}