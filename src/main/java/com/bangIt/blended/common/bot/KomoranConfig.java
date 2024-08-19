package com.bangIt.blended.common.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;


/**
 * Komoran 형태소 분석기 설정
 */
@Configuration
public class KomoranConfig {

	private String USER_DIC = "user.dic"; // 사용자 정의 사전 파일 경로

	@Bean
	Komoran komoran() {
		// Komoran 객체를 FULL 모델로 초기화합니다.
		// FULL 모델은 Komoran의 가장 포괄적인 모델로, 
		// 형태소 분석을 위한 전체적인 사전과 규칙을 포함
		Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
		
		// 사용자 정의 사전 파일을 설정
		komoran.setUserDic(USER_DIC);
		
		// 설정된 Komoran 객체를 반환
		return komoran;
	}

}
