package com.example.order.dto;

import lombok.Data;

@Data
public class SingleOrderRequest {
    private String productId;       // 주문하고자 하는 상품 ID
    private String productName;     // 주문하고자 하는 상품 이름
    private String productMadeBy;   // 주문하고자 하는 상품의 제조사
    private String productImage;    // 주문하고자 하는 상품의 이미지
    private int quantity;           // 주문하고자 하는 상품갯수
    private int unitPrice;          // 가격
    private String paymentMethod;
}
