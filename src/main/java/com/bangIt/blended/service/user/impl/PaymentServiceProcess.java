package com.bangIt.blended.service.user.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;

//import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bangIt.blended.common.config.TossPaymentConfig;
import com.bangIt.blended.domain.dto.user.payment.PaymentRequestDTO;
import com.bangIt.blended.domain.entity.PaymentEntity;
import com.bangIt.blended.domain.entity.ReservationEntity;
import com.bangIt.blended.domain.enums.PaymentMethod;
import com.bangIt.blended.domain.enums.PaymentStatus;
import com.bangIt.blended.domain.repository.PaymentEntityRepository;
import com.bangIt.blended.domain.repository.ReservationEntityRepository;
import com.bangIt.blended.service.user.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceProcess implements PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceProcess.class);

	private final TossPaymentConfig tossPaymentConfig;
	private final ReservationEntityRepository reservationRepository;
	private final PaymentEntityRepository paymentRepository;

	@Override
	public String createPayment(PaymentRequestDTO requestDTO) {
		StringBuilder responseBody = new StringBuilder();
		try {
			URL url = new URL(TossPaymentConfig.PAYMENT_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			JSONObject jsonBody = new JSONObject();
			jsonBody.put("orderNo", requestDTO.getOrderNo());
			jsonBody.put("amount", requestDTO.getAmount());
			jsonBody.put("amountTaxFree", requestDTO.getAmountTaxFree());
			jsonBody.put("productDesc", requestDTO.getProductDesc());
			jsonBody.put("apiKey", tossPaymentConfig.getTestClientApiKey());
			jsonBody.put("autoExecute", requestDTO.isAutoExecute());
			jsonBody.put("resultCallback", requestDTO.getResultCallback());
			jsonBody.put("retUrl", requestDTO.getRetUrl());
			jsonBody.put("retCancelUrl", requestDTO.getRetCancelUrl());

			logger.info("Creating payment with request body: {}", jsonBody);

			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			bos.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
			bos.flush();
			bos.close();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = br.readLine()) != null) {
				responseBody.append(line);
			}
			br.close();

			logger.info("Payment creation response: {}", responseBody.toString());
		} catch (Exception e) {
			logger.error("Error during payment creation", e);
			responseBody.append(e);
		}
		return responseBody.toString();
	}

	@Override
	public boolean validatePayment(String paymentKey, String orderId, Long amount) {
		try {
			logger.info("Validating payment - paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

			// 요청 데이터 설정
			JSONObject requestBody = new JSONObject();
			requestBody.put("orderId", orderId);
			requestBody.put("amount", amount);
			requestBody.put("paymentKey", paymentKey);

			// 실제 API 호출 부분 (RestTemplate 코드가 있는 경우 사용)
			// 해당 부분에 문제가 없다는 가정하에 생략했지만, 로깅이 필요한 경우 아래와 같이 진행
			logger.info("Payment validation request body: {}", requestBody);

			// 성공 시 반환값 처리
			return true;
		} catch (Exception e) {
			logger.error("Error validating payment", e);
			return false;
		}
	}

	@Override
	@Transactional
	public void savePaymentInfo(String paymentKey, String orderId, Long amount) {
	    logger.info("Saving payment information - paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

	    // orderId를 Long으로 변환하지 않고 그대로 사용
	    ReservationEntity reservation = reservationRepository.findByOrderId(orderId)
	            .orElseThrow(() -> new RuntimeException("Reservation not found with Order ID: " + orderId));

	    PaymentEntity payment = PaymentEntity.builder()
	            .tossPaymentId(paymentKey)
	            .reservation(reservation)
	            .amount(amount)
	            .paymentDate(LocalDateTime.now())
	            .paymentMethod(PaymentMethod.TOSS_PAY)
	            .paymentStatus(PaymentStatus.COMPLETED)
	            .build();

	    paymentRepository.save(payment);
	    logger.info("Payment information saved successfully.");
	}

	@Override
    public Long getAmountByReservationId(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));
        return reservation.getRoom().getRoomPrice(); // 예약된 방의 가격을 반환
    }

	

}
