package com.example.productapi.modules.supplier.repository;

import com.example.productapi.modules.category.model.Category;
import com.example.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameIgnoreCaseContaining(String name);

}
