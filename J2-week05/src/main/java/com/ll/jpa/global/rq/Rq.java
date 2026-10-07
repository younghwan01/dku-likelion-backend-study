package com.ll.jpa.global.rq;
import com.ll.jpa.domain.member.entity.Member;
import com.ll.jpa.domain.member.service.MemberService;
import com.ll.jpa.global.exception.GlobalException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
@Component @RequestScope @RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request;
    public Long getMemberId() {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("memberId") instanceof Long id))
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        return id;
    }
}
