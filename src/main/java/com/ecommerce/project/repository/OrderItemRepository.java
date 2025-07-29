package com.ecommerce.project.repository;


import com.ecommerce.project.model.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
