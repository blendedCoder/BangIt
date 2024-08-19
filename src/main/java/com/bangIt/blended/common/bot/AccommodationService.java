package com.bangIt.blended.common.bot;

import com.bangIt.blended.domain.dto.place.PlaceDetailDTO;
import com.bangIt.blended.domain.enums.PlaceType;

import java.util.List;

public interface AccommodationService {
    /**
     * 주어진 검색 조건에 맞는 숙소를 검색하는 메서드
     * @param region 검색할 지역 (문자열)
     * @param detailedAddress 상세 주소 키워드
     * @param type 숙소 유형
     * @return 검색 조건에 맞는 숙소 정보 목록
     */
    List<PlaceDetailDTO> search(String region, String detailedAddress, PlaceType type);
}