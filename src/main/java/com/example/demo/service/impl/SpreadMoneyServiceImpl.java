package com.example.demo.service.impl;

import com.example.demo.common.ErrorMessage;
import com.example.demo.entity.ReceivedInfo;
import com.example.demo.entity.SpreadMoneyInfo;
import com.example.demo.exception.CreateTokenFailException;
import com.example.demo.exception.InvalidUserException;
import com.example.demo.exception.InvalidParameterException;
import com.example.demo.model.BasicInfo;
import com.example.demo.model.SpreadMoneyRequest;
import com.example.demo.repository.ReceivedInfoRepository;
import com.example.demo.repository.SpreadMoneyInfoRepository;
import com.example.demo.service.SpreadMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpreadMoneyServiceImpl implements SpreadMoneyService {

    private final SpreadMoneyInfoRepository spreadMoneyInfoRepository;
    private final ReceivedInfoRepository receivedInfoRepository;

    private final char tokenCollection[] = new char[]{
            '1','2','3','4','5','6','7','8','9','0',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '!','@','#','$','%','^','&','*','(',')'};

    @Override
    public SpreadMoneyInfo getSpreadMoneyStatus(BasicInfo basicInfo, String token) {

        // 기본 토근과 룸의 뿌리기 정보.
        SpreadMoneyInfo spreadMoneyInfo = spreadMoneyInfoRepository.findByTokenAndRoomId(token, basicInfo.getRoomId());

        // 잘못되 정보 요청
        if(spreadMoneyInfo == null){
            throw new InvalidParameterException(ErrorMessage.INVALID_TOKEN);
        }

        // 생성한 유저가 아니라면
        if(!spreadMoneyInfo.getUserId().equals(basicInfo.getUserId())){
            throw new InvalidParameterException(ErrorMessage.INVALID_USER);
        }

        // 7일 동안 조회 가능
        if(isExpireDate(spreadMoneyInfo.getStartDate(), 604800)){
            throw new InvalidParameterException(ErrorMessage.EXPIRED_DATE);
        }

        return spreadMoneyInfo;
    }

    @Override
    public int getSpreadMoney(BasicInfo basicInfo, String token) {

        // 기본 토근과 룸의 뿌리기 정보.
        SpreadMoneyInfo spreadMoneyInfo = spreadMoneyInfoRepository.findByTokenAndRoomId(token, basicInfo.getRoomId());

        // 자신이 속한 대화방에서만 받을 수 있다.
        if(spreadMoneyInfo == null) {
            throw new InvalidParameterException(ErrorMessage.INVALID_ROOM_ID);
        }

        // 자신은 뿌리기한 건은 자신이 받을 수 없다.
        if(spreadMoneyInfo.getUserId().equals(basicInfo.getUserId())){
            throw new InvalidUserException(ErrorMessage.CREATE_USER);
        }

        //10분간만 유효하다.
        if(isExpireDate(spreadMoneyInfo.getStartDate(), 600)){
            throw new InvalidParameterException(ErrorMessage.EXPIRED_DATE);
        }

        // 해당 유저가 받았는지 체크 한다. (한번만 가능)
        ReceivedInfo ri = receivedInfoRepository
                .findByUserIdAndSpreadMoneyInfoId(basicInfo.getUserId(), spreadMoneyInfo.getId());
        if(ri != null) throw new InvalidUserException(ErrorMessage.DUPLICATE_USER);

        List<ReceivedInfo> receivedInfoList = receivedInfoRepository
                .findAllByStateAndSpreadMoneyInfoId(false, spreadMoneyInfo.getId());


        //받을게 없다면
        if(receivedInfoList.size() == 0){
            throw  new InvalidParameterException("Spread Money End");
        }

        int r = (int)(Math.random()*(receivedInfoList.size()));
        ReceivedInfo receivedInfo = receivedInfoList.get(r);
        receivedInfo.setState(true);
        receivedInfo.setUserId(basicInfo.getUserId());
        receivedInfoRepository.save(receivedInfo);

        int m = spreadMoneyInfo.getReceivedMoney() + receivedInfo.getMoney();
        spreadMoneyInfo.setReceivedMoney(m);
        spreadMoneyInfoRepository.save(spreadMoneyInfo);
        return receivedInfo.getMoney();
    }

    @Override
    public String createSpreadMoney(BasicInfo basicInfo, SpreadMoneyRequest spreadMoneyRequest) {

        // 혹시나 0원, 0명으로 보낼 경우
        if(spreadMoneyRequest.getMoney() == 0 || spreadMoneyRequest.getPeople() == 0)
            throw new InvalidParameterException(ErrorMessage.INVALID_ROOM_ID);

        // 3자리 문자열로 구성된 토큰을 생성한다. (채팅방마다 토큰은 겹칠 수 있다.)
        String token = getUniqueTokenInRoomId(basicInfo.getRoomId());

        // 인원수에 맞게 분배하는 로직을 만든다.
        List<ReceivedInfo> list = createReceivedInfo(spreadMoneyRequest.getMoney(), spreadMoneyRequest.getPeople());

        SpreadMoneyInfo spreadMoneyInfo = SpreadMoneyInfo.builder()
                .spreadMoney(spreadMoneyRequest.getMoney())
                .receivedMoney(0)
                .userId(basicInfo.getUserId())
                .roomId(basicInfo.getRoomId())
                .startDate(new Date())
                .token(token)
                .build();

        spreadMoneyInfoRepository.save(spreadMoneyInfo);
        spreadMoneyInfo.setReceivedInfoList(list);
        list.forEach(sm -> {
            sm.setSpreadMoneyInfoId(spreadMoneyInfo.getId());
        });
        spreadMoneyInfoRepository.save(spreadMoneyInfo);
        return token;
    }

    // 뿌릴 금액을 인원수에 맞게 저장한다.
    private List<ReceivedInfo> createReceivedInfo(int money, int people){

        List<ReceivedInfo> receivedInfoList = new ArrayList<>();
        int amount = 100, r, m, nm = money;
        for(int i=0; i<people-1; i++){
            r = (int)(Math.random()*(amount+1));
            m = money/100 * r;
            amount -= r;
            if(m >= money){
                m = money; amount = 0; nm = 0;
            }else{
                nm -= m;
            }
            receivedInfoList.add(ReceivedInfo.builder().money(m).state(false).userId("").build());

        }
        receivedInfoList.add(ReceivedInfo.builder().money(nm).build());
        return receivedInfoList;
    }


    private String getUniqueTokenInRoomId(String roomId){
        String token = "";
        int count = 0, max_count = 1000;
        // 최대 357,940개 생성 가능. 무한 루프 가능성 있어 1000개 까지만 확인.
        while(count < max_count){
            // token 생성
            token = generateToken();
            // 중복체크
            if(spreadMoneyInfoRepository.findByTokenAndRoomId(token, roomId) == null){
                break;
            }
            count++;
        }
        // 만약, max_count 되면 Exception 발생.
        if(count >= max_count) throw new CreateTokenFailException("The maximum count has been reached");
        return token;
    }


    private String generateToken(){
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int r = (int)(Math.random()*(tokenCollection.length));
            token.append(tokenCollection[r]);
        }
        return token.toString();
    }

    private boolean isExpireDate(Date start, long s){
        Date d = new Date();
        long diff = d.getTime() - start.getTime();
        return diff/1000 > s;
    }
}
