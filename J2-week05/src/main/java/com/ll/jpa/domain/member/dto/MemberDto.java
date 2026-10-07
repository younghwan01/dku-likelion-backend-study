package com.ll.jpa.domain.member.dto;
import com.ll.jpa.domain.member.entity.Member;
public record MemberDto(Long id, String username, String nickname) {
    public static MemberDto from(Member m) { return new MemberDto(m.getId(), m.getUsername(), m.getNickname()); }
}
