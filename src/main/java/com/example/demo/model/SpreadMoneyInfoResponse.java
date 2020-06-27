package com.example.demo.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SpreadMoneyInfoResponse {
    private Date startDate;
    private long spreadMoney;
    private long receivedMoney;
    private List<ReceivedInfoResponse> ReceivedList;
}
