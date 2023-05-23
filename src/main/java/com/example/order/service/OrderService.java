package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {
//    OrderDto createOrder(String userId, OrderRequest orderRequest);
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderByOrderId(String orderId);
    List<OrderResponse> getOrderByUserId(String userId);
    List<OrderDto> getAllOrders();
}
