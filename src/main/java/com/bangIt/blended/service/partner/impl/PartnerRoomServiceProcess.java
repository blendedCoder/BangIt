package com.bangIt.blended.service.partner.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.room.RoomListDTO;
import com.bangIt.blended.domain.dto.room.RoomSaveDTO;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.RoomEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.domain.repository.RoomEntityRepository;
import com.bangIt.blended.service.partner.PartnerRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PartnerRoomServiceProcess implements PartnerRoomService{
	
	private final RoomEntityRepository roomRepository;
    private final PlaceEntityRepository placeRepository;

    @Override
    @Transactional
    public void saveRoom(RoomSaveDTO dto) {
        PlaceEntity placeEntity = placeRepository.findById(dto.getPlaceId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid Place ID"));
        
        RoomEntity roomEntity = toRoomEntity(dto, placeEntity);
        roomRepository.save(roomEntity);
    }

    //방 등록
    private RoomEntity toRoomEntity(RoomSaveDTO dto, PlaceEntity placeEntity) {
        return RoomEntity.builder()
                .place(placeEntity)
                .roomName(dto.getRoomName())
                .roomInformation(dto.getRoomInformation())
                .roomPrice(dto.getRoomPrice())
                .roomStatus(RoomEntity.RoomStatus.valueOf(dto.getRoomStatus()))
                .refundPolicy(dto.getRefundPolicy())
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .guests(dto.getGuests())
                .build();
    }

    private RoomListDTO toRoomListDTO(RoomEntity room) {
        return RoomListDTO.builder()
            .id(room.getId())
            .roomName(room.getRoomName())
            .roomPrice(room.getRoomPrice())
            .roomStatus(room.getRoomStatus())
            .guests(room.getGuests())
            .placeName(room.getPlace().getName())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomListDTO> listProcess(Long placeId, PlaceStatus placeStatus) {
        PlaceEntity place = placeRepository.findById(placeId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Place ID: " + placeId));
        
        if (place.getStatus() != placeStatus) {
            throw new IllegalStateException("Place status mismatch");
        }
        
        List<RoomEntity> rooms = roomRepository.findByPlace(place);
        return rooms.stream()
            .map(this::toRoomListDTO)
            .collect(Collectors.toList());
    }
}
