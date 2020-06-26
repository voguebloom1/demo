package com.example.demo.exception;

public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String m){
        super(m);
    }
}
