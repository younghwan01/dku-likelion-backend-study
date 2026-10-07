package com.ll.jpa.domain.article.entity;
import com.ll.jpa.global.entity.BaseTime;
import com.ll.jpa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTime {
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String body;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;
    @Builder
    public Article(String title, String body, Member author) {
        this.title = title; this.body = body; this.author = author;
    }
    public void modify(String title, String body) { this.title = title; this.body = body; }
}
