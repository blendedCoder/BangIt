package com.bangIt.blended.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bangIt.blended.domain.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BangItUserDetailsService implements UserDetailsService {

    // 의존성 주입: 사용자 정보를 데이터베이스에서 조회하기 위한 UserEntityRepository
    private final UserEntityRepository repository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(">>>>>>>> username :" + email);
        
        // 이메일을 기준으로 사용자를 데이터베이스에서 조회하고, CustomUserDetails로 래핑하여 반환
        return new BangItUserDetails(
            repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email))
        );
    }
}
