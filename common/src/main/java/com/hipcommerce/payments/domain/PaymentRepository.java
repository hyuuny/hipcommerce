package com.hipcommerce.payments.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByImpUid(final String impUid);

}
