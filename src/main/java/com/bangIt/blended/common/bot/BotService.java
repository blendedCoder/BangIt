package com.bangIt.blended.common.bot;

import java.util.Set;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.bangIt.blended.common.bot.entity.AnswerEntity;
import com.bangIt.blended.common.bot.entity.IntentionEntityRepository;
import com.bangIt.blended.domain.dto.place.PlaceDetailDTO;
import com.bangIt.blended.domain.enums.PlaceType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BotService {

    // WebSocket을 통한 메시지 전송을 위한 템플릿
    private final SimpMessagingTemplate messagingTemplate;
    
    // 형태소 분석을 수행하는 서비스
    private final KomoranService komoranService;
    
    // 의도 엔티티를 조회하는 레포지토리
    private final IntentionEntityRepository intentionRepository;
    
    // 숙소 검색 서비스
    private final AccommodationService accommodationService;

    /**
     * 사용자의 질문을 처리하는 메소드
     * @param dto 사용자의 질문 정보를 담은 객체
     */
    public void questionProcess(Question dto) {
        // 숙소 검색 키워드 추출
        AccommodationSearchKeywords keywords = extractAccommodationKeywords(dto.getContent());
        
        if (keywords != null && "알려줘".equals(keywords.getAction())) {
            // 숙소 검색 처리
            handleAccommodationSearch(dto, keywords);
        } else {
            // 일반 질문 처리
            handleGeneralQuestion(dto);
        }
    }

    /**
     * 숙소 검색 처리 메소드
     * @param dto 사용자의 질문 정보를 담은 객체
     * @param keywords 추출된 숙소 검색 키워드
     */
    private void handleAccommodationSearch(Question dto, AccommodationSearchKeywords keywords) {
        // 숙소 검색 수행
        List<PlaceDetailDTO> accommodations = accommodationService.search(
            keywords.getRegion(), 
            keywords.getDetailedAddress(), 
            keywords.getType()
        );
        
        // 검색 결과 포맷팅 및 전송
        String resultMessage = formatAccommodationResults(accommodations);
        messagingTemplate.convertAndSend("/topic/bot/" + dto.getKey(), resultMessage);
    }

    /**
     * 일반 질문 처리 메소드
     * @param dto 사용자의 질문 정보를 담은 객체
     */
    private void handleGeneralQuestion(Question dto) {
        // 형태소 분석을 통한 명사 추출
        Set<String> result = komoranService.analyzeTokenAndGetNouns(dto.getContent());

        IntentionEntity intention = null;
        // 추출된 명사를 기반으로 의도 파악
        for (String keyword : result) {
            intention = intentionRepository.findByKeyword(keyword).orElse(null);
            if (intention != null) break;
        }

        // 기본 답변 설정
        AnswerEntity answer = AnswerEntity.builder()
            .content("죄송합니다. 제가 답변 할 수 없는 질문이네요!")
            .build();

        // 의도가 파악되면 해당 답변으로 업데이트
        if (intention != null) {
            answer = intention.getAnswer();
        }

        // 답변 전송
        messagingTemplate.convertAndSend("/topic/bot/" + dto.getKey(), answer.getContent());
    }

    /**
     * 사용자 메시지에서 숙소 검색 키워드를 추출하는 메소드
     * @param message 사용자 메시지
     * @return 추출된 검색 키워드 객체, 키워드가 없으면 null
     */
    private AccommodationSearchKeywords extractAccommodationKeywords(String message) {
        // TODO: 실제 키워드 추출 로직 구현
        return null; // 임시 반환값
    }

    /**
     * 숙소 검색 결과를 포맷팅하여 문자열로 변환하는 메소드
     * @param accommodations 검색된 숙소 목록
     * @return 포맷팅된 결과 문자열
     */
    private String formatAccommodationResults(List<PlaceDetailDTO> accommodations) {
        if (accommodations.isEmpty()) {
            return "죄송합니다. 해당 조건에 맞는 숙소를 찾지 못했습니다.";
        }

        StringBuilder sb = new StringBuilder("검색 결과:\n\n");
        for (PlaceDetailDTO place : accommodations) {
            sb.append(String.format("이름: %s\n", place.getName()));
            sb.append(String.format("위치: %s %s\n", place.getRegion(), place.getDetailedAddress()));
            sb.append(String.format("유형: %s\n", place.getType()));
            sb.append(String.format("설명: %s\n\n", place.getDescription()));
        }
        sb.append("더 자세한 정보나 예약을 원하시면 숙소 이름을 말씀해 주세요.");
        return sb.toString();
    }
}