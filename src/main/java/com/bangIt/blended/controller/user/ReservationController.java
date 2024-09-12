package com.bangIt.blended.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.bangIt.blended.common.security.BangItUserDetails;
import com.bangIt.blended.domain.dto.reservation.ReservationSaveDTO;
import com.bangIt.blended.service.user.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@Controller
public class ReservationController {

	private final ReservationService service;

	//예약 등록
	@PostMapping("/reservationSave")
	public String placeSave(@AuthenticationPrincipal BangItUserDetails userDetails, ReservationSaveDTO dto) {
        Long reservationId = service.saveProcess(userDetails.getId(), dto);
        return "redirect:/payment?reservationId=" + reservationId;
    }
	
	//예약 목록 조회
	@GetMapping("/reservation")
	public String reservationList(@AuthenticationPrincipal BangItUserDetails userDetails, Model model) {
			service.listProcess(userDetails.getId(), model);
		return "views/user/reservation/reservationList";
	}
	
	// 예약 상세정보 조회
    @GetMapping("/reservationDetails/{id}")
    public String reservationDetail(@PathVariable("id") Long id, Model model) {
        service.detailProcess(id, model);
        return "views/user/reservation/reservationDetails :: reservationDetailsFragment";
    }
	
}
