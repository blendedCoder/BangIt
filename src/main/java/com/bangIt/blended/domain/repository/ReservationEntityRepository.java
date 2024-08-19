package com.bangIt.blended.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bangIt.blended.domain.entity.ReservationEntity;

public interface ReservationEntityRepository extends JpaRepository<ReservationEntity, Long> {

	 //Optional<ReservationEntity> findById(Long id); // 기본 제공 메서드

	 Optional<ReservationEntity> findByOrderId(String orderId);

	 //ReservationEntity findTopByOrderByIdDesc();
}
