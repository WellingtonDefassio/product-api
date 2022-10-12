package com.example.productapi.modules.supplier.service;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.config.exception.ValidationException;
import com.example.productapi.modules.product.service.ProductService;
import com.example.productapi.modules.supplier.dto.SupplierRequest;
import com.example.productapi.modules.supplier.dto.SupplierResponse;
import com.example.productapi.modules.supplier.model.Supplier;
import com.example.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;


    public SupplierResponse save(SupplierRequest supplierRequest) {
        validateSupplierNameInformed(supplierRequest);
        var supplier = supplierRepository.save(Supplier.of(supplierRequest));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest supplierRequest, Integer id) {
        validateSupplierNameInformed(supplierRequest);
        var supplier = Supplier.of(supplierRequest);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }


    private void validateSupplierNameInformed(SupplierRequest supplierRequest) {
        if (ObjectUtils.isEmpty(supplierRequest.getName())) {
            throw new ValidationException("The supplier name was not informed");
        }
    }

    public List<SupplierResponse> findByDescription(String description) {
        if (ObjectUtils.isEmpty(description)) {
            throw new ValidationException("The category description must be informed");
        }
        return supplierRepository.findByNameIgnoreCaseContaining(description)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }


    public List<SupplierResponse> findAll() {

        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public Supplier findById(Integer id) {
        validateInformedId(id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given ID."));
    }

    public SupplierResponse findByIdResponse(Integer id) {

        return SupplierResponse.of(findById(id));
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if(productService.existsBySupplierId(id)) {
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("supplier was delete");
    }

    private void validateInformedId(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("supplier id must be informed");
        }
    }

}
