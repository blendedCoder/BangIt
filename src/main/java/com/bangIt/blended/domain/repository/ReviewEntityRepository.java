package com.bangIt.blended.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bangIt.blended.domain.entity.ReviewEntity;

public interface ReviewEntityRepository extends JpaRepository<ReviewEntity, Long>{
	
	List<ReviewEntity> findByPlaceId(long placeId);

}
