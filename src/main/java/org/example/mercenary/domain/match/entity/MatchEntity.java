package org.example.mercenary.domain.match.entity;
import jakarta.persistence.*;
import lombok.*;

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

    // 경기 정보
    private LocalDateTime matchDate;
    private int maxPlayerCount; // 최대 모집 인원
    private int currentPlayerCount; // 현재 인원 (동시성 제어 대상)
    private String description;

    // (JPA 편의 메서드) 현재 인원 증가
    public void increasePlayerCount() {
        this.currentPlayerCount++;
    }
}
