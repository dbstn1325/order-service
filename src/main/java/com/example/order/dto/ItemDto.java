package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String cartId;
    private String productId;
    private String productImage;
    private String productMadeBy;
    private String productName;
    private int quantity;
    private int totalPrice;
    private int unitPrice;
}
