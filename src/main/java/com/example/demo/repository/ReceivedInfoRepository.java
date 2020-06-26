package com.example.demo.repository;

import com.example.demo.entity.ReceivedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceivedInfoRepository extends JpaRepository<ReceivedInfo, Integer> {

    ReceivedInfo findByUserIdAndSpreadMoneyInfoId(String userid, int spreadMoneyInfoId);

    List<ReceivedInfo> findAllByStateAndSpreadMoneyInfoId(boolean state, int spreadMoneyInfoId);

}
