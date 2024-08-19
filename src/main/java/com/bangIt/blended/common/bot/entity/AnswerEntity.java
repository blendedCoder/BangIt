package com.bangIt.blended.common.bot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "answer")
public class AnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값을 자동으로 생성
	private long no; // @Id 어노테이션-고유 식별자(기본 키)
	private String intent; // 질문 의도
	private String content; // 답변 내용
	//private Service service;
	
}
