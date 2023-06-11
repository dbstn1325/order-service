package com.example.order.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonResponse {

    SUCCESS(0, "Success"),
    FAIL(-1, "Fail");

    private int code;
    private String message;
}
