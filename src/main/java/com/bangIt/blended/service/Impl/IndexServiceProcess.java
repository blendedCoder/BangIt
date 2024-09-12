package com.bangIt.blended.service.Impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.place.HotelListDTO;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.enums.Region;
import com.bangIt.blended.domain.repository.ActivityLogEntityRepositoty;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.service.IndexService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // final 필드에 대해 생성자를 자동으로 생성해 주입하는 Lombok 애너테이션
public class IndexServiceProcess implements IndexService {

	private final PlaceEntityRepository placeRepository;  // PlaceEntityRepository 주입
	private final ActivityLogEntityRepositoty activityLogRepository;
	
	
	@Override
	public List<HotelListDTO> getLatestHotels() {
		 // 승인된 최신 호텔 5곳을 생성 날짜 순서로 가져옵니다.
	    List<PlaceEntity> latestPlaces = placeRepository.findTop5ByStatusOrderByCreatedAtDesc(PlaceStatus.APPROVED);

		// PlaceEntity를 HotelListDTO로 변환하여 리스트로 반환합니다.
		return latestPlaces.stream()
			.map(PlaceEntity::toLatestHotelListDTO) // 최신 호텔 리스트 DTO로 변환
			.collect(Collectors.toList());
	}

	@Override
	public List<HotelListDTO> TopPriceHotelList(Model model) {
	    // 상위 4개의 가장 높은 가격의 호텔들을 가져오기 위해 Pageable 설정
	    Pageable topFour = PageRequest.of(0, 4); // 첫 번째 페이지의 상위 4개 항목

	    // 상위 4개의 호텔들을 가져옵니다.
	    List<PlaceEntity> topPriceHotels = placeRepository.findTopByOrderByRoomPriceDesc(topFour);

	    // PlaceEntity를 거리 정보 없이 HotelListDTO로 변환하여 리스트로 반환합니다.
	    return topPriceHotels.stream()
	        .map(PlaceEntity::toHotelListDTOWithoutDistance) // 거리 정보 없이 DTO로 변환
	        .collect(Collectors.toList());
	}


	// 두 좌표 간의 거리를 계산하는 메서드 (Haversine 공식 사용)
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // 지구 반지름 (킬로미터 단위)
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) 
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
			* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; // 계산된 거리 (킬로미터 단위)
		
		// 소숫점 두 자리까지 포맷팅
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(distance));
	}

	@Override
	public List<HotelListDTO> getNearByHotels(double latitude, double longitude) {
		// 모든 장소를 가져옵니다.
		List<PlaceEntity> places = placeRepository.findAll();

		// 각 장소와 현재 위치 간의 거리를 계산하고, DTO로 변환하여 리스트로 반환합니다.
		return places.stream()
			.map(place -> {
				// 각 장소의 위도와 경도를 가져와서 거리 계산
				double placeLat = place.getLatitude();
				double placeLon = place.getLongitude();
				double distance = calculateDistance(latitude, longitude, placeLat, placeLon);

				// 거리 정보를 포함한 DTO로 변환
				return place.toHotelListDTOWithDistance(distance);
			})
			// 거리 순으로 정렬
			.sorted(Comparator.comparingDouble(HotelListDTO::getDistance))
			// 가까운 4개의 장소만 반환
			.limit(4)
			.collect(Collectors.toList());
	}
	
	
	@Override
	public List<HotelListDTO> getRecommendedHotels(Long userId) {
	    // 사용자의 선호 지역 리스트 가져오기
	    List<String> preferredRegions = activityLogRepository.findMostSearchedRegionByUserId(userId);
	    
	    if (preferredRegions.isEmpty()) {
	        // 선호 지역이 없는 경우 빈 리스트 반환 또는 기본 로직
	        return new ArrayList<>();
	    }

	    // 첫 번째 선호 지역을 사용
	    String preferredRegionStr = preferredRegions.get(0);
	    
	    // 문자열을 Region enum으로 변환
	    Region preferredRegion;
	    try {
	        preferredRegion = Region.valueOf(preferredRegionStr.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        // 만약 변환할 수 없는 경우 빈 리스트 반환 또는 기본 로직
	        return new ArrayList<>();
	    }

	    // 선호 지역 기반으로 숙소 추천
	    List<PlaceEntity> recommendedPlaces = placeRepository.findByRegion(preferredRegion);

	    // PlaceEntity를 HotelListDTO로 변환하여 리스트로 반환
	    return recommendedPlaces.stream()
	        .map(PlaceEntity::toHotelListDTOWithoutDistance)
	        .collect(Collectors.toList());
	}


	
}
