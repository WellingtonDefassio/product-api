package com.example.productapi.modules.product.service;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.config.exception.ValidationException;
import com.example.productapi.modules.category.service.CategoryService;
import com.example.productapi.modules.product.dto.ProductRequest;
import com.example.productapi.modules.product.dto.ProductResponse;
import com.example.productapi.modules.product.model.Product;
import com.example.productapi.modules.product.repository.ProductRepository;
import com.example.productapi.modules.supplier.service.SupplierService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    public static Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;


    public ProductResponse save(ProductRequest productRequest) {
        validateProductDataInformed(productRequest);
        validateCategoryAndSupplierIdInformed(productRequest);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());
        var product = productRepository.save(Product.of(productRequest, category, supplier));
        return ProductResponse.of(product);
    }
    public ProductResponse update(ProductRequest productRequest, Integer id) {
        validateProductDataInformed(productRequest);
        validateInformedId(id);
        validateCategoryAndSupplierIdInformed(productRequest);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());
        var product = Product.of(productRequest, category, supplier);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    private void validateProductDataInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getName())) {
            throw new ValidationException("The product name was not informed");
        }
        if (ObjectUtils.isEmpty(productRequest.getQuantityAvailable())) {
            throw new ValidationException("The product quantity available was not informed");
        }
        if (productRequest.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The product quantity has to be bigger then zero");
        }
    }

    private void validateCategoryAndSupplierIdInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getCategoryId())) {
            throw new ValidationException("The category ID was not informed");
        }
        if (ObjectUtils.isEmpty(productRequest.getSupplierId())) {
            throw new ValidationException("The supplier ID was not informed");
        }
    }


    public List<ProductResponse> findByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new ValidationException("The product name must be informed");
        }
        return productRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }


    public List<ProductResponse> findAll() {

        return productRepository.findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public Product findById(Integer id) {
        validateInformedId(id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given ID."));
    }

    public ProductResponse findByIdResponse(Integer id) {

        return ProductResponse.of(findById(id));
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        validateInformedId(supplierId);
        return productRepository
                .findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        validateInformedId(categoryId);
        return productRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public Boolean existsByCategoryId(Integer id) {
        return productRepository.existsByCategoryId(id);
    }


    public Boolean existsBySupplierId(Integer id) {
        return productRepository.existsBySupplierId(id);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        productRepository.deleteById(id);
        return SuccessResponse.create("category was delete");
    }

    private void validateInformedId(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("the id must be informed");
        }
    }
}
