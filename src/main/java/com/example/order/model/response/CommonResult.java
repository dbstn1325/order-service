package com.example.order.model.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Common Response Model
 */

@Getter
@Setter
public class CommonResult {

    // Response Success (True / False)
    private boolean success;

    // Status Code (0 >= 정상, <0 비정상)
    private int code;

    // Response Message
    private String message;
}
