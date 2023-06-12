package com.example.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
    private String userId;

    private String productId;
    private String productName;
    private String productMadeBy;
    private String productImage;
    private int quantity;
    private int unitPrice;
    private int totalPrice;
    private String paymentMethod;

    private String orderId;
}
