package org.example.mercenary.domain.match.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;
import org.example.mercenary.domain.match.service.MatchService;
import org.example.mercenary.global.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.mercenary.domain.match.dto.MatchSearchRequestDto;
import org.example.mercenary.domain.match.dto.MatchSearchResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * 매치 생성 API (POST /api/matches)
     * - DB에 저장 후 Redis Geo에 위치 정보를 등록합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createMatch(
            @Valid @RequestBody MatchCreateRequestDto request
    ) {
        Long matchId = matchService.createMatch(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("매치가 성공적으로 생성되었습니다.", matchId));
    }
    // 2. [핵심] 내 주변 매치 검색 API 추가
    /**
     * 위치 기반 검색 API (GET /api/matches/nearby)
     * - Redis Geo를 활용하여 고성능 검색을 제공합니다.
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponseDto<List<MatchSearchResponseDto>>> searchNearbyMatches(
            // GET 요청이므로, 쿼리 파라미터로 DTO를 받습니다.
            @Valid MatchSearchRequestDto request
    ) {
        List<MatchSearchResponseDto> results = matchService.searchNearbyMatches(request);

        return ResponseEntity.ok(ApiResponseDto.success("주변 매치 검색 성공", results));
    }
}