package com.bangIt.blended.service;

import java.util.List;

import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.place.HotelListDTO;



public interface IndexService {
	
	List<HotelListDTO> TopPriceHotelList(Model model);

	List<HotelListDTO> getLatestHotels();

	List<HotelListDTO> getNearByHotels(double latitude, double longitude);

	List<HotelListDTO> getRecommendedHotels(Long userId);



}
