package com.bangIt.blended.common.security;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.enums.AuthProvider;
import com.bangIt.blended.domain.enums.Role;
import com.bangIt.blended.domain.repository.UserEntityRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BangItAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserEntityRepository userRepository;

    public BangItAuthenticationSuccessHandler(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = "/";
        
        String originalRequestUri = (String) request.getSession().getAttribute("originalRequest");
        if (originalRequestUri != null && originalRequestUri.equals("/partner-login")) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String socialId = null;
            String providerName = null;

            if (oAuth2User.getAttribute("sub") != null) {
                // Google
                socialId = oAuth2User.getAttribute("sub");
                providerName = "google";
            } else if (oAuth2User.getAttribute("response") != null) {
                // Naver
                Map<String, Object> responseData = (Map<String, Object>) oAuth2User.getAttribute("response");
                socialId = (String) responseData.get("id");
                providerName = "naver";
            } else if (oAuth2User.getAttribute("id") != null) {
                // Kakao
                socialId = oAuth2User.getAttribute("id").toString();
                providerName = "kakao";
            } else {
                throw new IllegalArgumentException("Unsupported provider or Social ID is missing");
            }

            System.out.println("Social ID: " + socialId);
            System.out.println("Provider: " + providerName);

            AuthProvider provider = AuthProvider.valueOf(providerName.toUpperCase());
            Optional<UserEntity> userOptional = userRepository.findBySocialIdAndProvider(socialId, provider);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                boolean hasPartnerRole = user.getRoles().contains(Role.PARTNER);
                if (!hasPartnerRole) {
                    targetUrl = "/business-registration";
                }
            } else {
                throw new IllegalArgumentException("User not found for social ID: " + socialId + " and provider: " + providerName);
            }
        }

        return targetUrl;
    }
}
