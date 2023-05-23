package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.messagequeue.KafkaProducer;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    public OrderDto createOrder(OrderDto orderDto){
        /* Order 변환 */
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order order = modelMapper.map(orderDto, Order.class);


        /* 주문 정보 저장 */
        orderRepository.save(order);

        OrderDto resultOrderDto = modelMapper.map(order, OrderDto.class);

        return resultOrderDto;
    }

    @Override
    public List<OrderResponse> getOrderByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        List<OrderResponse> result = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        orders.forEach(v->
                result.add(modelMapper.map(v, OrderResponse.class)
                ));

        return result;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Order order = orderRepository.findByOrderId(orderId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);

        return orderDto;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        List<OrderDto> orderDtos = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        orders.forEach(v -> orderDtos.add(
                modelMapper.map(v, OrderDto.class)
        ));

        return orderDtos;
    }
}
