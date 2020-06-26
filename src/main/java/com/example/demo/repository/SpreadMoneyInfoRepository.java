package com.example.demo.repository;

import com.example.demo.entity.SpreadMoneyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpreadMoneyInfoRepository extends JpaRepository<SpreadMoneyInfo, Integer> {

    SpreadMoneyInfo findByTokenAndRoomId(String Token, String RoomId);

}
