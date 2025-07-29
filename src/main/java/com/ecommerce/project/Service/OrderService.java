package com.ecommerce.project.Service;

import com.ecommerce.project.payload.OrderDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;


public interface OrderService {
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgRequestMessage);
}
