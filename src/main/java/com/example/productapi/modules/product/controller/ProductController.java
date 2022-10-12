package com.example.productapi.modules.product.controller;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.modules.category.dto.CategoryRequest;
import com.example.productapi.modules.category.dto.CategoryResponse;
import com.example.productapi.modules.product.dto.ProductRequest;
import com.example.productapi.modules.product.dto.ProductResponse;
import com.example.productapi.modules.product.service.ProductService;
import com.example.productapi.modules.supplier.dto.SupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest productRequest) {
        return productService.save(productRequest);
    }


    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest productRequest, @PathVariable Integer id) {
        return productService.update(productRequest, id);
    }

    @GetMapping()
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return productService.findByIdResponse(id);
    }


    @GetMapping("/category/{id}")
    public List<ProductResponse> findByDescription(@PathVariable Integer id) {
        return productService.findByCategoryId(id);
    }

    @GetMapping("/supplier/{id}")
    public List<ProductResponse> findBySupplierName(@PathVariable Integer id) {
        return productService.findBySupplierId(id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse deleteById(@PathVariable Integer id) {
        return productService.delete(id);
    }

}
