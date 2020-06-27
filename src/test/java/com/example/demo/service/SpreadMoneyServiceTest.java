package com.example.demo.service;

import com.example.demo.common.ErrorMessage;
import com.example.demo.entity.ReceivedInfo;
import com.example.demo.entity.SpreadMoneyInfo;
import com.example.demo.exception.CreateTokenFailException;
import com.example.demo.exception.InvalidParameterException;
import com.example.demo.exception.InvalidUserException;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.SpreadMoneyRequest;
import com.example.demo.repository.ReceivedInfoRepository;
import com.example.demo.repository.SpreadMoneyInfoRepository;
import com.example.demo.service.impl.SpreadMoneyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest
public class SpreadMoneyServiceTest {

    @Autowired
    private SpreadMoneyServiceImpl spreadMoneyService;

    @MockBean
    private SpreadMoneyInfoRepository spreadMoneyInfoRepository;

    @MockBean
    private ReceivedInfoRepository receivedInfoRepository;

    private BasicInfo createBasicInfo;
    private BasicInfo userBasicInfo;
    private String token;
    private SpreadMoneyInfo spreadMoneyInfo;
    private List<ReceivedInfo> list;

    @BeforeEach
    public void setUp(){
        createBasicInfo = new BasicInfo(123456, "test_room");
        userBasicInfo = new BasicInfo(456123, "test_room");
        token = "$1@";

        // 기본 SpreadMoney 정보
        Date startDate = new Date();
        list = Arrays.asList(
                ReceivedInfo.builder().money(200).state(true).spreadMoneyInfoId(0).userId(0).build(),
                ReceivedInfo.builder().money(3000).state(true).spreadMoneyInfoId(0).userId(0).build(),
                ReceivedInfo.builder().money(500).state(true).spreadMoneyInfoId(0).userId(0).build(),
                ReceivedInfo.builder().money(1000).state(true).spreadMoneyInfoId(0).userId(0).build(),
                ReceivedInfo.builder().money(300).state(true).spreadMoneyInfoId(0).userId(0).build()
        );
        spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token(token).startDate(startDate).spreadMoney(5000).receivedMoney(0)
                .roomId(createBasicInfo.getRoomId()).userId(createBasicInfo.getUserId())
                .receivedInfoList(list).build();
    }

