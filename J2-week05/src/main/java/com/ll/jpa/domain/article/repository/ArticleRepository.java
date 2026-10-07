package com.ll.jpa.domain.article.repository;
import com.ll.jpa.domain.article.entity.Article;
import org.springframework.data.jpa.repository.*;
import java.util.List;
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @EntityGraph(attributePaths = "author")
    List<Article> findByTitleContainingOrderByIdAsc(String keyword);
    @EntityGraph(attributePaths = "author")
    List<Article> findByIdInOrderByTitleDescIdAsc(List<Long> ids);
    @EntityGraph(attributePaths = "author")
    List<Article> findByTitleAndBody(String title, String body);
}
