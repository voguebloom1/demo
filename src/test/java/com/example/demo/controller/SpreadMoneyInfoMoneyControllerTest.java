package com.example.demo.controller;

import com.example.demo.common.ErrorMessage;
import com.example.demo.entity.ReceivedInfo;
import com.example.demo.entity.SpreadMoneyInfo;
import com.example.demo.exception.CreateTokenFailException;
import com.example.demo.exception.InvalidParameterException;
import com.example.demo.exception.InvalidUserException;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.SpreadMoneyRequest;
import com.example.demo.service.SpreadMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(SpreadMoneyController.class)
@WebAppConfiguration
@Slf4j
public class SpreadMoneyInfoMoneyControllerTest {

    @Autowired
    MockMvc mvc;

    /*** 조회 API  ***/
    @MockBean()
    private SpreadMoneyService spreadMoneyService;

    // 정상 응답
    @Test
    void getSpreadMoneyStatus_OK() throws Exception{
        String token = "123";
        Date startDate = new Date();
        List<ReceivedInfo> list = Arrays.asList(
                ReceivedInfo.builder().money(200).state(true).spreadMoneyInfoId(1).userId("321").build()
        );
        SpreadMoneyInfo spreadMoneyInfo = SpreadMoneyInfo.builder()
                .id(1)
                .token("123").startDate(startDate).spreadMoney(5000).receivedMoney(3000)
                .roomId("testRoom").userId("123").receivedInfoList(list).build();


        //given
        given(spreadMoneyService.getSpreadMoneyStatus(any(BasicInfo.class), eq(token)))
            .willReturn(spreadMoneyInfo);

        //when
        final ResultActions actions = mvc.perform(get("/api/v1/svc/moneys/"+token+"/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room"))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.result.spreadMoneyInfo.spreadMoney", is(5000)))
                .andExpect(jsonPath("$.result.spreadMoneyInfo.receivedMoney", is(3000)))
                .andExpect(jsonPath("$.result.spreadMoneyInfo.receivedList[0].money", is(200)))
                .andExpect(jsonPath("$.result.spreadMoneyInfo.receivedList[0].userId", is("321")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).getSpreadMoneyStatus(any(BasicInfo.class), eq(token));
    }

    // 잘못된 토근으로 조회 시도
    @Test
    void getSpreadMoneyStatus_Invalid_Token() throws Exception{
        String token = "123";

        //given
        given(spreadMoneyService.getSpreadMoneyStatus(any(BasicInfo.class), eq(token)))
                .willThrow(new InvalidParameterException(ErrorMessage.INVALID_TOKEN));

        //when
        final ResultActions actions = mvc.perform(get("/api/v1/svc/moneys/"+token+"/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room"))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(ErrorMessage.INVALID_TOKEN)))
                .andExpect(jsonPath("$.code", is("400")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).getSpreadMoneyStatus(any(BasicInfo.class), eq(token));
    }

    // 다른 유저가 조회 시도
    @Test
    void getSpreadMoneyStatus_Invalid_User() throws Exception{
        String token = "123";

        //given
        given(spreadMoneyService.getSpreadMoneyStatus(any(BasicInfo.class), eq(token)))
                .willThrow(new InvalidUserException(ErrorMessage.INVALID_USER));

        //when
        final ResultActions actions = mvc.perform(get("/api/v1/svc/moneys/"+token+"/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room"))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(ErrorMessage.INVALID_USER)))
                .andExpect(jsonPath("$.code", is("401")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).getSpreadMoneyStatus(any(BasicInfo.class), eq(token));
    }

    // 조회 7일 지난 요청
    @Test
    void getSpreadMoneyStatus_Expired_Date() throws Exception{
        String token = "123";

        //given
        given(spreadMoneyService.getSpreadMoneyStatus(any(BasicInfo.class), eq(token)))
                .willThrow(new InvalidParameterException(ErrorMessage.EXPIRED_DATE));

        //when
        final ResultActions actions = mvc.perform(get("/api/v1/svc/moneys/"+token+"/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room"))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(ErrorMessage.EXPIRED_DATE)))
                .andExpect(jsonPath("$.code", is("400")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).getSpreadMoneyStatus(any(BasicInfo.class), eq(token));
    }

    /*** 받기 API  ***/
    @Test
    void getSpreadMoney_OK(){

    }

    @Test
    void getSpreadMoney_Duplicate_User(){

    }

    @Test
    void getSpreadMoney_Create_User(){

    }

    @Test
    void getSpreadMoney_Invalid_RoomId(){

    }

    @Test
    void getSpreadMoney_Expired_Date(){

    }


    /*** 뿌리기 API  ***/
    @Test
    void createSpreadMoney_OK() throws Exception{
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(23000, 2);

        //given
        given(spreadMoneyService.createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class)))
                .willReturn("322");

        //when
        final ResultActions actions = mvc.perform(post("/api/v1/svc/moneys")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room")
                .content(spreadMoneyRequest.toString()))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.result.token", is("322")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class));
    }

    @Test
    void createSpreadMoney_InvalidParameterException() throws Exception{
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(23000, 2);

        //given
        given(spreadMoneyService.createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class)))
                .willThrow(new InvalidParameterException("invalid parameters"));

        //when
        final ResultActions actions = mvc.perform(post("/api/v1/svc/moneys")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room")
                .content(spreadMoneyRequest.toString()))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("invalid parameters")))
                .andExpect(jsonPath("$.code", is("400")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class));
    }

    @Test
    void createSpreadMoney_CreateTokenFailException() throws Exception{
        SpreadMoneyRequest spreadMoneyRequest = new SpreadMoneyRequest(23000, 2);

        //given
        given(spreadMoneyService.createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class)))
                .willThrow(new CreateTokenFailException("The maximum count has been reached"));

        //when
        final ResultActions actions = mvc.perform(post("/api/v1/svc/moneys")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", "test_user")
                .header("X-ROOM-ID", "test_room")
                .content(spreadMoneyRequest.toString()))
                .andDo(print());

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The maximum count has been reached")))
                .andExpect(jsonPath("$.code", is("500")))
                .andDo(print());
        verify(spreadMoneyService, times(1)).createSpreadMoney(any(BasicInfo.class), any(SpreadMoneyRequest.class));
    }

}
