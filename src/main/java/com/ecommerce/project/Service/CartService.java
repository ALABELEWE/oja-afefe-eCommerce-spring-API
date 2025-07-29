package com.ecommerce.project.Service;


import com.ecommerce.project.payload.CartDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantityId);

    List<CartDTO> findAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
