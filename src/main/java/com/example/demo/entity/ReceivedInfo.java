package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "receive_info")
public class ReceivedInfo extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="spread_money_info_id")
    private int spreadMoneyInfoId;

    private int money;
    private String userId;
    private boolean state;

}
