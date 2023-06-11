package com.example.order.messagequeue;

import com.example.order.dto.CartDto;
import com.example.order.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    /**
     * OrderDto -> json 형태 변환
     * json -> String 형태 데이터를 Kafka 전달
     * Kafka - Order Producer를 통해 Product Consumer 호출
     * @param orderDto: 사용자 주문정보 (누가, 어떤상품을 원하는지)
     */
    public OrderDto send(String topic, OrderDto orderDto) {
//        ObjectMapper mapper = new ObjectMapper(); // json 형태로 변경하기 위해 ObjectMapper 사용
        String jsonInString = "";                 // 해당 string값이 json형태로 표현되게끔 사용하겠습니다.
        try {
            jsonInString = objectMapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            log.info("err4rrasd");
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("주문 목록: " + jsonInString);
        log.info("주문 micro service로부터 Kafka Producer를 통해 데이터를 전송 완료하였습니다.");

        return orderDto;
    }

    public void removeCart(String topic, List<CartDto> cartDtos) {

        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(cartDtos);
        } catch (JsonProcessingException e) {
            log.info("Cart microservice producer error");
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Cart Send - " + jsonInString);
    }

}
