package com.example.demo.entity;

import com.example.demo.model.ReceivedInfoResponse;
import com.example.demo.model.SpreadMoneyInfoResponse;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "spread_money_info")
public class SpreadMoneyInfo extends BasicEntity {

    // DB의 autoIncrement를 사용한다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;
    private String roomId;
    private String token;

    private Date startDate;
    private int  spreadMoney;
    private int  receivedMoney;

    // 받은 사용자 정보.
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="spread_money_info_id")
    private Collection<ReceivedInfo> receivedInfoList;

    public SpreadMoneyInfoResponse convertSpreadMoneyInfoResponse(){

        List<ReceivedInfoResponse> list = new ArrayList<>();
        receivedInfoList.forEach( ri -> {
            if(ri.isState()) list.add(new ReceivedInfoResponse(ri.getMoney(), ri.getUserId()));
        });

        return SpreadMoneyInfoResponse.builder()
                .startDate(startDate)
                .spreadMoney(spreadMoney)
                .receivedMoney(receivedMoney)
                .ReceivedList(list).build();
    }

}
