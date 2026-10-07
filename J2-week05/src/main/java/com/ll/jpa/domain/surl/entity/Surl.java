package com.ll.jpa.domain.surl.entity;
import com.ll.jpa.global.entity.BaseTime;
import com.ll.jpa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Surl extends BaseTime {
    @Column(nullable = false, length = 200) private String body;
    @Column(nullable = false, length = 2048) private String url;
    @Column(nullable = false) private long count;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false) private Member author;
    @Builder
    public Surl(String body, String url, Member author) { this.body = body; this.url = url; this.author = author; }
    public void increaseCount() { count++; }
}
