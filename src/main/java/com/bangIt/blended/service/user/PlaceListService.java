package com.bangIt.blended.service.user;

import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;

public interface PlaceListService {

	void findPlaceProcess(SearchPlaceDTO dTO, Model model);

}
