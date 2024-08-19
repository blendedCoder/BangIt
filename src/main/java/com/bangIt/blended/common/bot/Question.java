package com.bangIt.blended.common.bot;

import lombok.Data;

@Data
public class Question {

	private String key;// 질문자를 구분하기위한 값
	private String content;// 질문내용
	private String name; // 질문자-선택사항

}
