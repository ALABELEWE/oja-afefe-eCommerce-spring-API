package com.ecommerce.project.controller;


import com.ecommerce.project.Service.CategoryService;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Tag(name="Category APIs", description = "API for managing all category")
    @Operation(summary = "Get All Categories", description = "End-point to get all categories")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORIES, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder
    ) {
        CategoryResponse categoryResponse =  categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @Tag(name="Category APIs", description = "API for managing all category")
    @Operation(summary = "Create a new Category", description = "End-point to Create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server", content = @Content)

    })
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Tag(name="Category APIs", description = "API for managing all category")
    @Operation(summary = "Delete a category with ID", description = "End-Point to delete a particular ID")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable("categoryId") Long categoryId) {
            CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @Tag(name="Category APIs", description = "API for managing all category")
    @Operation(summary = "Edit a Category", description = "Edit category with ID")
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable("categoryId") Long categoryId) {
            CategoryDTO saveCategory = categoryService.updateCategory(categoryDTO, categoryId);
            return new ResponseEntity<>(saveCategory, HttpStatus.OK);
    }



}
