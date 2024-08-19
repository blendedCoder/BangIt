package com.bangIt.blended.controller.user;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@Controller
public class IndexController {
	
	@GetMapping("/")
	public String getMethodName() {
		
		
		
		
		return "/index";
	}
	

}
