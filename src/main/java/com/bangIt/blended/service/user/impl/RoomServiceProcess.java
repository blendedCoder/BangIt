package com.bangIt.blended.service.user.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.room.RoomDetailDTO;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.entity.RoomEntity;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.domain.repository.RoomEntityRepository;
import com.bangIt.blended.service.user.RoomService;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class RoomServiceProcess implements RoomService {
    private final RoomEntityRepository roomRepository;
    private final PlaceEntityRepository placeRepository; // PlaceEntity를 조회하기 위해 추가

    @Override
    @Transactional(readOnly = true)
    public void roomDetailProcess(long roomId, Model model) {
        RoomEntity room = roomRepository.findById(roomId)
            .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId));
        RoomDetailDTO roomDTO = convertToDTO(room);
        model.addAttribute("room", roomDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public void listRoomsPlace(long placeId, Model model) {
        PlaceEntity place = placeRepository.findById(placeId)
            .orElseThrow(() -> new EntityNotFoundException("Place not found with id: " + placeId));
        List<RoomEntity> rooms = roomRepository.findByPlace(place);
        List<RoomDetailDTO> roomDTOs = rooms.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        model.addAttribute("rooms", roomDTOs);
    }

    private RoomDetailDTO convertToDTO(RoomEntity room) {
        return RoomDetailDTO.builder()
            .id(room.getId())
            .placeId(room.getPlace().getId())
            .roomName(room.getRoomName())
            .roomInformation(room.getRoomInformation())
            .roomPrice(room.getRoomPrice())
            .refundPolicy(room.getRefundPolicy())
            .checkInTime(room.getCheckInTime())
            .checkOutTime(room.getCheckOutTime())
            .guests(room.getGuests())
            .build();
    }

	@Override
	public void roomInfoProcess(Long roomId, Model model) {
		// TODO Auto-generated method stub
		
	}
}
