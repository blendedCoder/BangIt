package com.bangIt.blended.common.bot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;

/**
 * KomoranService 클래스는 Komoran 형태소 분석기를 사용하여 입력된 질문을 분석하고,
 * 명사와 특정 형태소를 추출하는 서비스를 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class KomoranService {

	private final Komoran komoran; // Komoran 형태소 분석기 객체

	// 질문을 분석하고 명사만 추출하여 반환하는 메서드
	public Set<String> analyzeTokenAndGetNouns(String question) {
		// Komoran 형태소 분석기를 사용하여 질문 내용을 분석
		KomoranResult analyzeResult = komoran.analyze(question);
		// 분석된 결과중 명사만 수집->의도분석을 위해서 심플하게 명사만 사용해볼께요
		Set<String> nouns = analyzeResult.getNouns().stream().collect(Collectors.toSet());
		
		// 추출한 명사들을 콘솔에 출력 (디버깅 용도)
		for (String noun : nouns) {
			System.out.println("명사:" + noun);
		}
		
		// 특정 형태소 태그 "SN" (숫자)을 가진 형태소를 리스트로 추출합니다.
		List<String> snList = analyzeResult.getMorphesByTags("SN");
		
		// 추출한 "SN" 형태소들을 콘솔에 출력 (디버깅 용도)
		for (String sn : snList) {
			System.out.println("sn:" + sn);
		}
		
		// 추출한 명사 집합을 반환
		return nouns;

	}
}
