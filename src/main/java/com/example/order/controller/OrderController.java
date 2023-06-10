package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
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
    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody
    OrderRequest orderRequest) {
        // OrderRequestDto -> OrderDto
        ModelMapper modelMapper = new ModelMapper();
        OrderDto orderDto = modelMapper.map(orderRequest, OrderDto.class);
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //연결 전략을 엄격하게 변경하여 같은 타입의 필드명 역시 같은 경우만 동작하도록 변경
        orderDto.setUserId(userId);


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

//    @PostMapping("/{userId}/orders")
//    public ResponseEntity<OrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody
//    OrderRequest orderRequest) {
//
//
//        OrderDto responseOrderDto = orderService.createOrder(userId, orderRequest);
//
//        //TODO 예외처리
//        //이제, Controller에서 보내지말고, Service단에서보내서 확인하자. 그래야 나중에 테스트하기도 편함
//        ModelMapper modelMapper = new ModelMapper();
//        /* 사용자가 주문한 상품 정보 -> OrderDetail(userId, orderId, 상품의 총 가격 포함)로 변환 */
//        OrderDto orderDetail = modelMapper.map(orderRequest, OrderDto.class);
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //연결 전략을 엄격하게 변경하여 같은 타입의 필드명 역시 같은 경우만 동작하도록 변경
//        orderDetail.setUserId(userId); // 사용자의 고유 ID 기입
//        orderDetail.setOrderId(UUID.randomUUID().toString()); //새로운 Order ID 기입
//        orderDetail.setTotalPrice(orderDetail.getUnitPrice() * orderDetail.getStock()); // 총 주문 가격 기입
//
//
//        /* Kafka에 주문 정보 message send (by Kafka Producer) */
////        OrderDto productResponse = kafkaProducer.order(orderDetail);
////        log.info("Controller: {}", productResponse);
//
//
//        OrderResponse orderResponse = modelMapper.map(responseOrderDto, OrderResponse.class);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(orderResponse);
//    }

    /**
     * 특정 사용자의 주문목록을 조회하는 메서드
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable("userId") String userId){
        ModelMapper modelMapper = new ModelMapper();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderByUserId(userId));

    }

//    @GetMapping("/{orderId}/orders")
//    public ResponseEntity<ResponseOrder> getOrder(@PathVariable("orderId") String orderId){
//        ModelMapper modelMapper = new ModelMapper();
//        OrderDto orderDto = orderService.getOrderByOrderId(orderId);
//        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(responseOrder);
//    }

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
