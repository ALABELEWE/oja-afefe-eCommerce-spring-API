package com.ecommerce.project.Service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgRequestMessage) {
        //Getting user cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart ", "email", emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId", addressId));

        //Create a new order with payment info
        Order order = new Order();
        order.setEmail(emailId);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus("Payement Accepted");
        Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgRequestMessage,pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        //Get items from the cart info the order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null || cartItems.isEmpty()){
            throw new APIException("No item found in the cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);

            orderItems = (List<OrderItem>) orderItemRepository.saveAll(orderItems);

        }
        //Update product stock
        cart.getCartItems().forEach(cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), cartItem.getProduct().getProductId());
        });



        //send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem -> {
            orderDTO.getOrderItems().add(
                    modelMapper.map(orderItem, OrderItemDTO.class)
            );
        });
        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
