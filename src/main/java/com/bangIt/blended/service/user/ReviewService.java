package com.bangIt.blended.service.user;

import java.util.List;

import com.bangIt.blended.domain.dto.place.ReviewCreateDTO;
import com.bangIt.blended.domain.dto.place.ReviewListDTO;
import com.bangIt.blended.domain.entity.ReviewEntity;

public interface ReviewService {

	void createReview(Long id, ReviewCreateDTO dto);
	
	List<ReviewListDTO> getReviewsByPlace(long placeId);

}
