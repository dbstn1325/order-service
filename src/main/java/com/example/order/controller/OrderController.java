package com.example.order.controller;

import com.example.order.dto.*;
import com.example.order.exception.OrderCreationException;
import com.example.order.messagequeue.KafkaProducer;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    /**
     * 사용자의 주문을 생성하는 메서드
     * @param userId: 사용자의 고유 ID
     * @param orderRequest: 사용자가 주문한 상품 정보
     */
    @PostMapping("/{userId}/order")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody
    OrderRequest orderRequest) throws OrderCreationException {
        // OrderRequestDto -> OrderDto
        ModelMapper modelMapper = new ModelMapper();
        OrderDto orderDto = modelMapper.map(orderRequest, OrderDto.class);
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //연결 전략을 엄격하게 변경하여 같은 타입의 필드명 역시 같은 경우만 동작하도록 변경
        orderDto.setUserId(userId);
        orderDto.setTotalPrice(orderDto.getQuantity() * orderDto.getUnitPrice());
        orderDto.setPaymentMethod(orderRequest.getPaymentMethod());



        // Order micro service쪽의 DB 내에 먼저 저장
        OrderDto responseOrderDto = orderService.createOrder(orderDto);

        /* Kafka에 특정 토픽으로 주문 정보 message send (by Kafka Producer)
            *해당 토픽은 Product topic이 구독하고 있기 때문에 Product topic에서 해당 message를 받을 수 있습니다.
        */
        kafkaProducer.send("update-quantity-product", orderDto);

        OrderResponse orderResponse = modelMapper.map(responseOrderDto, OrderResponse.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderResponse);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse2>> createOrders(@PathVariable("userId") String userId,
        @RequestBody OrderRequest2 orderRequest2) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(orderService.createOrders(userId, orderRequest2));
    }




    /**
     * 특정 사용자의 주문목록을 조회하는 메서드
     * @param userId 조회하고자 하는 사용자의 userID
     * @return 특정 사용자의 주문목록
     */
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable("userId") String userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderByUserId(userId));

    }


    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> findAllOrders(){
        List<OrderDto> orders = orderService.getAllOrders();
        List<OrderResponse> orderResponses = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        orders.forEach(v -> orderResponses.add(
                modelMapper.map(v, OrderResponse.class)
        ));


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderResponses);
    }
}
