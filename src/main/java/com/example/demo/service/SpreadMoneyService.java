package com.example.demo.service;

import com.example.demo.entity.SpreadMoneyInfo;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.SpreadMoneyRequest;

public interface SpreadMoneyService {

    SpreadMoneyInfo getSpreadMoneyStatus(BasicInfo basicInfo, String token);

    long getSpreadMoney(BasicInfo basicInfo, String token);

    String createSpreadMoney(BasicInfo basicInfo ,SpreadMoneyRequest spreadMoneyRequest);

}
