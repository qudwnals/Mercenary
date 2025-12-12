package org.example.mercenary.domain.match.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.mercenary.domain.match.dto.MatchCreateRequestDto;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matches")

public class MatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long writerId; // 경기 주최자 ID

    // 위치 정보 (MatchLocationService에서 Geo-Search 키로 사용)
    private String city;
    private String district;
    private String neighborhood;
    private String placeName;

    private Double latitude;
    private Double longitude;

    // 경기 정보
    private LocalDateTime matchDate;
    private int maxPlayerCount; // 최대 모집 인원
    private int currentPlayerCount; // 현재 인원 (동시성 제어 대상)
    private String description;

    // (JPA 편의 메서드) 현재 인원 증가
    public void increasePlayerCount() {
        this.currentPlayerCount++;
    }
    public static MatchEntity from(MatchCreateRequestDto request) {
        return MatchEntity.builder()
                .writerId(request.getWriterId())
                .city(request.getCity())
                .district(request.getDistrict())
                .neighborhood(request.getNeighborhood())
                .placeName(request.getPlaceName())
                .matchDate(LocalDateTime.parse(request.getMatchDate()))
                .maxPlayerCount(request.getMaxPlayerCount())
                .currentPlayerCount(0) // 시작 인원 기본값 0 설정
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

}
