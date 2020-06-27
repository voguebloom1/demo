package com.example.demo.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpreadMoneyRequest {
    private long money;
    private int people;
    @Override
    public String toString() {
        return "{"
                + "\"money\":\"" + money + "\""
                + ", \"people\":\"" + people + "\""
                + "}";
    }
}
