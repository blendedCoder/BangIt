package com.bangIt.blended.domain.dto.place;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReviewListDTO {
	
	private Long id; // 리뷰 ID
    private Long placeId; // 리뷰 대상 숙소의 ID
    private Long userId; // 리뷰 작성자의 ID
    private String userName; // 리뷰 작성자 이름
    private String content; // 리뷰 내용
    private Integer rating; // 평점 (예: 1-5)
    private LocalDateTime createdAt; // 등록일
    private LocalDateTime updatedAt; // 수정일
    
    private long reviewCount; //전체 리뷰 수
    private double averageRating; // 전체 리뷰의 평균점수

}
