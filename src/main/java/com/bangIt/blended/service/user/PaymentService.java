package com.bangIt.blended.service.user;

import com.bangIt.blended.domain.dto.user.payment.PaymentRequestDTO;

public interface PaymentService {


	String createPayment(PaymentRequestDTO requestDTO);

	boolean validatePayment(String paymentKey, String orderId, Long amount);

	void savePaymentInfo(String paymentKey, String reservationId, Long amount);

	Long getAmountByReservationId(Long reservationId);

	//Long getAmountByReservationId(Long reservationId);
	
}
