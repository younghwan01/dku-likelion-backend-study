package com.ll.jpa;
import com.ll.jpa.domain.article.entity.Article;
import com.ll.jpa.domain.article.repository.ArticleRepository;
import com.ll.jpa.domain.article.service.ArticleService;
import com.ll.jpa.domain.member.repository.MemberRepository;
import com.ll.jpa.domain.member.service.MemberService;
import com.ll.jpa.global.exception.GlobalException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @ActiveProfiles("test") @Import(TransactionIntegrationTest.Config.class)
class TransactionIntegrationTest {
    @Autowired MemberService members;
    @Autowired MemberRepository memberRepository;
    @Autowired ArticleService articles;
    @Autowired ArticleRepository repository;
    @Autowired RollbackWork rollbackWork;
    @Autowired PlatformTransactionManager transactionManager;
    String name() { return "t"+UUID.randomUUID().toString().replace("-", "").substring(0,20); }
    @Test void runtimeExceptionRollsBackWholeUnitOfWork() {
        String username=name(); long before=repository.count();
        assertThatThrownBy(() -> rollbackWork.execute(username)).isInstanceOf(GlobalException.class);
        assertThat(memberRepository.findByUsername(username)).isEmpty();
        assertThat(repository.count()).isEqualTo(before);
    }
    @Test void managedEntityUsesDirtyCheckingAndAuditing() throws Exception {
        var member=members.join(name(),"password123","회원");
        var article=articles.write("원본","본문",member.id());
        var persisted = articles.find(article.id());
        Thread.sleep(20);
        var changed=articles.modify(article.id(),"수정","새 본문",member.id());
        assertThat(articles.find(article.id()).title()).isEqualTo("수정");
        assertThat(changed.createDate()).isEqualTo(persisted.createDate());
        assertThat(changed.modifyDate()).isAfter(article.modifyDate());
    }
    @Test void proxyIdIsAvailableWithoutInitializingMember() {
        var member=members.join(name(),"password123","회원");
        new TransactionTemplate(transactionManager).execute(status -> {
            var proxy=members.getReference(member.id());
            assertThat(org.hibernate.Hibernate.isInitialized(proxy)).isFalse();
            assertThat(proxy.getId()).isEqualTo(member.id());
            assertThat(org.hibernate.Hibernate.isInitialized(proxy)).isFalse();
            assertThat(proxy.getNickname()).isEqualTo("회원");
            assertThat(org.hibernate.Hibernate.isInitialized(proxy)).isTrue();
            return null;
        });
    }
    @TestConfiguration static class Config {
        @Bean RollbackWork rollbackWork(MemberService members, ArticleService articles) { return new RollbackWork(members,articles); }
    }
    static class RollbackWork {
        private final MemberService members; private final ArticleService articles;
        RollbackWork(MemberService m,ArticleService a) { members=m;articles=a; }
        @Transactional public void execute(String username) {
            var member=members.join(username,"password123","회원");
            articles.write("롤백할 글","본문",member.id());
            throw new GlobalException(HttpStatus.BAD_REQUEST,"전체 취소");
        }
    }
}
