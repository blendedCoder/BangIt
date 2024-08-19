package com.bangIt.blended.common.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class TossPaymentConfig {

    @Value("${payment.toss.test_client_api_key}")
    private String testClientApiKey;
    
    @Value("${payment.toss.test_secret_api_key}")
    private String testSecretApiKey;
    
    @Value("${payment.toss.success_url}")
    private String successUrl;
    
    @Value("${payment.toss.fail_url}")
    private String failUrl;
    
    // 결제 생성 요청을 위한 URL
    public static final String PAYMENT_URL = "https://pay.toss.im/api/v2/payments";

    // 결제 확인(승인) 요청을 위한 URL
    public static final String PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public URI getPaymentUrl() {
        try {
            return new URI(PAYMENT_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI syntax for PAYMENT_URL: " + PAYMENT_URL, e);
        }
    }

    public URI getPaymentConfirmUrl() {
        try {
            return new URI(PAYMENT_CONFIRM_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI syntax for PAYMENT_CONFIRM_URL: " + PAYMENT_CONFIRM_URL, e);
        }
    }
}
