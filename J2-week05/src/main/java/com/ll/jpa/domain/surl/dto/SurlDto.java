package com.ll.jpa.domain.surl.dto;
import com.ll.jpa.domain.surl.entity.Surl;
import java.time.LocalDateTime;
public record SurlDto(Long id, String body, String url, long count, Long authorId, String authorUsername,
                      String shortUrl, LocalDateTime createDate, LocalDateTime modifyDate) {
    public static SurlDto from(Surl s) {
        return new SurlDto(s.getId(), s.getBody(), s.getUrl(), s.getCount(), s.getAuthor().getId(),
            s.getAuthor().getUsername(), "/g/"+s.getId(), s.getCreateDate(), s.getModifyDate());
    }
}
