package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceivedInfoResponse {
    private int money;
    private String userId;
}
