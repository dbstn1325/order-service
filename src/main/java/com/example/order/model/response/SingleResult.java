package com.example.order.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SingleResult<T> extends CommonResult {
    private T data;
}