    @Test
    public void getSpreadMoney_OK(){
        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);
        given(receivedInfoRepository.findAllByStateAndSpreadMoneyInfoId(false, 1))
                .willReturn(list);
        //when
        long money = spreadMoneyService.getSpreadMoney(userBasicInfo, token);
        //then
        assertThat(money, is(oneOf(200L,3000L,500L,1000L,300L)));
    }

    @Test
    public void getSpreadMoney_NULL_Error(){
        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(null);
        //then
        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoney(userBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.INVALID_ROOM_ID);
    }

    @Test
    public void getSpreadMoney_CreateUser_Error(){
        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, createBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);
        //then
        String message = assertThrows(InvalidUserException.class, () -> {
            spreadMoneyService.getSpreadMoney(createBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.CREATE_USER);
    }

    @Test
    public void getSpreadMoney_Expired_Date_Error(){
        long time = new Date().getTime();
        spreadMoneyInfo.setStartDate(new Date(time-601*1000));
        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);
        //then
        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoney(userBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.EXPIRED_DATE);
    }

    @Test
    public void getSpreadMoney_Duplicate_User(){

        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);
        given(receivedInfoRepository.findByUserIdAndSpreadMoneyInfoId(userBasicInfo.getUserId(),
                spreadMoneyInfo.getId())).willReturn(new ReceivedInfo());

        //then
        String message = assertThrows(InvalidUserException.class, () -> {
            spreadMoneyService.getSpreadMoney(userBasicInfo, token);
        }).getMessage();
        assertEquals(message, ErrorMessage.DUPLICATE_USER);
    }

    @Test
    public void getSpreadMoney_SPREAD_MONEU_END(){

        //given
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);
        given(receivedInfoRepository.findByUserIdAndSpreadMoneyInfoId(userBasicInfo.getUserId(),
                spreadMoneyInfo.getId())).willReturn(null);
        given(receivedInfoRepository.findAllByStateAndSpreadMoneyInfoId(false,
                spreadMoneyInfo.getId())).willReturn(new ArrayList<>());

        //then
        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoney(userBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.SPREAD_MONEY_END);

    }

    @Test
    public void createSpreadMoney_OK(){

        //given
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(1321, 3);
        spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token(token).startDate(new Date()).spreadMoney(spreadMoneyRequest.getMoney()).receivedMoney(0)
                .roomId(createBasicInfo.getRoomId()).userId(createBasicInfo.getUserId())
                .build();

        String token = spreadMoneyService.createSpreadMoney(createBasicInfo, spreadMoneyRequest);
        assertTrue(token.length() == 3);

    }

    @Test
    public void createSpreadMoney_Invalid_Money(){

        //given
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(0, 3);
        spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token(token).startDate(new Date()).spreadMoney(spreadMoneyRequest.getMoney()).receivedMoney(0)
                .roomId(createBasicInfo.getRoomId()).userId(createBasicInfo.getUserId())
                .build();

        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.createSpreadMoney(createBasicInfo, spreadMoneyRequest);
        }).getMessage();

        assertEquals(message, ErrorMessage.INVALID_PARAMETERS);
    }

    @Test
    public void createSpreadMoney_Invalid_People(){

        //given
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(1000, 0);
        spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token(token).startDate(new Date()).spreadMoney(spreadMoneyRequest.getMoney()).receivedMoney(0)
                .roomId(createBasicInfo.getRoomId()).userId(createBasicInfo.getUserId())
                .build();

        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.createSpreadMoney(createBasicInfo, spreadMoneyRequest);
        }).getMessage();

        assertEquals(message, ErrorMessage.INVALID_PARAMETERS);
    }

    @Test
    public void createSpreadMoney_TOKEN_MAXIMUM_COUNT(){

        //given
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(1000, 5);
        spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token(token).startDate(new Date()).spreadMoney(spreadMoneyRequest.getMoney()).receivedMoney(0)
                .roomId(createBasicInfo.getRoomId()).userId(createBasicInfo.getUserId())
                .build();
        given(spreadMoneyInfoRepository.findByTokenAndRoomId(anyString(), eq(createBasicInfo.getRoomId())))
                .willReturn(new SpreadMoneyInfo());


        String message = assertThrows(CreateTokenFailException.class, () -> {
            spreadMoneyService.createSpreadMoney(createBasicInfo, spreadMoneyRequest);
        }).getMessage();

        assertEquals(message, ErrorMessage.TOKEN_MAXIMUM_COUNT);
    }

    @Test
    public void getSpreadMoneyStatus_OK(){

        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, createBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);

        SpreadMoneyInfo smi = spreadMoneyService.getSpreadMoneyStatus(createBasicInfo, token);

        assertEquals(spreadMoneyInfo.getId(), smi.getId());
        assertEquals(spreadMoneyInfo.getStartDate(), smi.getStartDate());
        assertEquals(spreadMoneyInfo.getUserId(), smi.getUserId());
    }

    @Test
    public void getSpreadMoneyStatus_Invalid_Token(){

        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, createBasicInfo.getRoomId()))
                .willReturn(null);

        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoneyStatus(createBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.INVALID_TOKEN);
    }

    @Test
    public void getSpreadMoneyStatus_Invalid_User(){

        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, userBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);

        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoneyStatus(userBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.INVALID_USER);
    }

    @Test
    public void getSpreadMoneyStatus_Expired_Date(){
        long time = new Date().getTime();
        spreadMoneyInfo.setStartDate(new Date(time-604801*1000));

        given(spreadMoneyInfoRepository.findByTokenAndRoomId(token, createBasicInfo.getRoomId()))
                .willReturn(spreadMoneyInfo);

        String message = assertThrows(InvalidParameterException.class, () -> {
            spreadMoneyService.getSpreadMoneyStatus(createBasicInfo, token);
        }).getMessage();

        assertEquals(message, ErrorMessage.EXPIRED_DATE);
    }


}
