package com.example.demo.controller;

import com.example.demo.entity.SpreadMoneyInfo;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.ResponseModel;
import com.example.demo.model.SpreadMoneyRequest;
import com.example.demo.service.SpreadMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/svc")
public class SpreadMoneyController {

    private final SpreadMoneyService spreadMoneyService;

    /** 조회 API
     *  - token을 요청 값으로 받는다.
     *  - token에 해당하는 뿌리기 건의 응답값을 내려준다.
     *  - 뿌린시각, 부린 금액, 받기 완료된 금액, 받기 완료된 정보 ( 받은 금액, 받은 사용자 아이디 리스트 )
     *  - 뿌린 사람 자신만 조회가 가능, 다른 사람이면 유효하지 않은 token에 대해 실패 응답이 내려가야 한다.
     *  - 뿌린 건에 대한 조회는 7일 동안 할 수 있다.
     * */
    @GetMapping("/moneys/{token}/status")
    public ResponseEntity<Object> getSpreadMoneyStatus(@RequestAttribute BasicInfo basicInfo,
                                                       @PathVariable String token){
        SpreadMoneyInfo spreadMoneyInfo = spreadMoneyService.getSpreadMoneyStatus(basicInfo, token);
        Map<String, Object> result = new HashMap<>();
        result.put("spreadMoneyInfo", spreadMoneyInfo.convertSpreadMoneyInfoResponse());
        return ResponseEntity.ok().body(new ResponseModel(true, result));
    }


    /** 받기 API
     *
     * */
    @GetMapping("/moneys/{token}/receive")
    public ResponseEntity<Object> getSpreadMoney(@RequestAttribute BasicInfo basicInfo,
                                                 @PathVariable String token){
        int money = spreadMoneyService.getSpreadMoney(basicInfo, token);
        Map<String, Object> result = new HashMap<>();
        result.put("money", money);
        return ResponseEntity.ok().body(new ResponseModel(true, result));
    }

    /** 뿌리기 API
     *  - 뿌릴 금액, 뿌릴 인원 요청 값으로 받는다.
     * */
    @PostMapping("/moneys")
    public ResponseEntity<Object> createSpreadMoney(@RequestBody SpreadMoneyRequest spreadMoneyRequest,
                                                    @RequestAttribute BasicInfo basicInfo){

        String token = spreadMoneyService.createSpreadMoney(basicInfo, spreadMoneyRequest);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return ResponseEntity.ok().body(new ResponseModel(true, result));
    }





}
