package com.bangIt.blended.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.enums.Region;

public interface PlaceEntityRepository extends JpaRepository<PlaceEntity, Long> {

	// roomPrice 값이 높은 RoomEntity를 가진 PlaceEntity부터 낮은 순서대로 정렬하여 반환
	@Query("SELECT p FROM PlaceEntity p JOIN p.rooms r ORDER BY r.roomPrice DESC")
	List<PlaceEntity> findTopByOrderByRoomPriceDesc(Pageable topFour);

	// 새로 등록된 숙소들을 조회하는 메서드
	//List<PlaceEntity> findTop5ByOrderByCreatedAtDesc();

	// PENDING_APPROVAL 상태의 Place를 조회하는 메서드 추가
    List<PlaceEntity> findByStatus(PlaceStatus status);
    List<PlaceEntity> findBySeller(UserEntity seller);
    Optional<PlaceEntity> findByIdAndSeller(Long id, UserEntity seller);

	List<PlaceEntity> findTop5ByStatusOrderByCreatedAtDesc(PlaceStatus approved);

	List<PlaceEntity> findByRegion(Region preferredRegion);

}
