package com.gateway.repositories;

import com.gateway.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByIdAndMerchantId(String id, UUID merchantId);

    long countByMerchantId(UUID merchantId);

    // ✅ REQUIRED for dashboard & transactions
    List<Payment> findAllByMerchantId(UUID merchantId);
    @Query("""
        SELECT COUNT(p)
        FROM Payment p
        WHERE p.merchantId = :merchantId
          AND p.status = 'SUCCESS'
    """)
    long countSuccessfulByMerchantId(UUID merchantId);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.merchantId = :merchantId
          AND p.status = 'SUCCESS'
    """)
    long sumSuccessfulAmountByMerchantId(UUID merchantId);
}
