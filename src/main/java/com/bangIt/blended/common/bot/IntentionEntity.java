package com.bangIt.blended.common.bot;

import com.bangIt.blended.common.bot.entity.AnswerEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "intention", indexes = { // 엔티티가 'intention' 테이블에 매핑됨을 명시
		@Index(name = "idx_upper_intention", columnList = "upper_no"),
		@Index(name = "idx_answer_intention", columnList = "answer_no") })
@Entity
public class IntentionEntity {

	@Id // JPA 어노테이션으로, 이 필드가 엔티티의 기본 키임을 나타냄
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값을 자동으로 생성해주는 전략 설정
	private long no; // 고유 식별자 (기본 키)
	private String keyword; // 명사 또는 주요 키워드, ex) 호텔, 모텔 등
	private String behavior; // 동사 또는 행동을 나타내는 필드, ex) 알려주다, 설명하다
	private String number; // 추가적인 식별자나 순번을 나타내는 필드
	
	@ManyToOne // 다대일 관계를 나타냄
	@JoinColumn(name="answer_no") // 'answer_no' 컬럼을 외래 키로 설정
	private AnswerEntity answer; // 이 의도와 연결된 답변 엔티티
	
	@ManyToOne // 다대일 관계를 나타냄
	@JoinColumn(name="upper_no") // 'upper_no' 컬럼을 외래 키로 설정
	IntentionEntity upper; // 상위 의도 엔티티, 즉 이 의도의 상위 또는 부모 의도
}
