package com.bangIt.blended.common.security;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.bangIt.blended.domain.entity.UserEntity;

import lombok.Getter;

// CustomUserDetails 클래스는 Spring Security의 User 클래스와 OAuth2User 인터페이스를 모두 구현함으로써
// 소셜 로그인 사용자와 일반 사용자를 모두 처리할 수 있도록 구성됩니다.
@Getter // Lombok 어노테이션을 통해 게터 메서드를 자동으로 생성
public class BangItUserDetails extends User implements OAuth2User {

    private static final long serialVersionUID = 1L;
    
    // 사용자와 관련된 추가 정보 필드
    private Long id; 
    private String email; // username과 동일
    private String name;
    private Map<String, Object> attributes;

    // UserEntity로부터 정보를 받아 사용자 정보 설정
    public BangItUserDetails(UserEntity entity) {
        // 부모 클래스 User 생성자를 호출하여 사용자 인증 정보 설정
        super(entity.getEmail(), entity.getPassword(),
              entity.getRoles().stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // 역할을 SimpleGrantedAuthority로 변환
              .collect(Collectors.toSet())); // Set으로 변환하여 Spring Security에서 사용

        this.id = entity.getId();
        this.email = entity.getEmail();
        this.name = entity.getUsername();
    }

    // OAuth2 사용자 정보를 추가로 설정하는 생성자
    public BangItUserDetails(UserEntity entity, Map<String, Object> attributes) {
        this(entity); // 기존 생성자를 호출하여 기본 정보 설정
        this.attributes = attributes; // OAuth2 관련 속성을 추가로 설정
    }

    // OAuth2User 인터페이스의 추상 메서드를 구현하여 OAuth2 사용자 속성을 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User 인터페이스의 추상 메서드를 구현하여 OAuth2 사용자의 이름을 반환
    @Override
    public String getName() {
        return this.name;
    }
}
