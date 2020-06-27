package com.example.demo.model;

import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ResponseModel {

    private boolean success;
    private Map<String, Object> result;

}
