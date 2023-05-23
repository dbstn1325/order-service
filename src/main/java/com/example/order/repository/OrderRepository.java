package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(String orderId);      //특정 주문번호에 대한 주문정보 조회
    List<Order> findByUserId(String userId);  //특정 유저에 대한 주문정보 조회
}
