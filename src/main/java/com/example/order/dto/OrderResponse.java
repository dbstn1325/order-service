package com.example.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private String userId;
    private String email;
    private String name;

    private String productId;
    private int stock;
    private int unitPrice;
    private int totalPrice;
    private LocalDateTime createdAt;

    private String orderId;
}