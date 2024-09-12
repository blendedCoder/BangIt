package com.bangIt.blended.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// Custom OAuth2 User Service와 Authentication Success Handler를 의존성 주입
	private final BangItOAuth2UserService bangItOAuth2UserService;
	private final BangItAuthenticationSuccessHandler bangItAuthenticationSuccessHandler;
	private final BangItAccessDeniedHandler bangItAccessDeniedHandler;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				// CSRF 설정: 특정 요청을 CSRF 보호에서 제외
				.csrf(csrf -> csrf.ignoringRequestMatchers("/generate-order-id"))
				// 요청에 대한 보안 설정
				.authorizeHttpRequests(authorize -> authorize
						// 정적 자원과 일부 엔드포인트에 대한 접근을 허용
						.requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
						// 루트, 로그인, 로그아웃, 파트너 및 기타 특정 경로에 대한 접근을 허용
						.requestMatchers("/", "/login", "/logout", "/partner-login", "/business-registration",
								"/bangItBot/**", "/success/**", "/fail/**", "/confirm/**", "/generate-order-id",
								"/get-nearby-hotels", "/search/**")
						.permitAll()
						// 특정 역할에 따라 접근 권한 부여
						.requestMatchers("/partner/**").hasRole("PARTNER") // "PARTNER" 역할 필요
						.requestMatchers("/admin").hasRole("USER") // "USER" 역할 필요
						.requestMatchers("/admin/**").hasRole("ADMIN") // "ADMIN" 역할 필요
						// 나머지 모든 요청에 대해 인증 필요
						.anyRequest().authenticated())
				// OAuth2 로그인 설정
				.oauth2Login(oauth2 -> oauth2
						// 로그인 페이지 경로 설정
						.loginPage("/login")
						// 사용자 정보를 가져오는 서비스 설정
						.userInfoEndpoint(userInfo -> userInfo.userService(bangItOAuth2UserService))
						// 로그인 성공 시 커스텀 성공 핸들러 설정
						.successHandler(bangItAuthenticationSuccessHandler))
				// 로그아웃 설정
				.logout(logout -> logout
						// 로그아웃 요청 경로 설정
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						// 로그아웃 성공 후 리디렉션 경로 설정
						.logoutSuccessUrl("/login")
						// 커스텀 로그아웃 핸들러 사용 시 활성화
						// .logoutSuccessHandler(customLogoutSuccessHandler) // (현재 비활성화됨)
						// 로그아웃 시 세션 무효화
						.invalidateHttpSession(true)
						// 인증 정보 제거
						.clearAuthentication(true)
						// 세션 쿠키 삭제
						.deleteCookies("JSESSIONID").permitAll())
				// 예외 처리 설정 (403 에러 발생 시 커스텀 핸들러 호출)
	            .exceptionHandling(exception -> exception
	                .accessDeniedHandler(bangItAccessDeniedHandler)
	            );
		// OAuth2 로그인 필터 전에 커스텀 필터 추가
		http.addFilterBefore(new SaveOriginalRequestFilter(), OAuth2LoginAuthenticationFilter.class);

		return http.build();
	}
}
