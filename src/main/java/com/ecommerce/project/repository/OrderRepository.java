package com.ecommerce.project.repository;

import com.ecommerce.project.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
