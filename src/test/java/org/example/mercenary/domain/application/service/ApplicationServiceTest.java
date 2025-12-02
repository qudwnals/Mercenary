package org.example.mercenary.domain.application.service;

import org.example.mercenary.domain.application.repository.ApplicationRepository;
import org.example.mercenary.domain.match.entity.MatchEntity;
import org.example.mercenary.domain.match.repository.MatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// DB와 Redis 설정을 강제로 주입합니다.

class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    @DisplayName("Redisson 락: 100명이 동시에 신청해도 정확히 5명만 성공해야 한다.")
    void concurrentApplicationTest() throws InterruptedException {
        // given
        // 1. 선착순 5명 모집하는 경기 생성 (정원 5명)
        MatchEntity match = matchRepository.save(MatchEntity.builder()
                .writerId(1L)
                .city("서울").district("강남구").neighborhood("역삼동")
                .matchDate(LocalDateTime.now().plusDays(1))
                .placeName("강남풋살장")
                .maxPlayerCount(5)
                .currentPlayerCount(0)
                .description("빡겜러 모집")
                .build());

        int threadCount = 100; // 100명의 유저 동시 신청
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 100L;
            executorService.submit(() -> {
                try {
                    applicationService.applyMatch(match.getId(), userId);
                } catch (Exception e) {
                    // 마감으로 인한 실패는 정상 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 100개 스레드가 끝날 때까지 대기

        // then
        MatchEntity findMatch = matchRepository.findById(match.getId()).orElseThrow();

        System.out.println("=========================================");
        System.out.println(">>> [Redisson 락 적용 결과] 최종 신청 성공 인원: " + findMatch.getCurrentPlayerCount());
        System.out.println("=========================================");

        // 검증: 최종 인원 수는 정확히 5명이어야 합니다.
        assertThat(findMatch.getCurrentPlayerCount()).isEqualTo(5);
    }
}