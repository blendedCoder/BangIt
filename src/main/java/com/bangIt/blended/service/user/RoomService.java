package com.bangIt.blended.service.user;

import org.springframework.ui.Model;

public interface RoomService{

	void roomInfoProcess(Long roomId, Model model);

	void roomDetailProcess(long roomId, Model model);

	void listRoomsPlace(long placeId, Model model);

}
