package org.lidionbank.paymentsystem.infrastructure.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.lidionbank.paymentsystem.domain.Payment;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Payment save(Payment payment) {
        entityManager.persist(payment);
        return payment;
    }

    public Payment findById(UUID id) {
        return entityManager.find(Payment.class, id);
    }

    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return entityManager.createQuery(
                        "SELECT p FROM Payment p WHERE p.status = :status", Payment.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Payment> findAll(int page, int size) {
        return entityManager.createQuery("SELECT p FROM Payment p ORDER BY p.id", Payment.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
