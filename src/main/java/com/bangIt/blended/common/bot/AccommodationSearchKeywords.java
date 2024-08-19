package com.bangIt.blended.common.bot;

import com.bangIt.blended.domain.enums.PlaceType;

import lombok.Getter;
import lombok.Setter;

/**
 * 숙소 검색에 사용되는 키워드를 캡슐화하는 클래스
 */
@Getter
@Setter
public class AccommodationSearchKeywords {
    private String region;           // 검색할 지역
    private String detailedAddress;  // 상세 주소 키워드
    private PlaceType type;          // 숙소 유형 (예: 호텔, 펜션 등)
    private String action;           // 사용자 요청 액션 (예: "알려줘")

    // 생성자, getter, setter 메서드
    // ...

    /**
     * 유효한 검색 키워드인지 확인하는 메서드
     * @return 모든 필수 필드가 null이 아니면 true, 그렇지 않으면 false
     */
    public boolean isValid() {
        return region != null && type != null && action != null;
    }
}
