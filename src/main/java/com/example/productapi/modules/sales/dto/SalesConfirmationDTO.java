package com.example.productapi.modules.sales.dto;


import com.example.productapi.modules.sales.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesConfirmationDTO {

    private String salesId;
    private SalesStatus status;
}
