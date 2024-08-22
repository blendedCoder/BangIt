package com.bangIt.blended.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bangIt.blended.service.admin.AdminEmployeeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AdminEmployeeController {

	
	private final AdminEmployeeService adminEmployeeService;
	
	@GetMapping("/admin/employee")
	public String employList(Model model, HttpSession session) {
		
		 String accessToken = (String) session.getAttribute("accessToken");
		    if (accessToken == null) {
		        return "redirect:/admin"; // 토큰이 없으면 다시 인증
		    }
		    	
		adminEmployeeService.getEmployListProcess(model,accessToken);
		return "views/admin/employee/list";
	}
	
	

}
