package com.bangIt.blended.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.RoomEntity;
import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.enums.Region;

public interface PlaceEntityRepository extends JpaRepository<PlaceEntity, Long> {

    // 기존 메서드
    List<PlaceEntity> findByStatus(PlaceStatus status);
    List<PlaceEntity> findBySeller(UserEntity seller);
    Optional<PlaceEntity> findByIdAndSeller(Long id, UserEntity seller);

    // 최신 숙소 5개 ID 조회 (페이징 처리 가능)
    @Query("SELECT p.id FROM PlaceEntity p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Long> findTop5IdsByStatus(@Param("status") PlaceStatus status, Pageable pageable);

    // Fetch Join으로 이미지 포함하여 숙소 조회
    @Query("SELECT DISTINCT p FROM PlaceEntity p LEFT JOIN FETCH p.images WHERE p.id IN :ids")
    List<PlaceEntity> findAllByIdsWithImages(@Param("ids") List<Long> ids);

    // 특정 지역 숙소 조회 (이미지 Fetch Join)
    @Query("SELECT DISTINCT p FROM PlaceEntity p LEFT JOIN FETCH p.images WHERE p.region = :region")
    List<PlaceEntity> findByRegionWithImages(@Param("region") Region region);

    // 모든 숙소 조회 (이미지 Fetch Join)
    @Query("SELECT DISTINCT p FROM PlaceEntity p LEFT JOIN FETCH p.images")
    List<PlaceEntity> findAllWithImages();

    // Rooms 데이터 개별 조회
    @Query("SELECT r FROM RoomEntity r WHERE r.place.id = :placeId")
    List<RoomEntity> findRoomsByPlaceId(@Param("placeId") Long placeId);

}
