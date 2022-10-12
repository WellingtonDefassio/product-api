package com.example.productapi.modules.product.repository;

import com.example.productapi.modules.product.model.Product;
import com.example.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameIgnoreCaseContaining(String name);
    List<Product> findByCategoryId(Integer id);
    List<Product> findBySupplierId(Integer id);
    Boolean existsByCategoryId(Integer id);
    Boolean existsBySupplierId(Integer id);


}
