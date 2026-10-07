package com.ll.jpa.global.init;
import com.ll.jpa.domain.member.service.MemberService;
import com.ll.jpa.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component @Profile("dev") @RequiredArgsConstructor
public class DevInitData implements ApplicationRunner {
    private final MemberService members;
    private final ArticleService articles;
    public void run(ApplicationArguments args) {
        // 명시적으로 dev 프로파일을 선택한 경우에만 실행합니다.
        // 재시작 때 자동 생성하지 않도록 게시물 개수를 먼저 확인합니다.
        if (articles.count() > 0) return;
        createMember("user1", "회원1");
        createMember("user2", "회원2");
        var user1 = members.findByUsername("user1").orElseThrow();
        var user2 = members.findByUsername("user2").orElseThrow();
        articles.write("안녕 JPA", "첫 번째 게시물", user1.id());
        articles.write("안녕 Spring", "두 번째 게시물", user1.id());
        articles.write("트랜잭션", "세 번째 게시물", user2.id());
        articles.write("영속성", "네 번째 게시물", user2.id());
    }
    private void createMember(String username, String nickname) {
        try { members.join(username, "password123", nickname); }
        catch (com.ll.jpa.global.exception.GlobalException ex) {
            if (ex.getStatus() != org.springframework.http.HttpStatus.CONFLICT) throw ex;
        }
    }
}
