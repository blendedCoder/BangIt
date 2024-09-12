package com.bangIt.blended.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bangIt.blended.common.security.BangItUserDetails;
import com.bangIt.blended.domain.dto.place.ReviewCreateDTO;
import com.bangIt.blended.service.user.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/reviews")
public class ReviewController {
	
	private final ReviewService reviewService;

	@PostMapping("/create")
    public String createReview(@AuthenticationPrincipal BangItUserDetails userDetails, 
                               @ModelAttribute ReviewCreateDTO dto) {
        reviewService.createReview(userDetails.getId(), dto);
        return "redirect:/place/detail/" + dto.getPlaceId();
    }
    

//    @PostMapping("/update/{reviewId}")
//    public String updateReview(@AuthenticationPrincipal CustomUserDetails userDetails,
//                               @PathVariable Long reviewId,
//                               @ModelAttribute ReviewCreateDTO dto) {
//        reviewService.updateReview(reviewId, dto, userDetails.getId());
//        return "redirect:/place/detail/" + dto.getPlaceId();
//    }
//
//    @PostMapping("/delete/{reviewId}")
//    public String deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
//                               @PathVariable Long reviewId) {
//        Long placeId = reviewService.deleteReview(reviewId, userDetails.getId());
//        return "redirect:/place/detail/" + placeId;
//    }

}
