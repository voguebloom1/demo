package com.example.demo.controller;

import com.example.demo.exception.CreateTokenFailException;
import com.example.demo.exception.InvalidParameterException;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.SpreadMoneyRequest;
import com.example.demo.service.SpreadMoneyService;
import com.example.demo.service.impl.SpreadMoneyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void getSpreadMoneyStatus_OK(){

    }

    // 잘못된 토근으로 조회 시도
    @Test
    void getSpreadMoneyStatus_Invalid_Token(){

    }

    // 다른 유저가 조회 시도
    @Test
    void getSpreadMoneyStatus_Invalid_User(){

    }

    // 조회 7일 지난 요청
    @Test
    void getSpreadMoneyStatus_Invalid_Date(){

    }

    /*** 받기 API  ***/
    @Test
    void getSpreadMoney_OK(){

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
