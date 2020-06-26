package com.example.demo.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpreadMoneyInfoResponse {
    private Date startDate;
    private int spreadMoney;
    private int receivedMoney;
    private List<ReceivedInfoResponse> ReceivedList;
}
