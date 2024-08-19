package com.bangIt.blended.common.bot;

import com.bangIt.blended.domain.dto.place.PlaceDetailDTO;
import com.bangIt.blended.domain.enums.PlaceType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationServiceImpl implements AccommodationService {

	@Override
	public List<PlaceDetailDTO> search(String region, String detailedAddress, PlaceType type) {
		// TODO: 실제 검색 로직 구현
		// 이 메서드는 데이터베이스나 외부 API를 통해 실제 숙소 정보를 검색해야 합니다.
		// 현재는 빈 리스트를 반환하는 더미 구현입니다.
		return List.of();
	}
}