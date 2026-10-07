package com.ll.jpa.domain.member.service;
import com.ll.jpa.domain.member.entity.Member;
import com.ll.jpa.domain.member.dto.MemberDto;
import com.ll.jpa.domain.member.repository.MemberRepository;
import com.ll.jpa.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public MemberDto join(String username, String password, String nickname) {
        if (repository.existsByUsername(username))
            throw new GlobalException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        Member member = Member.builder().username(username).password(passwordEncoder.encode(password)).nickname(nickname).build();
        // DB의 UNIQUE 제약도 확인하도록 flush합니다. 동시 가입은 예외 핸들러에서 409로 처리합니다.
        return MemberDto.from(repository.saveAndFlush(member));
    }
    public MemberDto login(String username, String password) {
        Member member = repository.findByUsername(username)
            .orElseThrow(() -> new GlobalException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."));
        if (!passwordEncoder.matches(password, member.getPassword()))
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.");
        return MemberDto.from(member);
    }
    public MemberDto find(Long id) {
        return MemberDto.from(repository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.UNAUTHORIZED, "회원 정보를 찾을 수 없습니다.")));
    }
    public java.util.Optional<MemberDto> findByUsername(String username) {
        return repository.findByUsername(username).map(MemberDto::from);
    }
    public Member getReference(Long id) { return repository.getReferenceById(id); }
}
