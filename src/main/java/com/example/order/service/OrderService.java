package com.example.order.service;

import com.example.order.dto.*;
import com.example.order.exception.OrderCreationException;

import java.util.List;

public interface OrderService {
//    OrderDto createOrder(String userId, OrderRequest orderRequest);
    List<MultipleOrderResponse> createOrders(String userId, MultipleOrderRequest multipleOrderRequest);
    OrderDto createOrder(OrderDto orderDto) throws OrderCreationException;
    OrderDto getOrderByOrderId(String orderId);
    List<SingleOrderResponse> getOrderByUserId(String userId);
    List<OrderDto> getAllOrders();
}
