package com.example.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleOrderResponse {
    private String userId;

    private String productId;
    private String productName;
    private String productMadeBy;
    private String productImage;
    private int quantity;
    private int unitPrice;
    private int totalPrice;
    private String paymentMethod;
    private LocalDateTime createdAt;

    private String orderId;
}