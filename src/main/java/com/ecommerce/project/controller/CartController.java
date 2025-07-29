package com.ecommerce.project.controller;


import com.ecommerce.project.Service.CartService;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Tag(name="Cart APIs", description = "API for managing all cart")
    @Operation(summary = "Create Quantity ", description = "API to create new quantity")
    @PostMapping("/carts/products/{productId}/quantity/{quantityId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long productId, @PathVariable Integer quantityId) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantityId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @Tag(name="Cart APIs", description = "API for managing all cart")
    @Operation(summary = "Get Carts ", description = "API to get all carts")
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOS = cartService.findAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    @Tag(name="Cart APIs", description = "API for managing all cart")
    @Operation(summary = "Get User's Cart ", description = "API to get all carts from a user")
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartsById() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @Tag(name="Cart APIs", description = "API for managing all cart")
    @Operation(summary = "Edit product in Carts ", description = "API to edit quantity of product in a cart using ID")
    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO>  updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @Tag(name="Cart APIs", description = "API for managing all cart")
    @Operation(summary = "Get product from cart ", description = "API to delete a product from cart using ID")
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
