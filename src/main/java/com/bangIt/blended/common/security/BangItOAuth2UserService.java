package com.bangIt.blended.common.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.enums.AuthProvider;
import com.bangIt.blended.domain.enums.Role;
import com.bangIt.blended.domain.repository.PartnerEntityRepository;
import com.bangIt.blended.domain.repository.UserEntityRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BangItOAuth2UserService extends DefaultOAuth2UserService {

    // 의존성 주입: 사용자 정보와 관련된 레포지토리 및 기타 서비스
    private final UserEntityRepository repository;
    private final PartnerEntityRepository partnerRepository;
    private final PasswordEncoder pw;
    private final HttpSession session;
    private final HttpServletRequest request;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2 사용자 정보를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 클라이언트 등록 ID (Google, Naver, Kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 속성(attributes)을 수정 가능한 맵으로 복사
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("registrationId", registrationId);

        // 디버깅용 로그 출력
        System.out.println("registrationID: " + registrationId);
        attributes.forEach((key, value) -> System.out.println(key + ": " + value));

        // 사용자 정보를 처리하여 UserDetails를 반환
        return socialUser(oAuth2User, registrationId, attributes);
    }

    private OAuth2User socialUser(OAuth2User oAuth2User, String registrationId, Map<String, Object> attributes) {
        String email = null;
        String username = null;
        String socialId = null;
        AuthProvider provider = null;

        // 제공자별로 사용자 정보를 추출하여 소셜 ID, 이메일, 사용자명, 제공자 정보를 설정
        if (registrationId.equals("google")) {
            email = (String) attributes.getOrDefault("email", null);
            username = (String) attributes.getOrDefault("name", null);
            socialId = (String) attributes.getOrDefault("sub", null);
            provider = AuthProvider.GOOGLE;
        } else if (registrationId.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                email = (String) response.getOrDefault("email", null);
                username = (String) response.getOrDefault("nickname", (String) response.get("name"));
                socialId = String.valueOf(response.get("id"));
            }
            provider = AuthProvider.NAVER;
        } else if (registrationId.equals("kakao")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) response.getOrDefault("email", null);
            username = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");
            Long kakaoId = (Long) attributes.getOrDefault("id", null);
            socialId = String.valueOf(kakaoId);
            provider = AuthProvider.KAKAO;
        }

        // 소셜 ID 및 제공자 정보를 로그로 출력
        System.out.println("Initial Social ID: " + socialId);
        System.out.println("Provider: " + provider);

        // 소셜 ID가 없으면 예외 발생
        if (socialId == null || socialId.isEmpty()) {
            throw new IllegalArgumentException("Failed to retrieve social ID from " + provider + ". Social ID is null or empty.");
        }

        // 데이터베이스에서 소셜 ID와 제공자 정보로 사용자를 조회
        Optional<UserEntity> existingUserOptional = repository.findBySocialIdAndProvider(socialId, provider);

        if (existingUserOptional.isEmpty()) {
            // 사용자가 존재하지 않으면 새 사용자 생성
            UserEntity newUser = UserEntity.builder()
                .email(email)
                .username(username)
                .password(pw.encode(String.valueOf(System.currentTimeMillis()))) // 임시 비밀번호 설정
                .socialId(socialId)
                .provider(provider)
                .build()
                .addRole(Role.USER); // 기본 역할(USER) 부여

            // 새 사용자를 저장하고 CustomUserDetails로 반환
            return new BangItUserDetails(repository.save(newUser), attributes);
        } else {
            // 사용자가 존재하는 경우
            UserEntity existingUser = existingUserOptional.get();

            // 기존 사용자의 소셜 ID를 로그로 출력
            System.out.println("Existing User Social ID: " + existingUser.getSocialId());

            // 현재 요청 URI가 /partner-login인지 확인하고, ROLE_PARTNER 추가 여부 판단
            if (request.getRequestURI().equals("/partner-login") && 
                existingUser.getRoles().contains(Role.USER) && 
                !existingUser.getRoles().contains(Role.PARTNER)) {
                
                // 세션에 사용자 ID 저장
                session.setAttribute("businessRegistrationUserId", existingUser.getId());
                
                // business-registration 페이지로 리다이렉트하기 위한 플래그 설정
                session.setAttribute("redirectToBusinessRegistration", true);
            }

            // 기존 사용자를 CustomUserDetails로 반환
            return new BangItUserDetails(existingUser, attributes);
        }
    }
}
