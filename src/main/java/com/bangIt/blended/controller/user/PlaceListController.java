package com.bangIt.blended.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bangIt.blended.common.util.LogActivity;
import com.bangIt.blended.domain.dto.placesList.ScrapePlaceDTO;
import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;
import com.bangIt.blended.domain.enums.ActivityType;
import com.bangIt.blended.service.user.PlaceListService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class PlaceListController {
	
	private final PlaceListService placeListService;
	
	
	@LogActivity(
		    activityType = ActivityType.SEARCH_ACCOMMODATION,
		    detailRecordExpression = "#args[0].region"
		)
    @GetMapping("/search/places")
    public String searchPlaces(SearchPlaceDTO dto, Model model) {
        placeListService.findPlaceProcess(dto, model);
        return "/views/user/placeList/placeList";
    }
    
    
    @ResponseBody
    @GetMapping("/search/places/scrape/{placeName}")
    public ScrapePlaceDTO scrapePlace(@PathVariable(name = "placeName") String placeName) {
    	
    	return placeListService.scrapeProcess(placeName);
        
    }
    
    
 
    

	

}
