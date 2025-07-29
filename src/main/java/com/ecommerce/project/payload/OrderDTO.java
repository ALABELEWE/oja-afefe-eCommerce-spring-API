package com.ecommerce.project.payload;


import com.ecommerce.project.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    String orderStatus;
    private Long addressId;

}
