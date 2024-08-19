package com.bangIt.blended.service.partner;

import java.util.List;

import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.room.RoomListDTO;
import com.bangIt.blended.domain.dto.room.RoomSaveDTO;
import com.bangIt.blended.domain.enums.PlaceStatus;

public interface PartnerRoomService {

	void saveRoom(RoomSaveDTO dto);

	List<RoomListDTO> listProcess(Long placeId, PlaceStatus placeStatus);


}
