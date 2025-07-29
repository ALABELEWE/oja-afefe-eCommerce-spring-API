package com.ecommerce.project.payload;


import com.ecommerce.project.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO productDTO;
    private Integer quantity;
    private double price;
    private double discount;

}
