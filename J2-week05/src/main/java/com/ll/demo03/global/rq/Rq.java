package com.ll.demo03.global.rq;

import com.ll.demo03.domain.member.member.entity.Member;
import com.ll.demo03.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final MemberService memberService;

    public Member getMember() {
        // 실제 로그인 기능은 구현하지 않고 1번 회원의 프록시를 사용합니다.
        return memberService.getReferenceById(1L);
    }
}
