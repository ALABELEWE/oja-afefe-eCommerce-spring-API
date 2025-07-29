package com.ecommerce.project.repository;


import com.ecommerce.project.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
