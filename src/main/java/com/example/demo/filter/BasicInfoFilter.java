package com.example.demo.filter;

import com.example.demo.model.BasicInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class BasicInfoFilter extends OncePerRequestFilter {

    private final String HEADER_KEY_USER_ID = "X-USER-ID";
    private final String HEADER_KEY_ROOM_ID = "X-ROOM-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("[Request] method : {}, uri : {}", request.getMethod(), request.getRequestURI());

        String userId = request.getHeader(HEADER_KEY_USER_ID);
        String roomId = request.getHeader(HEADER_KEY_ROOM_ID);

        // 정상적인 정보만 왔을때 처리 한다.
        if(isInvalidBasicInfo(userId, roomId)){
            log.warn("Invalid userId : {}, roomId : {}", userId, roomId);
            // 나중에 Room, 유저 권한 체크시 사용
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid UserId, RoomId");
        }else{
            BasicInfo basicInfo = new BasicInfo(userId, roomId);
            request.setAttribute("basicInfo", basicInfo);
        }

        filterChain.doFilter(request, response);


    }

   private boolean isInvalidBasicInfo(String userId, String roomId){
        if(StringUtils.isEmpty(userId)) return true;
        if(StringUtils.isEmpty(roomId)) return true;
        return false;
   }


}
