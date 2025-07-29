package com.ecommerce.project.controller;


import com.ecommerce.project.Service.ProductService;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Create a new Product ", description = "API to create a new product")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO newProductDTO = productService.addProduct(productDTO, categoryId);
        return new ResponseEntity<>(newProductDTO, HttpStatus.CREATED);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Get all Products ", description = "API to get all products")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder
    ) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Get product by category ", description = "API to get all product in a particular category ")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId,
                                                                @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT, required = false) String sortBy,
                                                                @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Get product by keyword ", description = "API to get product using keyword")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword,
                                                               @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                               @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                               @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT, required = false) String sortBy,
                                                               @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
        ProductResponse productResponse = productService.findProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Edit product using ID ", description = "API to edit a product using ID")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Delete a product using product ID ", description = "API to delete a product using ID")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @Tag(name="Product APIs", description = "API for managing all product")
    @Operation(summary = "Add image ", description = "API to edit a product by adding image")
    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

}
