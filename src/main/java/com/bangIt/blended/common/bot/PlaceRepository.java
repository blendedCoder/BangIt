package com.bangIt.blended.common.bot;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.enums.PlaceType;
import com.bangIt.blended.domain.enums.Region;

/**
 * PlaceEntity에 대한 데이터 액세스 작업을 정의하는 리포지토리 인터페이스
 */
@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    /**
     * 지역, 상세 주소, 숙소 유형에 따라 PlaceEntity를 검색하는 메서드
     * @param region 검색할 지역
     * @param detailedAddress 상세 주소 키워드 (부분 일치 검색)
     * @param type 숙소 유형
     * @return 조건에 맞는 PlaceEntity 목록
     */
    List<PlaceEntity> findByRegionAndDetailedAddressContainingAndType(
        Region region, String detailedAddress, PlaceType type);
}
