package com.bangIt.blended.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;
import com.bangIt.blended.service.user.PlaceListService;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PlaceListController {
	
	private final PlaceListService placeListService;
	
	@PostMapping("/search/places")
	public String postMethodName(SearchPlaceDTO DTO,Model model) {
		
		placeListService.findPlaceProcess(DTO,model);
		
		
		return "/views/user/placeList/placelist";
	}
	
	@GetMapping("/placeslist")
	public String getMethodName() {
		return "/views/user/placeList/placelist";
	}
	

}
