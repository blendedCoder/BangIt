package com.bangIt.blended.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.RoomEntity;

public interface RoomEntityRepository extends JpaRepository<RoomEntity, Long>{
	List<RoomEntity> findByPlace(PlaceEntity place);

}
