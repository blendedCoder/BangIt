package com.bangIt.blended.common.util;

import com.bangIt.blended.common.util.LogActivity;
import com.bangIt.blended.domain.entity.ActivityLogEntity;
import com.bangIt.blended.domain.entity.UserEntity;
import com.bangIt.blended.domain.enums.ActivityType;
import com.bangIt.blended.domain.repository.ActivityLogEntityRepositoty;
import com.bangIt.blended.domain.repository.UserEntityRepository;
import com.bangIt.blended.common.security.BangItUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

// AOP를 사용하여 로깅 기능을 구현하기 위한 Aspect 클래스
@Aspect
// Spring 컨테이너에 의해 관리되는 빈으로 등록
@Component
public class LogActivityAspect {
    // 로깅을 위한 Logger 인스턴스 생성
    private static final Logger logger = LoggerFactory.getLogger(LogActivityAspect.class);

    // 활동 로그를 저장하기 위한 리포지토리
    private final ActivityLogEntityRepositoty activityLogRepository;
    // 사용자 정보를 조회하기 위한 리포지토리
    private final UserEntityRepository userEntityRepository;
    // SpEL(Spring Expression Language) 파서 - 동적 표현식 평가에 사용
    private final ExpressionParser parser = new SpelExpressionParser();

    // 생성자를 통한 의존성 주입
    public LogActivityAspect(ActivityLogEntityRepositoty activityLogRepository, UserEntityRepository userEntityRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userEntityRepository = userEntityRepository;
    }

    // @LogActivity 어노테이션이 붙은 메소드 실행 전후에 로직을 수행하는 Around 어드바이스
    @Around("@annotation(logActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint, LogActivity logActivity) throws Throwable {
        // 현재 인증된 사용자 정보 조회
        UserEntity user = getCurrentUser();

        // 원래 메서드 실행 및 결과 저장
        Object result = joinPoint.proceed();

        // 로그인한 사용자만 활동 로그 기록
        if (user != null) {
            // 어노테이션에서 활동 유형 가져오기
            ActivityType activityType = logActivity.activityType();
            // 상세 기록 해석
            String detailRecord = resolveDetailRecord(logActivity.detailRecordExpression(), joinPoint, result);

            // 활동 로그 엔티티 생성
            ActivityLogEntity log = ActivityLogEntity.builder()
                .user(user)
                .activityType(activityType)
                .description(activityType.getDescription())
                .detailRecord(detailRecord)
                .timestamp(LocalDateTime.now())
                .build();
            // 로그 저장
            activityLogRepository.save(log);
            logger.info("Activity logged for user ID: {}, type: {}", user.getId(), activityType);
        } else {
            // 인증되지 않은 사용자의 경우 로그만 남기고 넘어감
            logger.debug("Activity not logged: No authenticated user");
        }

        // 원래 메서드의 실행 결과 반환
        return result;
    }

    // 현재 인증된 사용자의 UserEntity를 반환하는 private 메소드
    private UserEntity getCurrentUser() {
        // 현재 보안 컨텍스트에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 존재하고, 인증되었으며, CustomUserDetails 타입인 경우
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof BangItUserDetails) {
            BangItUserDetails userDetails = (BangItUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            // 사용자 ID로 데이터베이스에서 사용자 정보 조회
            return userEntityRepository.findById(userId)
                .orElseGet(() -> {
                    // 사용자를 찾지 못한 경우 경고 로그 남기고 null 반환
                    logger.warn("User not found in database for ID: {}", userId);
                    return null;
                });
        }
        // 인증되지 않은 경우 null 반환
        return null;
    }

    // detailRecordExpression을 해석하여 실제 값으로 변환하는 private 메소드
    private String resolveDetailRecord(String detailRecordExpression, ProceedingJoinPoint joinPoint, Object result) {
        if (detailRecordExpression.isEmpty()) {
            return "";
        }

        // SpEL 표현식을 평가하기 위한 컨텍스트 설정
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("result", result); // 메서드 실행 결과를 컨텍스트에 추가
        context.setVariable("args", joinPoint.getArgs()); // 메서드 인자를 컨텍스트에 추가

        // 표현식 파싱 및 평가
        Expression expression = parser.parseExpression(detailRecordExpression);
        return expression.getValue(context, String.class);
    }
}