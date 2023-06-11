package com.example.order.exception;


public class OrderCreationException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrderCreationException() {
        super();
    }

    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

