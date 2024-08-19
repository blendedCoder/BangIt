package com.bangIt.blended.common.bot.entity;

import java.util.Optional;
import java.lang.Long;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bangIt.blended.common.bot.IntentionEntity;

public interface IntentionEntityRepository extends JpaRepository<IntentionEntity, Long> {

	// 주어진 키워드에 해당하는 IntentionEntity를 검색하는 메서드
	// 결과가 존재하지 않을 수 있으므로 Optional로 반환
	Optional<IntentionEntity> findByKeyword(String keyword);

	// 주어진 키워드가 존재하는지 여부를 확인하는 메서드
	boolean existsByKeyword(String keyword);
}
