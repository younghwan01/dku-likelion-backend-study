package com.ll.jpa;
import com.ll.jpa.domain.member.service.MemberService;
import com.ll.jpa.domain.surl.service.SurlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import java.nio.file.Path;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
class PersistenceRestartTest {
    @TempDir Path directory;
    @Test void dataSurvivesApplicationRestart() {
        String url="jdbc:h2:file:"+directory.resolve("surl").toAbsolutePath()+";MODE=MySQL";
        long id;
        try (var first=open(url)) {
            var members=first.getBean(MemberService.class);
            var member=members.join("restart_user","password123","회원");
            var service=first.getBean(SurlService.class);
            id=service.write("재시작 테스트","https://example.com",member.id()).id();
            service.go(id);
        }
        try (var second=open(url)) {
            var saved=second.getBean(SurlService.class).find(id);
            assertThat(saved.url()).isEqualTo("https://example.com");
            assertThat(saved.count()).isEqualTo(1);
            assertThat(saved.authorUsername()).isEqualTo("restart_user");
        }
    }
    ConfigurableApplicationContext open(String url) {
        return new SpringApplicationBuilder(JpaApplication.class).profiles("test")
            .run("--spring.datasource.url="+url,"--spring.jpa.hibernate.ddl-auto=update","--server.port=0");
    }
}
