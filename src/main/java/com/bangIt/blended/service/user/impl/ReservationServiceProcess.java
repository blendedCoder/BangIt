package com.bangIt.blended.service.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bangIt.blended.domain.dto.room.ReservationDTO;
import com.bangIt.blended.domain.entity.ReservationEntity;
import com.bangIt.blended.domain.entity.RoomEntity;
import com.bangIt.blended.domain.repository.ReservationEntityRepository;
import com.bangIt.blended.domain.repository.RoomEntityRepository;
import com.bangIt.blended.service.user.ReservationService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReservationServiceProcess implements ReservationService {
	
	private final ReservationEntityRepository reservationRepository;
	private final RoomEntityRepository roomRepository;
	
	@Override
	public void getReservationInfo(Long reservationId,Long roomId, Model model) {
		
		 Optional<ReservationEntity> reservedInfo = reservationRepository.findById(reservationId);
		 model.addAttribute("reservedInfo", reservedInfo.stream().map(ReservationEntity::toReservationDTO)
					.collect(Collectors.toList()));
	}

	@Override
	public ReservationDTO getReservationById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLatestReservationId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public Long getLatestReservationId() { // 가장 최근 예약을 가져오는 로직 (필요에 맞게
	 * 구현) ReservationEntity latestReservation =
	 * reservationRepository.findTopByOrderByIdDesc(); return
	 * latestReservation.getId(); // 혹은 다른 방식으로 reservationId를 가져올 수 있음 }
	 */

}
