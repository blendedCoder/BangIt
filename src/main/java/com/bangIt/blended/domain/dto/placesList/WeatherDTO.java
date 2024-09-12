package com.bangIt.blended.domain.dto.placesList;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class WeatherDTO {
	
    private LocalDate date;           // 날짜
    private String weather;           // 날씨 상태 (예: 맑음, 흐림, 비 등)
    private Double temperature;       // 현재 기온 (단기 예보용)
    private Double maxTemperature;    // 최고 기온
    private Double minTemperature;    // 최저 기온
    private String forecastType;      // 예보 유형 (단기 or 중기)
    
    private Integer precipitationProbability; // 강수 확률

}
