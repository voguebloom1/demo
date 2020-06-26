package com.example.demo.controller;

import com.example.demo.exception.CreateTokenFailException;
import com.example.demo.exception.InvalidUserException;
import com.example.demo.exception.ErrorResponse;
import com.example.demo.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class MoneyControllerAdvisor {

    @ExceptionHandler(InvalidParameterException.class)
    public @ResponseBody
    ErrorResponse spreadMoneyError(InvalidParameterException e){
        return ErrorResponse.builder()
                .success(false)
                .code("400")
                .message(e.getMessage()).build();
    }

    @ExceptionHandler(CreateTokenFailException.class)
    public @ResponseBody
    ErrorResponse spreadMoneyError(CreateTokenFailException e){
        return ErrorResponse.builder()
                .success(false)
                .code("500")
                .message(e.getMessage()).build();
    }

    @ExceptionHandler(InvalidUserException.class)
    public @ResponseBody
    ErrorResponse spreadMoneyError(InvalidUserException e){
        return ErrorResponse.builder()
                .success(false)
                .code("401")
                .message(e.getMessage()).build();
    }

}
