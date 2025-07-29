package com.ecommerce.project.payload;


import com.ecommerce.project.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;
    private Product product;
    private int quantity;
    private double discount;
    private double orderedProductPrice;

}
