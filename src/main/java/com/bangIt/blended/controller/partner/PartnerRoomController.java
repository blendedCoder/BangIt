package com.bangIt.blended.controller.partner;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bangIt.blended.domain.dto.room.RoomListDTO;
import com.bangIt.blended.domain.dto.room.RoomSaveDTO;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.service.partner.PartnerRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PartnerRoomController {

	    private final PartnerRoomService roomService;
	    private final PlaceEntityRepository placeRepository;

	    //방 등록 페이지
	    @GetMapping("/partner/roomRegisterForm")
	    public String roomRegisterForm(@RequestParam("placeId") Long placeId, Model model) {
	        model.addAttribute("placeId", placeId);
	        return "views/partner/room/roomSave";
	    }

	    //방 등록
	    @PostMapping("/partner/roomSave")
	    public String roomSave(RoomSaveDTO dto) {
	        roomService.saveRoom(dto);
	        return "redirect:/partner/placeList ";
	    }
	    
	    @GetMapping("/partner/roomListHtml")
	    public String roomListHtml(@RequestParam("placeId") Long placeId, Model model) {
	        // 인스턴스를 사용하여 findById 메서드를 호출합니다.
	        PlaceEntity place = placeRepository.findById(placeId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid Place ID: " + placeId));
	        
	        PlaceStatus placeStatus = place.getStatus();
	        List<RoomListDTO> rooms = roomService.listProcess(placeId, placeStatus);
	        model.addAttribute("rooms", rooms);
	        model.addAttribute("placeStatus", placeStatus);
	        return "views/partner/room/roomDetails :: roomListContent";
	    }
		
	    
	    
}
