package com.ll.jpa.domain.article.dto;
import com.ll.jpa.domain.article.entity.Article;
import java.time.LocalDateTime;
public record ArticleDto(Long id, String title, String body, Long authorId, String authorUsername,
                         LocalDateTime createDate, LocalDateTime modifyDate) {
    public static ArticleDto from(Article a) {
        return new ArticleDto(a.getId(), a.getTitle(), a.getBody(), a.getAuthor().getId(),
            a.getAuthor().getUsername(), a.getCreateDate(), a.getModifyDate());
    }
}
