package com.bangIt.blended.service.user;


import java.io.IOException;

import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.placesList.ScrapePlaceDTO;
import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;

public interface PlaceListService {

	void findPlaceProcess(SearchPlaceDTO dTO, Model model) throws IOException;

	ScrapePlaceDTO scrapeProcess(String placeName);

}
