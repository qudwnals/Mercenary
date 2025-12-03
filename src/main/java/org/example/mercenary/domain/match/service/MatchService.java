package org.example.mercenary.domain.match.service;

import lombok.RequiredArgsConstructor;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;
import org.example.mercenary.domain.match.entity.MatchEntity;
import org.example.mercenary.domain.match.repository.MatchRepository;
import org.example.mercenary.domain.match.dto.MatchSearchRequestDto;
import org.example.mercenary.domain.match.dto.MatchSearchResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


        MatchEntity newMatch = MatchEntity.from(request);

        // 2. MySQL에 저장 (ID 발급)
        MatchEntity savedMatch = matchRepository.save(newMatch);

        // 3. Redis Geo에 위치 정보 동기화
        matchLocationService.addMatchLocation(
                savedMatch.getId(),
                request.getLongitude(),
                request.getLatitude()
        );

        return savedMatch.getId();
    }

    /**
     * 내 주변 매치 검색 (Redis Geo + MySQL 조합)
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<MatchSearchResponseDto> searchNearbyMatches(MatchSearchRequestDto request) {

        // 1. [핵심] Redis Geo로 Match ID 목록과 거리 정보를 초고속으로 조회
        Map<Long, Double> nearbyMatchData = matchLocationService.findNearbyMatchIds(
                request.getLongitude(),
                request.getLatitude(),
                request.getDistanceKm()
        );

        // 검색 결과가 없으면 바로 빈 리스트 반환
        if (nearbyMatchData.isEmpty()) {
            return List.of();
        }

        // 2. Redis에서 찾은 ID 목록을 기반으로 MySQL에서 상세 정보 조회
        List<Long> matchIds = nearbyMatchData.keySet().stream().toList();
        List<MatchEntity> matches = matchRepository.findAllById(matchIds);

        // 3. MySQL 결과와 Redis에서 받은 거리 정보를 조합하여 응답 DTO로 변환
        return matches.stream()
                .map(match -> {
                    Double distance = nearbyMatchData.getOrDefault(match.getId(), 0.0);
                    return MatchSearchResponseDto.from(match, distance);
                })
                .sorted((m1, m2) -> Double.compare(m1.getDistance(), m2.getDistance()))
                .collect(Collectors.toList());
    }
}