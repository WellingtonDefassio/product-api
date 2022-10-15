package com.example.productapi.modules.product.service;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.config.exception.ValidationException;
import com.example.productapi.modules.category.service.CategoryService;
import com.example.productapi.modules.product.dto.*;
import com.example.productapi.modules.product.model.Product;
import com.example.productapi.modules.product.repository.ProductRepository;
import com.example.productapi.modules.sales.client.SalesClient;
import com.example.productapi.modules.sales.dto.ProductStockCheckRequest;
import com.example.productapi.modules.sales.dto.SalesConfirmationDTO;
import com.example.productapi.modules.sales.enums.SalesStatus;
import com.example.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import com.example.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    public static Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SalesConfirmationSender salesConfirmationSender;
    @Autowired
    private SalesClient salesClient;


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

    public void updateProductStock(ProductStockDTO productStockDTO) {
        try {
            validateStockUpdateData(productStockDTO);
            updateStock(productStockDTO);
        } catch (Exception e) {
            log.error("Error while trying to update stock for message with error: {}", e.getMessage(), e);
            var rejectedMessage = new SalesConfirmationDTO(productStockDTO.getSalesId(), SalesStatus.REJECTED);
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);

        }

    }
    @Transactional
    public void updateStock(ProductStockDTO productStockDTO) {
        var productsForUpdate = new ArrayList<Product>();

        productStockDTO
                .getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId());
                    validateQuantityInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);

                });
        if(!ObjectUtils.isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(productStockDTO.getSalesId(), SalesStatus.APPROVED);
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }


    }


    private void validateStockUpdateData(ProductStockDTO productStockDTO) {
        if (ObjectUtils.isEmpty(productStockDTO) || ObjectUtils.isEmpty(productStockDTO.getSalesId())) {
            throw new ValidationException("The product data or sales ID cannot be null");
        }
        if (ObjectUtils.isEmpty(productStockDTO.getProducts())) {
            throw new ValidationException("The sales products must be informed.");
        }

        productStockDTO.getProducts().forEach(sales -> {
            if (ObjectUtils.isEmpty(sales.getQuantity()) || ObjectUtils.isEmpty(sales.getProductId())) {
                throw new ValidationException("the product id and quantity must be informed");
            }
        });
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct){

        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(String.format("The product %s is out of stock.", existingProduct.getId()));
        }

    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        try {
            var sales = salesClient.findSalesByProductId(product.getId())
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            return ProductSalesResponse.of(product, sales.getSalesIds());
        } catch (Exception e) {
            throw new ValidationException("There was an error trying to get the product's sales.");
        }
    }


    public SuccessResponse checkProductStock (ProductStockCheckRequest request) {
        if(ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getProducts())) {
            throw new ValidationException("The request data must be informed");
        }

        request.getProducts().forEach(this::validateStock);
        return SuccessResponse.create("The stock is ok!");
    }

    private void validateStock(ProductQuantityDTO productQuantity) {
        if(ObjectUtils.isEmpty(productQuantity) || ObjectUtils.isEmpty(productQuantity.getProductId())) {
            throw new ValidationException("Product Id and quantity must be informed");
        }
        var product = findById(productQuantity.getProductId());
        if(productQuantity.getQuantity()> product.getQuantityAvailable()) {
                throw new ValidationException(String.format("The product %s is out of stock.", product.getId()));
        }

    }
}
