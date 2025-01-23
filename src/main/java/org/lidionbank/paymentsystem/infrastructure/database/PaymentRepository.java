package org.lidionbank.paymentsystem.infrastructure.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.lidionbank.paymentsystem.domain.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(UUID.randomUUID());
            entityManager.persist(payment);
        } else {
            payment = entityManager.merge(payment);
        }
        return payment;
    }

    public Optional<Payment> findById(UUID id) {
        Payment payment = entityManager.find(Payment.class, id);
        return Optional.ofNullable(payment);
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
