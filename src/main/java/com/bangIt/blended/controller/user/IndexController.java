package com.bangIt.blended.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bangIt.blended.common.security.BangItUserDetails;
import com.bangIt.blended.domain.dto.place.HotelListDTO;
import com.bangIt.blended.service.IndexService;

@RequiredArgsConstructor
@Controller
public class IndexController {

	private final IndexService indexService;

	@GetMapping("/")
	public String showHomePage(Model model, @AuthenticationPrincipal BangItUserDetails userDetails) {
		 if (userDetails == null) {
		        // 인증되지 않은 경우
		        model.addAttribute("isLoggedIn", false);
		    } else {
		        // 인증된 경우
		        List<HotelListDTO> recommendedHotels = indexService.getRecommendedHotels(userDetails.getId());
		        model.addAttribute("recommendedHotels", recommendedHotels);
		        model.addAttribute("isLoggedIn", true);
		    }

		// 최근에 등록된 숙소 기준으로 정렬된 호텔 리스트 추가
		List<HotelListDTO> latestHotels = indexService.getLatestHotels();
		model.addAttribute("latestHotels", latestHotels);

		// 가격별 숙소 리스트 추가
		List<HotelListDTO> hotelList = indexService.TopPriceHotelList(model);
		model.addAttribute("hotelList", hotelList);

		return "index";
	}

	@PostMapping("/get-nearby-hotels")
	public @ResponseBody List<HotelListDTO> getNearByHotels(@RequestBody Map<String, Double> locationData) {
		double latitude = locationData.get("latitude");
		double longitude = locationData.get("longitude");

		return indexService.getNearByHotels(latitude, longitude);
	}
}
