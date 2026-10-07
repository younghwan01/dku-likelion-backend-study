package com.ll.jpa.domain.member.controller;
import com.ll.jpa.domain.member.dto.MemberDto;
import com.ll.jpa.domain.member.service.MemberService;
import com.ll.jpa.global.rq.Rq;
import com.ll.jpa.global.rsData.RsData;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/members") @RequiredArgsConstructor
public class MemberController {
    private final MemberService service;
    private final Rq rq;
    public record JoinRequest(@NotBlank @Pattern(regexp="[A-Za-z0-9_]{3,30}") String username,
                              @NotBlank @Size(min=8, max=60) String password,
                              @NotBlank @Size(max=30) String nickname) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    @PostMapping
    public ResponseEntity<RsData<MemberDto>> join(@Valid @RequestBody JoinRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RsData<>("201-1", "회원가입이 완료되었습니다.",
            service.join(body.username(), body.password(), body.nickname())));
    }
    @PostMapping("/login")
    public RsData<MemberDto> login(@Valid @RequestBody LoginRequest body, HttpServletRequest request) {
        MemberDto member = service.login(body.username(), body.password());
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        request.getSession(true).setAttribute("memberId", member.id());
        return new RsData<>("200-1", "로그인되었습니다.", member);
    }
    @PostMapping("/logout")
    public RsData<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return new RsData<>("200-1", "로그아웃되었습니다.");
    }
    @GetMapping("/me")
    public RsData<MemberDto> me() { return new RsData<>("200-1", "내 정보 조회", service.find(rq.getMemberId())); }
}
