package com.example.productapi.modules.category.service;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.config.exception.ValidationException;
import com.example.productapi.modules.category.dto.CategoryRequest;
import com.example.productapi.modules.category.dto.CategoryResponse;
import com.example.productapi.modules.category.model.Category;
import com.example.productapi.modules.category.repository.CategoryRepository;
import com.example.productapi.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public CategoryResponse save(CategoryRequest categoryRequest) {
        validateCategoryNameInformed(categoryRequest);
        var category = categoryRepository.save(Category.of(categoryRequest));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest categoryRequest, Integer id) {
        validateCategoryNameInformed(categoryRequest);
        var category = Category.of(categoryRequest);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }


    private void validateCategoryNameInformed(CategoryRequest categoryRequest) {
        if (ObjectUtils.isEmpty(categoryRequest.getDescription())) {
            throw new ValidationException("The category description was not informed");
        }
    }

    public List<CategoryResponse> findByDescription(String description) {
        if (ObjectUtils.isEmpty(description)) {
            throw new ValidationException("The category description must be informed");
        }

        return categoryRepository.findByDescriptionIgnoreCaseContaining(description).stream().map(CategoryResponse::of).collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id));
    }


    public List<CategoryResponse> findAll() {

        return categoryRepository.findAll().stream().map(CategoryResponse::of).collect(Collectors.toList());
    }

    public Category findById(Integer id) {
        validateInformedId(id);
        return categoryRepository.findById(id).orElseThrow(() -> new ValidationException("There's no category for the given ID."));
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("You cannot delete this category because it's already defined by a product");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("category was delete");
    }

    private void validateInformedId(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("supplier id must be informed");
        }
    }

}
