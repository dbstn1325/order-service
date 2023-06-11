package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderRequest {
    private String productId;   // 주문하고자 하는 상품 ID
    private String productName; // 주문하고자 하는 상품 이름
    private int quantity;       // 주문하고자 하는 상품갯수
    private int unitPrice;      // 가격
    private String paymentMethod;
}
