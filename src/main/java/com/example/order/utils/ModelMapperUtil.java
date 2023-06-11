package com.example.order.utils;

import com.example.order.dto.*;
import com.example.order.entity.Order;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperUtil {
    private final ModelMapper modelMapper;

    public ModelMapperUtil() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    public Order convertToEntity(OrderDto orderDto) {
        return modelMapper.map(orderDto, Order.class);
    }

    public OrderDto convertItemToOrderDto(ItemDto item) {
        return modelMapper.map(item, OrderDto.class);
    }

    public MultipleOrderResponse convertToOrderResponse2(Order order) {
        return modelMapper.map(order, MultipleOrderResponse.class);
    }
}
