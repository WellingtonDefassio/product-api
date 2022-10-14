package com.example.productapi.modules.product.model;

import com.example.productapi.modules.category.model.Category;
import com.example.productapi.modules.product.dto.ProductRequest;
import com.example.productapi.modules.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FK_CATEGORY")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "FK_SUPPLIER")
    private Supplier supplier;

    @Column(name = "QUANTITY_AVAILABLE")
    private Integer quantityAvailable;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }


    public static Product of(ProductRequest productRequest, Category category, Supplier supplier) {
        return Product.builder()
                .name(productRequest.getName())
                .quantityAvailable(productRequest.getQuantityAvailable())
                .category(category)
                .supplier(supplier)
                .build();
    }

   public void updateStock(Integer quantity) {
        quantityAvailable = quantityAvailable - quantity;
   }

}
