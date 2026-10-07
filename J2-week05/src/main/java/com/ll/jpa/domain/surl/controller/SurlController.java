package com.ll.jpa.domain.surl.controller;
import com.ll.jpa.domain.surl.dto.SurlDto;
import com.ll.jpa.domain.surl.service.SurlService;
import com.ll.jpa.global.rq.Rq;
import com.ll.jpa.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
@RestController @RequiredArgsConstructor
public class SurlController {
    private final SurlService service;
    private final Rq rq;
    public record SurlRequest(@NotBlank @Size(max=200) String body, @NotBlank @Size(max=2048) String url) {}
    @PostMapping("/api/surls")
    public ResponseEntity<RsData<SurlDto>> write(@Valid @RequestBody SurlRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RsData<>("201-1", "URL이 생성되었습니다.",
            service.write(body.body(), body.url(), rq.getMemberId())));
    }
    @GetMapping("/api/surls") public RsData<List<SurlDto>> list() { return new RsData<>("200-1", "URL 목록", service.findAll()); }
    @GetMapping("/api/surls/{id}") public RsData<SurlDto> find(@PathVariable Long id) { return new RsData<>("200-1", "URL 조회", service.find(id)); }
    @GetMapping("/g/{id}")
    public ResponseEntity<Void> go(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.go(id))).build();
    }
}
