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
import com.bangIt.blended.domain.entity.RoomEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.enums.Region;
import com.bangIt.blended.domain.repository.ActivityLogEntityRepositoty;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.service.IndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexServiceProcess implements IndexService {

    private final PlaceEntityRepository placeRepository;
    private final ActivityLogEntityRepositoty activityLogRepository;

    // 최신 숙소 조회
    @Override
    public List<HotelListDTO> getLatestHotels() {
        // 최신 숙소 5개의 ID만 가져오기
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> placeIds = placeRepository.findTop5IdsByStatus(PlaceStatus.APPROVED, pageable);

        // Fetch Join으로 PlaceEntity 가져오기
        List<PlaceEntity> latestPlaces = placeRepository.findAllByIdsWithImages(placeIds);

        // Room 데이터 병합
        latestPlaces.forEach(place -> {
            List<RoomEntity> rooms = placeRepository.findRoomsByPlaceId(place.getId());
            place.setRooms(rooms);
        });

        // DTO 변환 및 반환
        return latestPlaces.stream()
            .map(PlaceEntity::toLatestHotelListDTO)
            .collect(Collectors.toList());
    }


    // 상위 가격 숙소 조회
    @Override
    public List<HotelListDTO> TopPriceHotelList(Model model) {
        // 상위 4개의 숙소 ID만 가져오기
        Pageable pageable = PageRequest.of(0, 4);
        List<Long> placeIds = placeRepository.findTop5IdsByStatus(PlaceStatus.APPROVED, pageable);

        // Fetch Join으로 PlaceEntity 가져오기
        List<PlaceEntity> topPriceHotels = placeRepository.findAllByIdsWithImages(placeIds);

        // Room 데이터 병합
        topPriceHotels.forEach(place -> {
            List<RoomEntity> rooms = placeRepository.findRoomsByPlaceId(place.getId());
            place.setRooms(rooms);
        });

        // 가격 비교 및 정렬
        return topPriceHotels.stream()
            .sorted(Comparator.comparing(
                place -> place.getRooms().stream()
                    .map(RoomEntity::getRoomPrice)
                    .max(Long::compareTo)
                    .orElse(0L),
                Comparator.reverseOrder()))
            .map(PlaceEntity::toHotelListDTOWithoutDistance)
            .collect(Collectors.toList());
    }


    // 현재 위치 기준 가까운 숙소 조회
    @Override
    public List<HotelListDTO> getNearByHotels(double latitude, double longitude) {
        // 모든 숙소 ID 가져오기 (최대 100개로 제한)
        Pageable pageable = PageRequest.of(0, 100);
        List<Long> placeIds = placeRepository.findTop5IdsByStatus(PlaceStatus.APPROVED, pageable);

        // Fetch Join으로 PlaceEntity 가져오기
        List<PlaceEntity> places = placeRepository.findAllByIdsWithImages(placeIds);

        // Room 데이터 병합
        places.forEach(place -> {
            List<RoomEntity> rooms = placeRepository.findRoomsByPlaceId(place.getId());
            place.setRooms(rooms);
        });

        // 거리 계산 및 정렬
        return places.stream()
            .map(place -> {
                double distance = calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude());
                return place.toHotelListDTOWithDistance(distance);
            })
            .sorted(Comparator.comparingDouble(HotelListDTO::getDistance))
            .limit(4)
            .collect(Collectors.toList());
    }


    // 추천 숙소 조회
    @Override
    public List<HotelListDTO> getRecommendedHotels(Long userId) {
        // 선호 지역 가져오기
        List<String> preferredRegions = activityLogRepository.findMostSearchedRegionByUserId(userId);

        if (preferredRegions.isEmpty()) {
            return new ArrayList<>();
        }

        String preferredRegionStr = preferredRegions.get(0);
        Region preferredRegion;

        try {
            preferredRegion = Region.valueOf(preferredRegionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }

        // Fetch Join된 PlaceEntity 가져오기
        List<PlaceEntity> recommendedPlaces = placeRepository.findByRegionWithImages(preferredRegion);

        // Room 데이터 병합
        recommendedPlaces.forEach(place -> {
            List<RoomEntity> rooms = placeRepository.findRoomsByPlaceId(place.getId());
            place.setRooms(rooms);
        });

        return recommendedPlaces.stream()
            .map(PlaceEntity::toHotelListDTOWithoutDistance)
            .collect(Collectors.toList());
    }

    // 두 좌표 간 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        DecimalFormat df = new DecimalFormat("#.00");
        return Double.parseDouble(df.format(distance));
    }
}
