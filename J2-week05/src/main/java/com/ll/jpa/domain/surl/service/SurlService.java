package com.ll.jpa.domain.surl.service;
import com.ll.jpa.domain.surl.entity.Surl;
import com.ll.jpa.domain.surl.dto.SurlDto;
import com.ll.jpa.domain.surl.repository.SurlRepository;
import com.ll.jpa.domain.member.entity.Member;
import com.ll.jpa.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.*;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class SurlService {
    private final SurlRepository repository;
    private final com.ll.jpa.domain.member.service.MemberService members;
    @Transactional
    public SurlDto write(String body, String url, Long authorId) {
        validateUrl(url);
        return SurlDto.from(repository.saveAndFlush(Surl.builder().body(body).url(url).author(members.getReference(authorId)).build()));
    }
    public List<SurlDto> findAll() { return repository.findAllByOrderByIdAsc().stream().map(SurlDto::from).toList(); }
    public SurlDto find(Long id) {
        return SurlDto.from(repository.findById(id).orElseThrow(() -> missing()));
    }
    @Transactional
    public String go(Long id) {
        // 동시에 요청해도 조회수가 유실되지 않도록 해당 행을 잠급니다.
        Surl surl = repository.findForUpdate(id).orElseThrow(() -> missing());
        surl.increaseCount(); // 더티 체킹으로 count UPDATE
        return surl.getUrl();
    }
    private GlobalException missing() { return new GlobalException(HttpStatus.NOT_FOUND, "URL을 찾을 수 없습니다."); }
    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getUserInfo() != null)
                throw new URISyntaxException(url, "HTTP(S) 주소가 필요합니다.");
        } catch (URISyntaxException ex) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "http:// 또는 https://로 시작하는 올바른 URL을 입력해주세요.");
        }
    }
}
