package org.example.mercenary.domain.match.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchLocationService {

    private final StringRedisTemplate redisTemplate;
    private static final String GEO_KEY = "matches:geo"; // Redis Key

    /**
     * 1. 경기 등록 시 위치 정보 저장
     * Redis에 (경도, 위도, matchId)를 저장합니다.
     */
    public void addMatchLocation(Long matchId, double longitude, double latitude) {
        // Point(x=경도, y=위도) 주의! 순서 틀리면 엉뚱한 곳 찍힙니다.
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), matchId.toString());
    }

    /**
     * 2. 내 주변 경기 검색 (Reader: 용병)
     * 내 위치 기준 반경 N km 이내의 matchId들을 0.01초 만에 가져옵니다.
     */
    public List<String> findNearbyMatchIds(double longitude, double latitude, double kmDistance) {
        // Circle: 중심점과 반경
        // GeoRadiusCommandArgs: 좌표값도 같이 반환받을지 등의 옵션
        var options = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance();

        var results = redisTemplate.opsForGeo().radius(
                GEO_KEY,
                new Circle(new Point(longitude, latitude), new Distance(kmDistance, Metrics.KILOMETERS)),
                options
        );

        // 검색된 결과에서 Match ID만 추출해서 리턴 (이후 DB에서 상세 정보 조회)
        if (results == null) return List.of();
        return results.getContent().stream()
                .map(content -> content.getContent().getName())
                .toList();
    }
}