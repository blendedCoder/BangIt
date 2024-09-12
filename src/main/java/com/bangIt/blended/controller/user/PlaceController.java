package com.bangIt.blended.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bangIt.blended.domain.dto.place.ReviewCreateDTO;
import com.bangIt.blended.domain.dto.place.ReviewListDTO;
import com.bangIt.blended.domain.dto.room.RoomDetailDTO;
import com.bangIt.blended.service.user.ReviewService;
import com.bangIt.blended.service.user.RoomService;
import com.bangIt.blended.service.user.UserPlaceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PlaceController {
	
	private final UserPlaceService service;
	private final RoomService roomService;
	private final ReviewService reviewService;

	@GetMapping("/place")
	public String listPlaces() {
		return "views/user/place/placeList";
	}
	
	// 숙소 상세정보 조회 (방 목록 포함)
    @GetMapping("/place/detail/{placeId}")
    public String placeDetail(@PathVariable("placeId") long placeId, Model model) {
        service.detailProcess(placeId, model);
        roomService.listRoomsPlace(placeId, model);

        // 리뷰 정보 추가 (반환 타입을 명시적으로 표시)
        List<ReviewListDTO> reviews = reviewService.getReviewsByPlace(placeId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("newReview", new ReviewCreateDTO());

        return "views/user/place/placeDetail";
    }
    
    // 방 상세정보 조회 (AJAX 요청 처리)
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDetailDTO> RoomDetails(@PathVariable("roomId") long roomId) {
        return ResponseEntity.ok(roomService.getRoomDetailById(roomId));
    }
	

}
