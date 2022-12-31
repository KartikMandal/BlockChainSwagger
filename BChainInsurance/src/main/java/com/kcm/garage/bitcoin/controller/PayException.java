package com.kcm.garage.bitcoin.controller;

public class PayException extends Exception {

    public PayException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
