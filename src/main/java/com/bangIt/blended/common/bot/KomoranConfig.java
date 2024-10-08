package com.bangIt.blended.common.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Komoran 형태소 분석기의 설정을 담당하는 클래스입니다.
 * Spring의 @Configuration 애너테이션을 사용하여 컴포넌트 스캔 시
 * 이 클래스를 스프링 빈 설정 클래스에 포함시킵니다.
 */
@Configuration
public class KomoranConfig {

    /**
     * Komoran 객체를 생성하고 초기화합니다.
     * @return Komoran 형태소 분석기 객체
     */
    @Bean
    Komoran komoran() {
        // Komoran 객체를 FULL 모델로 초기화합니다.
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        try {
            // 클래스패스에서 user.dic 파일을 리소스로 로드합니다.
            Resource resource = new ClassPathResource("static/user.dic");
            
            // 리소스에서 InputStream을 얻습니다.
            try (InputStream inputStream = resource.getInputStream()) {
                // 임시 파일을 생성합니다.
                Path tempFile = Files.createTempFile("user", ".dic");
                
                // InputStream의 내용을 임시 파일에 복사합니다.
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                
                // Komoran 객체에 임시 파일의 경로를 사용자 정의 사전으로 설정합니다.
                komoran.setUserDic(tempFile.toString());
                
                // 애플리케이션 종료 시 임시 파일을 삭제하도록 설정합니다.
                tempFile.toFile().deleteOnExit();
            }
        } catch (IOException e) {
            // 파일을 로드하는 도중 IOException이 발생할 경우, 예외를 던집니다.
            throw new RuntimeException("Failed to load user dictionary", e);
        }

        // 설정된 Komoran 객체를 반환합니다.
        return komoran;
    }
}