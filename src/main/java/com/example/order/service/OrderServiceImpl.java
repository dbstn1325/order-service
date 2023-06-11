package com.example.order.service;

import com.example.order.dto.*;
import com.example.order.entity.Order;
import com.example.order.exception.OrderCreationException;
import com.example.order.messagequeue.KafkaProducer;
import com.example.order.repository.OrderRepository;
import com.example.order.utils.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;
    private final ModelMapperUtil modelMapperUtil;

    /**
     * 단건 주문 정보를 저장
     *
     * @param orderDto 저장할 주문 정보
     * @return 저장된 주문 정보
     */
    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) throws OrderCreationException {
        Order order = modelMapperUtil.convertToEntity(orderDto);

        try {
            orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            // 데이터 무결성 오류를 처리
            log.error("데이터 무결성 오류가 발생했습니다.", e);
            throw new OrderCreationException("주문 생성 중 데이터 무결성 문제가 발생했습니다.");
        } catch (Exception e) {
            // 기타 오류를 처리합니다.
            log.error("주문 생성 중 알 수 없는 오류가 발생했습니다.", e);
            throw new OrderCreationException("주문 생성 중 알 수 없는 오류가 발생했습니다.");
        }

        return modelMapperUtil.convertToDto(order);
    }


    /**
     * 주어진 사용자 ID와 주문 요청 정보를 바탕으로 여러 주문 생성 & 저장
     * 장바구니 정보도 Kafka에 전송
     *
     * @param userId 사용자 ID
     * @param multipleOrderRequest 주문 요청 정보
     * @return 생성된 주문 목록
     */
    @Override
    public List<MultipleOrderResponse> createOrders(String userId, MultipleOrderRequest multipleOrderRequest) {
        sendCartInfosToKafka(multipleOrderRequest);
        List<Order> orders = createOrdersFromRequest(userId, multipleOrderRequest);
        List<Order> savedOrders = orderRepository.saveAll(orders);

        return convertToOrderResponse2List(savedOrders);
    }

    /**
     * 주어진 주문 요청 정보에 있는 장바구니 정보를 Kafka에 전송
     *
     * @param multipleOrderRequest 주문 요청 정보
     */
    private void sendCartInfosToKafka(MultipleOrderRequest multipleOrderRequest) {
        List<CartDto> cartInfos = multipleOrderRequest.getItems().stream()
                .map(item -> new CartDto(item.getCartId()))
                .collect(Collectors.toList());

        kafkaProducer.removeCart("cart-info-topic", cartInfos);
    }

    /**
     * 주어진 사용자 ID와 주문 요청 정보를 이용해서 주문 목록 생성
     *
     * @param userId 사용자 ID
     * @param multipleOrderRequest 주문 요청 정보
     * @return 생성된 주문 목록
     */
    private List<Order> createOrdersFromRequest(String userId, MultipleOrderRequest multipleOrderRequest) {
        return multipleOrderRequest.getItems().stream()
                .map(item -> createSingleOrder(userId, item, multipleOrderRequest.getPaymentMethod()))
                .collect(Collectors.toList());
    }

    /**
     * 주어진 사용자 ID, 항목, 결제 방법을 사용해서 개별 주문을 생성
     * 해당 주문 정보를 Kafka에 전송
     *
     * @param userId 사용자 ID
     * @param item 주문 항목
     * @param paymentMethod 결제 방법
     * @return 생성된 주문
     */
    private Order createSingleOrder(String userId, ItemDto item, String paymentMethod) {
        OrderDto orderDto = modelMapperUtil.convertItemToOrderDto(item);
        orderDto.setUserId(userId);
        orderDto.setPaymentMethod(paymentMethod);

        kafkaProducer.send("update-quantity-product", orderDto);

        return modelMapperUtil.convertToEntity(orderDto);
    }

    /**
     * 주어진 주문 목록을 OrderResponse2 목록으로 변환
     *
     * @param orders 주문 목록
     * @return 변환된 OrderResponse2 목록
     */
    private List<MultipleOrderResponse> convertToOrderResponse2List(List<Order> orders) {
        return orders.stream()
                .map(modelMapperUtil::convertToOrderResponse2)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 사용자 ID에 해당하는 주문 목록을 조회해서 반환
     *
     * @param userId 사용자 ID
     * @return 사용자의 주문 목록
     */
    @Override
    public List<SingleOrderResponse> getOrderByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        List<SingleOrderResponse> result = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        orders.forEach(v->
                result.add(modelMapper.map(v, SingleOrderResponse.class)
                ));

        return result;
    }



    /**
     * 주어진 주문 ID에 해당하는 주문을 조회해서 반환
     *
     * @param orderId 주문 ID
     * @return 주문 ID에 해당하는 주문 정보
     */
    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Order order = orderRepository.findByOrderId(orderId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);

        return orderDto;
    }

    /**
     * 모든 주문들을 조회해서 반환
     *
     * @return 모든 주문들의 목록
     */
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
