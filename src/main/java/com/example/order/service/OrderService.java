package com.example.order.service;

import com.example.order.dto.*;
import com.example.order.exception.OrderCreationException;

import java.util.List;

public interface OrderService {
//    OrderDto createOrder(String userId, OrderRequest orderRequest);
    List<OrderResponse2> createOrders(String userId, OrderRequest2 orderRequest2);
    OrderDto createOrder(OrderDto orderDto) throws OrderCreationException;
    OrderDto getOrderByOrderId(String orderId);
    List<OrderResponse> getOrderByUserId(String userId);
    List<OrderDto> getAllOrders();
}
