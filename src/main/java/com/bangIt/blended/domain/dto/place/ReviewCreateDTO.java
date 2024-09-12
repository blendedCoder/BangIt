package com.bangIt.blended.domain.dto.place;

import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.ReviewEntity;
import com.bangIt.blended.domain.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewCreateDTO {
	
	private Long placeId;  // 리뷰 대상 숙소의 ID
    private Long userId;   // 리뷰 작성자의 ID
    private String content;  // 리뷰 내용
    private Integer rating;  // 평점 (예: 1-5)
    
 // ReviewEntity로 변환하는 메서드
    public ReviewEntity toReviewEntity(PlaceEntity place, UserEntity user) {
        return ReviewEntity.builder()
                .place(place)
                .user(user)
                .content(content)
                .rating(rating)
                .build();
    }
    

}
