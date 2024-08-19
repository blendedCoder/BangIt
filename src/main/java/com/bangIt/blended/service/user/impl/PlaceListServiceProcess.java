package com.bangIt.blended.service.user.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.place.PlaceDTO;
import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;
import com.bangIt.blended.domain.mapper.PlaceMapper;
import com.bangIt.blended.service.user.PlaceListService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class PlaceListServiceProcess implements PlaceListService {
	
	private final PlaceMapper placeMapper;

	@Override
	public void findPlaceProcess(SearchPlaceDTO dto, Model model) {
		
		List<PlaceDTO> place =placeMapper.findprocess(dto);
		
		model.addAttribute("placeList", place);
		
	}

}
