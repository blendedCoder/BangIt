package com.bangIt.blended.controller.partner;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bangIt.blended.common.security.BangItUserDetails;
import com.bangIt.blended.domain.dto.place.PlaceSaveDTO;
import com.bangIt.blended.domain.dto.place.PlaceUpdateDTO;
import com.bangIt.blended.service.partner.PartnerPlaceService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
@RequestMapping("/partner")
public class PartnerController {
	
	private final PartnerPlaceService placeService;
	
	@GetMapping
	public String partner() {
		return "views/partner/partnerIndex";
	}
	
	//숙소등록 페이지이동
	@GetMapping("/placeNew")
	public String placeWrite() {
		return "views/partner/place/placeSave";
	}
	
	//숙소 등록하기
	@PostMapping("/placeSave")
	public String placeSave(@AuthenticationPrincipal BangItUserDetails userDetails, PlaceSaveDTO dto) {
		placeService.saveProcess(userDetails.getId(), dto);
		return "redirect:/partner";
	}
	
	//숙소 및 방 이미지 저장
	@PostMapping("/uploadImage")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return placeService.s3TempUpload(file);
    }
	
	//숙소 목록 조회
	@GetMapping("/placeList")
	public String placeList(@AuthenticationPrincipal BangItUserDetails userDetails, Model model) {
		placeService.listProcess(userDetails.getId(), model);
	    return "views/partner/place/placeList";
	}

	//숙소 상세정보 조회
	@GetMapping("/placeDetails/{id}")
	public String PlaceDetails(@PathVariable("id") Long id, Model model) {

		placeService.detailProcess(id, model);
		return "views/partner/place/placeDetails :: placeDetailsFragment";
	}

//	//숙소 수정
//	@PutMapping("/partner/placeDetails/{id}")
//	public String update(@PathVariable("id") long id, PlaceUpdateDTO dto) {
//		//placeService.updateProcess(id, dto);
//		return "views/partner/place/placeList";
//	}

	//숙소 삭제
	@DeleteMapping("/places/{id}")
	public String delete(@PathVariable("id") long id) {
	    placeService.deleteProcess(id);
	    return "redirect:/partner/placeList";
	}
	
	@GetMapping("/roomSave")
    public String roomSave() {
		 return "views/partner/room/roomSave";
    }
	
	//예약목록
	@GetMapping("/reservation")
	public String reservation() {
		return "views/partner/reservation/reservation";
	}
	
}
