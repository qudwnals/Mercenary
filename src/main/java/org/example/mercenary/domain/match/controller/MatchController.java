package org.example.mercenary.domain.match.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;
import org.example.mercenary.domain.match.dto.MatchSearchRequestDto;
import org.example.mercenary.domain.match.dto.MatchSearchResponseDto;
import org.example.mercenary.domain.match.service.MatchService;
import org.example.mercenary.global.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * 1. 매치 생성 API (POST)
     * 🔥 수정됨: UserDetails 대신 Long memberId를 직접 받습니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createMatch(
            @Valid @RequestBody MatchCreateRequestDto request,
            @AuthenticationPrincipal Long memberId //
    ) {
        log.info("매치 생성 요청 - 작성자 ID: {}, 제목: {}", memberId, request.getTitle());

        // 서비스 호출 (ID 변환 과정 없이 바로 넘김)
        Long matchId = matchService.createMatch(request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("매치가 성공적으로 생성되었습니다.", matchId));
    }

    /**
     * 2. 전체 매치 조회 API (GET)
     * 🔥 추가됨: 프론트엔드 목록(/api/matches) 요청을 처리하기 위해 필요합니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<MatchSearchResponseDto>>> getAllMatches() {
        // 서비스에 getAllMatches() 메서드가 없다면 만들어주셔야 합니다!
        // 만약 없다면, 임시로 빈 리스트를 반환하거나 searchNearbyMatches를 활용하세요.
        List<MatchSearchResponseDto> results = matchService.getAllMatches();

        return ResponseEntity.ok(ApiResponseDto.success("전체 매치 조회 성공", results));
    }

    /**
     * 3. 내 주변 매치 검색 API (GET /nearby)
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> searchNearbyMatches( // <--- 반환 타입을 와일드카드(?)로 잠시 변경
                                                  @Valid @ModelAttribute MatchSearchRequestDto request,
                                                  org.springframework.validation.BindingResult bindingResult // 👈 에러 잡는 그물망 추가
    ) {
        // 1. 에러가 있는지 검사
        if (bindingResult.hasErrors()) {
            log.error("검색 요청 데이터 오류: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 2. 정상 로직 실행
        log.info("검색 요청 들어옴: 위도={}, 경도={}, 거리={}",
                request.getLatitude(), request.getLongitude(), request.getDistance());

        List<MatchSearchResponseDto> results = matchService.searchNearbyMatches(request);
        return ResponseEntity.ok(ApiResponseDto.success("주변 매치 검색 성공", results));
    }
}