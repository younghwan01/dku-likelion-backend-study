package com.ll.jpa.domain.article.controller;
import com.ll.jpa.domain.article.dto.ArticleDto;
import com.ll.jpa.domain.article.service.ArticleService;
import com.ll.jpa.global.rq.Rq;
import com.ll.jpa.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/articles") @RequiredArgsConstructor
public class ArticleController {
    private final ArticleService service;
    private final Rq rq;
    public record ArticleRequest(@NotBlank @Size(max=200) String title, @NotBlank @Size(max=10000) String body) {}
    @PostMapping
    public ResponseEntity<RsData<ArticleDto>> write(@Valid @RequestBody ArticleRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RsData<>("201-1", "게시물이 생성되었습니다.",
            service.write(body.title(), body.body(), rq.getMemberId())));
    }
    @GetMapping
    public RsData<List<ArticleDto>> list(@RequestParam(defaultValue="") String keyword) {
        return new RsData<>("200-1", "게시물 목록", service.search(keyword));
    }
    @GetMapping("/count") public RsData<Long> count() { return new RsData<>("200-1", "게시물 수", service.count()); }
    @GetMapping("/by-ids")
    public RsData<List<ArticleDto>> byIds(@RequestParam List<Long> ids) { return new RsData<>("200-1", "ID 검색", service.findByIds(ids)); }
    @GetMapping("/exact")
    public RsData<List<ArticleDto>> exact(@RequestParam String title, @RequestParam String body) {
        return new RsData<>("200-1", "제목·내용 검색", service.findExact(title, body));
    }
    @GetMapping("/{id}") public RsData<ArticleDto> find(@PathVariable Long id) { return new RsData<>("200-1", "게시물 조회", service.find(id)); }
    @PutMapping("/{id}")
    public RsData<ArticleDto> modify(@PathVariable Long id, @Valid @RequestBody ArticleRequest body) {
        return new RsData<>("200-1", "게시물이 수정되었습니다.", service.modify(id, body.title(), body.body(), rq.getMemberId()));
    }
    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable Long id) {
        service.delete(id, rq.getMemberId()); return new RsData<>("200-1", "게시물이 삭제되었습니다.");
    }
}
