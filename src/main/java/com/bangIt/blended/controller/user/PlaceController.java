package com.bangIt.blended.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bangIt.blended.service.user.RoomService;
import com.bangIt.blended.service.user.UserPlaceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PlaceController {
	
	private final UserPlaceService service;
	private final RoomService roomService;

	@GetMapping("/place")
	public String listPlaces() {
		return "views/user/place/placeList";
	}
	
	// 숙소 상세정보 조회 (방 목록 포함)
    @GetMapping("/place/detail/{placeId}")
    public String placeDetail(@PathVariable("placeId") long placeId, Model model) {
        service.detailProcess(placeId, model);
        roomService.listRoomsPlace(placeId, model);
        return "views/user/place/placeDetail";
    }
	
	// 개별 방 상세정보 조회 (필요한 경우)
    @GetMapping("/place/{placeId}/room/{roomId}")
    public String roomDetail(@PathVariable("placeId") long placeId, 
                             @PathVariable("roomId") long roomId, 
                             Model model) {
        service.detailProcess(placeId, model);
        roomService.roomDetailProcess(roomId, model);
        return "views/user/place/placeDetail";
    }

}
