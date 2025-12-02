package org.example.mercenary.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.application.dto.ApplicationRequestDto;
import org.example.mercenary.domain.application.service.ApplicationService;
import org.example.mercenary.global.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 용병 신청 API (POST /api/matches/{matchId}/apply)
     * - Redisson 분산 락을 통해 동시성 문제를 제어합니다.
     */
    @PostMapping("/{matchId}/apply")
    public ResponseEntity<ApiResponseDto<?>> applyMatch(
            @PathVariable Long matchId,
            @RequestBody ApplicationRequestDto request
    ) {
        // Service 로직 호출
        applicationService.applyMatch(matchId, request.getUserId());

        // 성공 응답 반환
        return ResponseEntity.ok(ApiResponseDto.success("용병 신청이 성공적으로 완료되었습니다."));
    }
}
