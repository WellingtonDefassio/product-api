package com.example.productapi.modules.sales.dto;

import com.example.productapi.modules.product.dto.ProductQuantityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockCheckRequest {

    List<ProductQuantityDTO> products;


}
