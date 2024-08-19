package com.bangIt.blended.controller.user.payment;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.bangIt.blended.common.config.TossPaymentConfig;
import com.bangIt.blended.domain.dto.room.ReservationDTO;
import com.bangIt.blended.domain.dto.user.payment.PaymentRequestDTO;
import com.bangIt.blended.service.user.PaymentService;
import com.bangIt.blended.service.user.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	private final TossPaymentConfig tossPaymentConfig;
	private final PaymentService paymentService;
	private final ReservationService reservationService;

	@GetMapping("/payment")
	public String paymentPage(Model model) {
		// 예약 ID를 가져오는 로직 (필요에 따라 이 부분을 수정)
		//Long reservationId = reservationService.getLatestReservationId(); // 가정: 가장 최근 예약 ID를 가져옴
		// 예약 ID를 모델에 추가
		//model.addAttribute("reservationId", reservationId);
		model.addAttribute("testClientApiKey", tossPaymentConfig.getTestClientApiKey());
		model.addAttribute("successUrl", tossPaymentConfig.getSuccessUrl());
		model.addAttribute("failUrl", tossPaymentConfig.getFailUrl());
		return "views/user/payment/payment";
	}

	@GetMapping("/payment/reservation/{id}")
	@ResponseBody
	public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
		logger.info("Fetching reservation with ID: {}", id);
		ReservationDTO reservation = reservationService.getReservationById(id);
		return ResponseEntity.ok(reservation);
	}

	  @GetMapping("/getAmount")
	    @ResponseBody
	    public ResponseEntity<?> getAmount(@RequestParam("reservationId") Long reservationId) {
	        try {
	            Long amount = paymentService.getAmountByReservationId(reservationId);
	            return ResponseEntity.ok(Collections.singletonMap("amount", amount));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Collections.singletonMap("success", false));
	        }
	    }

	@GetMapping("/success")
	public String paymentSuccess(@RequestParam("paymentKey") String paymentKey, @RequestParam("orderId") String orderId,
			@RequestParam("amount") Long amount, Model model) {
		// 결제 성공 처리 로직
		boolean isPaymentValid = paymentService.validatePayment(paymentKey, orderId, amount);

		if (isPaymentValid) {
			// orderId를 Long으로 변환하지 않고 그대로 전달
			paymentService.savePaymentInfo(paymentKey, orderId, amount);
			model.addAttribute("message", "결제가 성공적으로 처리되었습니다.");
		} else {
			model.addAttribute("message", "결제 검증 실패. 고객센터로 문의해 주세요.");
		}

		model.addAttribute("paymentKey", paymentKey);
		model.addAttribute("orderId", orderId);
		model.addAttribute("amount", amount);

		return "views/user/payment/success";
	}

	@GetMapping("/fail")
	public String paymentFail(@RequestParam String message, @RequestParam String code, Model model) {
		logger.error("Payment failed - message: {}, code: {}", message, code);
		model.addAttribute("message", message);
		model.addAttribute("code", code);
		return "user/payment/fail";
	}

	@PostMapping("/create")
	@ResponseBody
	public String createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
		logger.info("Received PaymentRequestDTO: {}", paymentRequestDTO);
		return paymentService.createPayment(paymentRequestDTO);
	}
}
