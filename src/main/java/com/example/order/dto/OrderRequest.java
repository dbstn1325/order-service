package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //다른 곳에서 생성자를 만들 수 있도록 설정해줍니다.
public class OrderRequest {
    private String productId;   //주문하고자 하는 상품ID
    private int stock;          //주문하고자 하는 상품갯수
    private int unitPrice;      //가격


}
