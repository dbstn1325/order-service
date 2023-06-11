package com.example.order.controller;

import com.example.order.dto.*;
import com.example.order.exception.OrderCreationException;
import com.example.order.messagequeue.KafkaProducer;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
     * @param singleOrderRequest: 사용자가 주문한 상품 정보
     */
    @PostMapping("/{userId}/order")
    public ResponseEntity<SingleOrderResponse> createOrder(@PathVariable("userId") String userId, @RequestBody
    SingleOrderRequest singleOrderRequest) throws OrderCreationException {
        // OrderRequestDto -> OrderDto
        ModelMapper modelMapper = new ModelMapper();
        OrderDto orderDto = modelMapper.map(singleOrderRequest, OrderDto.class);
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //연결 전략을 엄격하게 변경하여 같은 타입의 필드명 역시 같은 경우만 동작하도록 변경
        orderDto.setUserId(userId);
        orderDto.setTotalPrice(orderDto.getQuantity() * orderDto.getUnitPrice());
        orderDto.setPaymentMethod(singleOrderRequest.getPaymentMethod());



        // Order micro service쪽의 DB 내에 먼저 저장
        OrderDto responseOrderDto = orderService.createOrder(orderDto);

        /* Kafka에 특정 토픽으로 주문 정보 message send (by Kafka Producer)
            *해당 토픽은 Product topic이 구독하고 있기 때문에 Product topic에서 해당 message를 받을 수 있습니다.
        */
        kafkaProducer.send("update-quantity-product", orderDto);

        SingleOrderResponse singleOrderResponse = modelMapper.map(responseOrderDto, SingleOrderResponse.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(singleOrderResponse);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<List<MultipleOrderResponse>> createOrders(@PathVariable("userId") String userId,
                                                                    @RequestBody MultipleOrderRequest multipleOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(orderService.createOrders(userId, multipleOrderRequest));
    }




    /**
     * 특정 사용자의 주문목록을 조회하는 메서드
     * @param userId 조회하고자 하는 사용자의 userID
     * @return 특정 사용자의 주문목록
     */
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<SingleOrderResponse>> getOrdersByUserId(@PathVariable("userId") String userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderByUserId(userId));

    }


    @GetMapping("/orders")
    public ResponseEntity<List<SingleOrderResponse>> findAllOrders(){
        List<OrderDto> orders = orderService.getAllOrders();
        List<SingleOrderResponse> singleOrderRespons = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        orders.forEach(v -> singleOrderRespons.add(
                modelMapper.map(v, SingleOrderResponse.class)
        ));


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(singleOrderRespons);
    }
}
