//package com.example.order.messagequeue;
//
//import com.example.order.repository.OrderRepository;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.KafkaException;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.listener.ListenerExecutionFailedException;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class KafkaConsumer {
//
//    private final OrderRepository orderRepository;
//
//    @KafkaListener(topics = "out-of-stock-notification-topic")
//    public void listenOrderOutOfStockMessage(String kafkaMessage) {
//
//        log.info("[Order] 상품 재고 부족 알림 메시지: {}", kafkaMessage);
////        throw new KafkaException("상품 재고 부족 알림 메시지");
//    }
//
//    @KafkaListener(topics = "order-confirmation-topic")
//    public Map<Object, Object> listenOrderCompletionMessages(String kafkaMessage){
//        // 주문 완료 알림 메세지 수신
////        log.info("[Order] 주문 완료 알림 메시지: {}", kafkaMessage);
//
//
//        Map<Object, Object> map = getMessageToJson(kafkaMessage);
//        log.info("[Order] 주문 완료 알림 메시지: {}", map);
//
//        // 주문 데이터베이스 저장
//
//        // 사용자에게 주문 알림 발송
//        return map;
//    }
//
//    /**
//     * String -> json 형태 변환
//     * @param kafkaMessage
//     * @return
//     */
//
//    private static Map<Object, Object> getMessageToJson(String kafkaMessage) {
//        Map<Object, Object> map = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//
//        try{
//            //String값으로 전달받은 메시지를 우리가 원하는 데이터타입(Json)으로 변경해줍니다.
//            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
//        } catch(JsonProcessingException ex){
//            ex.printStackTrace();
//        }
//        return map;
//    }
//}
