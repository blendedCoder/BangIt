package com.bangIt.blended.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bangIt.blended.domain.entity.ActivityLogEntity;


public interface ActivityLogEntityRepositoty extends JpaRepository<ActivityLogEntity, Long> {

	@Query("SELECT a.detailRecord FROM ActivityLogEntity a WHERE a.user.id = :userId GROUP BY a.detailRecord ORDER BY COUNT(a.detailRecord) DESC")
	List<String> findMostSearchedRegionByUserId(@Param("userId") Long userId);


}
