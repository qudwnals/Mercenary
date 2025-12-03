package org.example.mercenary.domain.match.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;
import org.example.mercenary.domain.match.service.MatchService;
import org.example.mercenary.global.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}