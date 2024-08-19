package com.bangIt.blended.service.user;

import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.room.ReservationDTO;

public interface ReservationService {

	void getReservationInfo(Long reservationId, Long roomId , Model model);

	ReservationDTO getReservationById(Long id);

	Long getLatestReservationId();

}
