package com.bangIt.blended.service.user.impl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bangIt.blended.domain.dto.place.ReviewCreateDTO;
import com.bangIt.blended.domain.dto.place.ReviewListDTO;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.ReviewEntity;
import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.domain.repository.ReviewEntityRepository;
import com.bangIt.blended.domain.repository.UserEntityRepository;
import com.bangIt.blended.service.user.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewServiceProcess implements ReviewService{
	
	private final ReviewEntityRepository reviewRepository;
    private final UserEntityRepository userRepository;
    private final PlaceEntityRepository placeRepository;
    
    @Override
    @Transactional
    public void createReview(Long id, ReviewCreateDTO dto) {
        UserEntity user = userRepository.findById(id).orElseThrow();
        PlaceEntity place = placeRepository.findById(dto.getPlaceId()).orElseThrow();

        ReviewEntity reviewEntity = dto.toReviewEntity(place, user);
        reviewRepository.save(reviewEntity);
    }

	@Override
	public List<ReviewListDTO> getReviewsByPlace(long placeId) {
		List<ReviewEntity> reviews = reviewRepository.findByPlaceId(placeId);
		long reviewCount = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
        
        return reviews.stream()
                .map(review -> {
                    ReviewListDTO dto = review.toReviewListDTO();
                    dto.setReviewCount(reviewCount);
                    dto.setAverageRating(averageRating);
                    return dto;
                })
                .collect(Collectors.toList());
	}

}
