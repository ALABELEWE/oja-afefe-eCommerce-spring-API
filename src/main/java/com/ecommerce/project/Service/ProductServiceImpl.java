package com.ecommerce.project.Service;


import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileService fileService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    //Field dependency injection
    @Autowired
    private ModelMapper modelMapper;

    //Constructor injection
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, FileService fileService) {
        this.productRepository = productRepository;
        this.fileService = fileService;
    }
    //Setter injection
    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long  categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryID",categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for(Product product : products) {
            if(product.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }


        if (isProductNotPresent) {
            //Transformation
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product productSaved = productRepository.save(product);

            return modelMapper.map(productSaved, ProductDTO.class);
        }else{
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageable);


       List<Product> products = productPage.getContent();

       List<ProductDTO> productDTOS = products.stream()
               .map(product -> modelMapper.map(product, ProductDTO.class))
               .toList();

       if(products.isEmpty()) {
           throw new APIException("No products found");
       }

       ProductResponse productResponse = new ProductResponse();
       productResponse.setContent(productDTOS);
       productResponse.setPageNumber(productPage.getNumber());
       productResponse.setPageSize(productPage.getSize());
       productResponse.setTotalPages(productPage.getTotalPages());
       productResponse.setTotalElements(productPage.getTotalElements());
       productResponse.setLastPage(productPage.isLast());
       return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryID",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(pageable, category);
        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIException( category.getCategoryName() + " has no product in it");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse findProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase(pageable, '%' + keyword + '%');
        List<Product> products = productPage.getContent();

        if(products.isEmpty()) {
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        //Get the existing product from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductID",productId));
        //update the product info
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        double specialPrice = productDTO.getPrice()-
                ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());

        //save the new update to the database
        Product productSaved = productRepository.save(product);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();

            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return  modelMapper.map(productSaved, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        //find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductID",productId));


        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get product from database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductID",productId));

        //Upload image to server
        //Get the file name of uploaded image
//      String path = "images/";   //use application properties instead
        String fileName = fileService.uploadImage(path, image);

        //updating the new file name to the product
        product.setImage(fileName);

        //Save updated product
        productRepository.save(product);

        //return DTO after mapping product to DTO
        return modelMapper.map(product, ProductDTO.class);

    }

}
