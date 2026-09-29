package com.example.study.member;

import com.example.study.member.dto.MemberRequest;
import com.example.study.member.dto.MemberResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 표현 계층.
 *
 * <p>내 자료 조회는 식별자를 받지 않고 토큰에서 확인함. 받으면 남의 자료를 조회할 수 있음.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping
  public ResponseEntity<MemberResponse> join(@Valid @RequestBody MemberRequest request) {
    MemberResponse created =
        memberService.join(request.email(), request.password(), request.nickname());

    return ResponseEntity.created(URI.create("/api/members/" + created.id())).body(created);
  }

  @GetMapping("/{id}")
  public MemberResponse findOne(@PathVariable Long id) {
    return memberService.findById(id);
  }

  // 마이페이지(등록한 책·받은 요청·보낸 요청)는 여기가 아니라 /api/mypage/* 에서 처리함(03_API목록.md 5~7번,
  // 담당: 김시웅). BookRepository.findByOwnerIdOrderByCreatedAtDesc(),
  // ExchangeRequestRepository.findByBookOwnerIdOrderByCreatedAtDesc()/findByRequesterIdOrderByCreatedAtDesc()
  // 사용.
}
