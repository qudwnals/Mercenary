package org.example.mercenary.domain.match.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MatchCreateRequestDto {
    @NotNull private Long writerId; // 주최자 ID

    @NotBlank private String city;
    @NotBlank private String district;
    @NotBlank private String neighborhood;
    @NotBlank private String placeName;

    // Geo 위치 정보 (Redis 동기화에 필수)
    @NotNull @Min(-90) @Max(90)
    private Double latitude;
    @NotNull @Min(-180) @Max(180)
    private Double longitude;

    @NotNull private String matchDate; // 날짜와 시간 정보 (예: 2025-12-31T18:00:00)

    @NotNull @Min(2) @Max(22)
    private Integer maxPlayerCount; // 최대 인원 (최소 2명, 최대 22명)

    @NotBlank private String description;

}
