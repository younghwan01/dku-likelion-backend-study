package com.ll.jpa.domain.member.entity;
import com.ll.jpa.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {
    @Column(nullable = false, unique = true, length = 30)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, length = 30)
    private String nickname;
    @Builder
    public Member(String username, String password, String nickname) {
        this.username = username; this.password = password; this.nickname = nickname;
    }
}
