package com.example.productapi.modules.supplier.controller;

import com.example.productapi.config.exception.SuccessResponse;
import com.example.productapi.modules.category.dto.CategoryResponse;
import com.example.productapi.modules.supplier.dto.SupplierRequest;
import com.example.productapi.modules.supplier.dto.SupplierResponse;
import com.example.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping()
    public SupplierResponse save(@RequestBody SupplierRequest supplierRequest) {
       return supplierService.save(supplierRequest);
    }

    @PutMapping("{îd}")
    public SupplierResponse update(@RequestBody SupplierRequest supplierRequest, Integer id) {
        return supplierService.update(supplierRequest, id);
    }


    @GetMapping()
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return supplierService.findByIdResponse(id);
    }


    @GetMapping("/name/{name}")
    public List<SupplierResponse> findByDescription(@PathVariable String name) {
        return supplierService.findByDescription(name);
    }

    @DeleteMapping("{id}")
    public SuccessResponse deleteById(@PathVariable Integer id) {
        return supplierService.delete(id);
    }

}
