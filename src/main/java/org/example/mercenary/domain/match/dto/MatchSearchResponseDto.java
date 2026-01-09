package org.example.mercenary.domain.match.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.mercenary.domain.match.entity.MatchEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class MatchSearchResponseDto {
    private final Long matchId;
    private final String placeName;
    private final String district;
    private final LocalDateTime matchDate;
    private final Integer maxPlayerCount;
    private final Integer currentPlayerCount;
    private final String description;

    // Redis Geo 검색 시 거리 정보도 함께 받아 사용자에게 보여줍니다.
    private final Double distance; // 사용자 위치에서 경기장까지의 거리 (Km)

    private Double latitude;
    private Double longitude;

    public static MatchSearchResponseDto from(MatchEntity match, Double distance) {
        return MatchSearchResponseDto.builder()
                .matchId(match.getId())
                .placeName(match.getPlaceName())
                .district(match.getDistrict())
                .matchDate(match.getMatchDate())
                .maxPlayerCount(match.getMaxPlayerCount())
                .currentPlayerCount(match.getCurrentPlayerCount())
                .distance(distance)
                .latitude(match.getLatitude())
                .longitude(match.getLongitude())
                .build();
    }
}
