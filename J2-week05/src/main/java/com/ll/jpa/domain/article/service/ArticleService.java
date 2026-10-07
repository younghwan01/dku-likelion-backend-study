package com.ll.jpa.domain.article.service;
import com.ll.jpa.domain.article.entity.Article;
import com.ll.jpa.domain.article.dto.ArticleDto;
import com.ll.jpa.domain.article.repository.ArticleRepository;
import com.ll.jpa.domain.member.entity.Member;
import com.ll.jpa.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository repository;
    private final com.ll.jpa.domain.member.service.MemberService members;
    @Transactional
    public ArticleDto write(String title, String body, Long authorId) {
        return ArticleDto.from(repository.saveAndFlush(Article.builder().title(title).body(body).author(members.getReference(authorId)).build()));
    }
    public List<ArticleDto> search(String keyword) {
        return repository.findByTitleContainingOrderByIdAsc(keyword).stream().map(ArticleDto::from).toList();
    }
    public List<ArticleDto> findByIds(List<Long> ids) {
        return repository.findByIdInOrderByTitleDescIdAsc(ids).stream().map(ArticleDto::from).toList();
    }
    public List<ArticleDto> findExact(String title, String body) {
        return repository.findByTitleAndBody(title, body).stream().map(ArticleDto::from).toList();
    }
    public long count() { return repository.count(); }
    public ArticleDto find(Long id) { return ArticleDto.from(findEntity(id)); }
    @Transactional
    public ArticleDto modify(Long id, String title, String body, Long memberId) {
        Article article = findEntity(id);
        checkAuthor(article, memberId);
        article.modify(title, body); // 영속 엔티티: save() 없이 더티 체킹으로 UPDATE됩니다.
        repository.flush(); // 응답 DTO에 최신 modifyDate를 담기 위해 flush합니다.
        return ArticleDto.from(article);
    }
    @Transactional
    public void delete(Long id, Long memberId) {
        Article article = findEntity(id);
        checkAuthor(article, memberId);
        repository.delete(article);
    }
    private Article findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));
    }
    private void checkAuthor(Article article, Long memberId) {
        if (!article.getAuthor().getId().equals(memberId))
            throw new GlobalException(HttpStatus.FORBIDDEN, "작성자만 변경할 수 있습니다.");
    }
}
