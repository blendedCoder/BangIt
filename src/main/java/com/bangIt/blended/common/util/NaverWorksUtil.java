package com.bangIt.blended.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.UUID;

@Component
public class NaverWorksUtil {

    //토큰요청을 위한 url
    private static final String AUTH_URL = "https://auth.worksmobile.com/oauth2/v2.0/authorize";
    private static final String TOKEN_URL = "https://auth.worksmobile.com/oauth2/v2.0/token";
    private static final String API_BASE_URL = "https://www.worksapis.com/v1.0";

    //토큰 요청에 필요한 정보들(프로퍼티즈에 정의되어 있음)
    @Value("${spring.naver.works.client.id}")
    private String clientId;

    @Value("${spring.naver.works.client.secret}")
    private String clientSecret;

    @Value("${spring.naver.works.client.redirectUri}")
    private String redirectUri;

    @Value("${spring.naver.works.client.scope}")
    private String scope;

    //
    private final RestTemplate restTemplate;
    

    //
    public NaverWorksUtil(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();
    }

    //최종적으로 네이버가 요구하는 url 조합 생성
    public String getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        return AUTH_URL + "?client_id=" + clientId +
               "&redirect_uri=" + redirectUri +
               "&scope=" + scope +
               "&response_type=code" +
               "&state=" + state;
    }

    //ResponseEntity를 사용해 post요청을 하기 위해 header와, body정보를 호환성이 맞는 map을 사용해 조합하여 토큰을 발급 받은 후
    //발급 받은 토큰을 TokenResponse에 저장
    public TokenResponse getAccessToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", authorizationCode);
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(TOKEN_URL, request, TokenResponse.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Successfully retrieved access token");
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch token. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching token: " + e.getMessage());
            throw new RuntimeException("Failed to fetch token", e);
        }
    }

    // 각 서비스에서 적용될 HTTP 요청 get 메서드
    //이 메서드를 사용해서 토큰과 엔드포인드를 전달 받음
    public ResponseEntity<JsonNode> get(String accessToken, String endpoint) {
        HttpHeaders headers = new HttpHeaders();//http 요청 헤더 설정을 위한 객체 
        headers.set("Authorization", "Bearer " + accessToken);//Authorization"은 HTTP 표준 헤더,"Bearer"는 토큰 기반 인증의 한 유형,억세스토큰
        HttpEntity<String> entity = new HttpEntity<>(headers);//HttpEntity는 http요청과 응답을 나타내는 클래스,RestTemplate의 메서드들(예: exchange())은 이 객체를 파라미터로 받기를 요구함)
        
        //실제 요청할 url주소,요청 메서드, 포함시킬 헤더 정보,요청 후 응답받은 json을 JsonNode 형태로 받기 위함
        String url = API_BASE_URL + endpoint;
        return restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
    }
    
    
     // 각 서비스에서 제네릭을 사용해 적용될 HTTP 요청 post 메서드
    public ResponseEntity<JsonNode> post(String accessToken, String endpoint, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        String url = API_BASE_URL + endpoint;
        return restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
    }
    

    
    
    
    public ResponseEntity<JsonNode> delete(String accessToken, String endpoint) {
        HttpHeaders headers = new HttpHeaders();//http 요청 헤더 설정을 위한 객체 
        headers.set("Authorization", "Bearer " + accessToken);//Authorization"은 HTTP 표준 헤더,"Bearer"는 토큰 기반 인증의 한 유형,억세스토큰
        HttpEntity<String> entity = new HttpEntity<>(headers);//HttpEntity는 http요청과 응답을 나타내는 클래스,RestTemplate의 메서드들(예: exchange())은 이 객체를 파라미터로 받기를 요구함)
        
        //실제 요청할 url주소,요청 메서드, 포함시킬 헤더 정보,요청 후 응답받은 json을 JsonNode 형태로 받기 위함
        String url = API_BASE_URL + endpoint;
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, JsonNode.class);
    }
    
    
    
    
    
    
    // 파일 다운로드를 위한 메서드
    public ResponseEntity<byte[]> getFile(String accessToken, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = API_BASE_URL + endpoint;
        return restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
    }
    
    // 파일 업로드를 위한 메서드 
    public ResponseEntity<String> putFile(String accessToken, String url, byte[] fileContent,String filename,String contentType) {
    	  HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.MULTIPART_FORM_DATA);
          headers.set("Authorization", "Bearer " + accessToken);

          MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
          HttpHeaders fileHeaders = new HttpHeaders();
          fileHeaders.setContentType(MediaType.parseMediaType(contentType));
          fileHeaders.setContentDispositionFormData("Filedata", filename);
          
          HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContent, fileHeaders);
          body.add("Filedata", fileEntity);

          HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

          return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

    }
    
    
    

    // TokenResponse 클래스 정의
    @Setter
    @Getter
    public static class TokenResponse {
        private String access_token;
        private String token_type;
        private int expires_in;
        private String refresh_token;
        private String scope;
    }
}