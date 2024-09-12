package com.bangIt.blended.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Component
public class BangItLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";
    private static final String KAKAO_LOGOUT_REDIRECT_URL = "https://kauth.kakao.com/oauth/logout";
    private static final String CLIENT_ID_KAKAO = "";
    private static final String LOGOUT_REDIRECT_URI = "http://localhost:8080/";
    private static final String GOOGLE_LOGOUT_URL = "https://accounts.google.com/logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080/";
    private static final String NAVER_LOGOUT_URL = "https://nid.naver.com/oauth2.0/token";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String accessToken = null;

        if (authentication instanceof OAuth2AuthenticationToken) {
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

            if (session != null) {
                accessToken = (String) session.getAttribute("accessToken");
                session.invalidate();
            }

            switch (registrationId.toLowerCase()) {
                case "kakao":
                    if (accessToken != null) {
                        logoutFromKakao(accessToken);
                    }
                    String kakaoLogoutUrl = KAKAO_LOGOUT_REDIRECT_URL +
                            "?client_id=" + CLIENT_ID_KAKAO +
                            "&logout_redirect_uri=" + request.getContextPath() + LOGOUT_REDIRECT_URI;
                    response.sendRedirect(kakaoLogoutUrl);
                    break;

                case "google":
                    response.sendRedirect(GOOGLE_LOGOUT_URL);
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + LOGOUT_REDIRECT_URI);
                    break;
            }
        } else {
            response.sendRedirect(request.getContextPath() + LOGOUT_REDIRECT_URI);
        }
    }

    private void logoutFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(KAKAO_LOGOUT_URL, HttpMethod.POST, entity, String.class);
    }

	/*
	 * private void logoutFromNaver(String accessToken) { RestTemplate restTemplate
	 * = new RestTemplate(); String url = NAVER_LOGOUT_URL +
	 * "?grant_type=delete&client_id=" + CLIENT_ID_NAVER + "&client_secret=" +
	 * CLIENT_SECRET_NAVER + "&access_token=" + accessToken +
	 * "&service_provider=NAVER";
	 * 
	 * ResponseEntity<String> response = restTemplate.postForEntity(url, null,
	 * String.class); if (response.getStatusCode().is2xxSuccessful()) {
	 * System.out.println("Naver logout successful."); } else {
	 * System.out.println("Naver logout failed. Status code: " +
	 * response.getStatusCode().value()); } }
	 */
}
