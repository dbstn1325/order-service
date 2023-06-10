package com.example.order.messagequeue;

import com.example.order.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    /**
     * OrderDto -> json 형태 변환
     * json -> String 형태 데이터를 Kafka 전달
     * Kafka - Order Producer를 통해 Product Consumer 호출
     * @param orderDto: 사용자 주문정보 (누가, 어떤상품을 원하는지)
     */
    public OrderDto send(String topic, OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            log.info("err4rrasd");
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("주문" + jsonInString);
        log.info("주문 micro service로부터 Kafka Producer를 통해 데이터를 전송 완료하였습니다.");

        return orderDto;
    }


    public OrderDto order(OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();   //json 형태로 변경하기 위해서 ObjectMapper를 사용하겠습니다.
        String jsonInString = "";                   //해당 string값의 모양새가 json형태로 표현되게끔 사용하겠습니다.
        try {
            jsonInString = mapper.writeValueAsString(orderDto);
            log.info("hasd" + jsonInString);
        } catch (JsonProcessingException ex) {  // json 변환과정 예외처리
            ex.printStackTrace();
            log.info("err4rrasd");
        }

        log.info("hasd" + jsonInString);

        // 해당 item 갯수 갱신 (갯수 감소)
        try{
            kafkaTemplate.send("update-quantity-product", jsonInString);
        } catch(KafkaException ex){
            log.info("성공!!!!");
//            ex.printStackTrace();
        }


        log.info("주문 micro service로부터 Kafka Producer를 통해 데이터를 전송 완료하였습니다.");

        return orderDto;
    }
}
