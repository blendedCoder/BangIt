package com.bangIt.blended.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
	
	private final Integer code;
	private final String msg;
	private final T data;
	
	

}
