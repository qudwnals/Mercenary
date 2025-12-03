package org.example.mercenary.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;
import org.example.mercenary.domain.match.entity.MatchEntity;
import org.example.mercenary.domain.match.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchLocationService matchLocationService;

    /**
     * 매치 생성 (DB 저장 + Redis Geo 동기화)
     */
    @Transactional
    public Long createMatch(MatchCreateRequestDto request) {
        // 1. DTO -> Entity 변환
        MatchEntity newMatch = MatchEntity.builder()
                .writerId(request.getWriterId())
                .city(request.getCity())
                .district(request.getDistrict())
                .neighborhood(request.getNeighborhood())
                .placeName(request.getPlaceName())
                // String 날짜를 LocalDateTime으로 변환
                .matchDate(LocalDateTime.parse(request.getMatchDate()))
                .maxPlayerCount(request.getMaxPlayerCount())
                .currentPlayerCount(0)
                .description(request.getDescription())
                .build();

        // 2. MySQL에 저장 (ID 발급)
        MatchEntity savedMatch = matchRepository.save(newMatch);

        // 3. [핵심] Redis Geo에 위치 정보 동기화 (기존 MatchLocationService 사용)
        matchLocationService.addMatchLocation(
                savedMatch.getId(),
                request.getLongitude(),
                request.getLatitude()
        );

        return savedMatch.getId();
    }
}