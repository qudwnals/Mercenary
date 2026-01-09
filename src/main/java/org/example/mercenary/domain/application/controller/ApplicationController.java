package org.example.mercenary.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.application.service.ApplicationService;
import org.example.mercenary.global.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches") // 기존 URL 유지
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 용병 신청 API (POST /api/matches/{matchId}/apply)
     * Token에서 유저 정보를 가져옵니다.
     */
    @PostMapping("/{matchId}/apply")
    public ResponseEntity<ApiResponseDto<String>> applyMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserDetails userDetails // 🔥 핵심: 토큰 검증
    ) {
        // 토큰에서 ID 꺼내기 (위조 불가능)
        Long userId = Long.parseLong(userDetails.getUsername());

        // Service 로직 호출
        applicationService.applyMatch(matchId, userId);

        // 성공 응답 반환
        return ResponseEntity.ok(ApiResponseDto.success("용병 신청이 성공적으로 완료되었습니다.", null));
    }
}