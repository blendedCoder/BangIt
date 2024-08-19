package com.bangIt.blended.controller.user.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bangIt.blended.service.user.PaymentService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class WidgetController {

    private final PaymentService paymentService;  // PaymentService 주입
    private final Logger logger = LoggerFactory.getLogger(WidgetController.class);

    @RequestMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {

        logger.info("confirmPayment 메서드 호출됨");

        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = String.valueOf(requestData.get("orderId")); // orderId를 String으로 변환
            amount = String.valueOf(requestData.get("amount"));   // amount도 동일하게 String으로 변환
            logger.info("요청 데이터 파싱 완료 - paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);
        } catch (ParseException e) {
            logger.error("JSON 파싱 오류: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);
        
        logger.info("토스페이 API 요청 준비 완료");

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";  // 여기에 실제 키를 사용해야 합니다.
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감됩니다.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));
        logger.info("토스페이 API 요청 전송");

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        logger.info("토스페이 API 응답 코드: {}", code);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        logger.info("토스페이 API 응답 파싱 완료");

        if (isSuccess) {
            // 결제가 성공하면 결제 정보를 DB에 저장합니다.
            Long amountValue = Long.parseLong(amount);  // amount는 여전히 Long으로 처리
            logger.info("결제 성공 - DB에 결제 정보 저장 시작");
            paymentService.savePaymentInfo(paymentKey, orderId, amountValue);  // orderId를 문자열로 전달
            logger.info("DB에 결제 정보 저장 완료");
        }

        return ResponseEntity.status(code).body(jsonObject);
    }
}
