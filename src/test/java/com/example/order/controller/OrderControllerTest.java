package com.example.order.controller;

import com.example.order.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;


    @Test
    @DisplayName("order service에서 createOrder() 요청시 응답이 와야합니다.")
    void createOrder() throws Exception{
        //given
        String userId = "71fd2a16-0a04-476f-8e69-e64dffd3628e";

        OrderRequest orderRequest = new OrderRequest("PRODUCT-001", 1, 379000);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(orderRequest);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/order-service/" + userId + "/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/product-service/products")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }
}