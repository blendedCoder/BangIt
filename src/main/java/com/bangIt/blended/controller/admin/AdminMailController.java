package com.bangIt.blended.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.bangIt.blended.domain.dto.naverWorks.WritMailDTO;
import com.bangIt.blended.service.admin.AdminMailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class AdminMailController {

	private final AdminMailService adminMailService;
	

	private final HttpSession session;
	

	
	  // 세션에서 accessToken 가져오는 공통 메서드
    private String getAccessToken() {
        return (String) session.getAttribute("accessToken");
    }
    

    //메일함과 메일리스트 조회
	@GetMapping("/admin/mail/{folderId}")
	public String getMailList(Model model,@PathVariable(name ="folderId" ) String folderId) {
		
		String accessToken = getAccessToken();
		
		if (accessToken == null) {
			return "redirect:/admin"; // 토큰이 없으면 리다이렉트
		}
		adminMailService.getMailFolderProcess(model, accessToken);
		adminMailService.getMailFolderListProcess(folderId,model,accessToken);
		
		
		return "views/admin/mail/list";
	}
	
	
	//특정 메일 내용 조희
	@GetMapping("/admin/mail/detail/{mailId}")
	public String getMaildetail(Model model,@PathVariable(name ="mailId" ) String mailId) {
		
		String accessToken = getAccessToken();
		
		if (accessToken == null) {
			return "redirect:/admin"; // 토큰이 없으면 리다이렉트
		}
	
		adminMailService.getMailDetail(mailId,model,accessToken);
		
		
		return "views/admin/mail/detail :: mailDetailContent";
	}
	
	
	//메일 보내기
	@PostMapping("/admin/mail")
	public String postMethodName(WritMailDTO dto) {
		String accessToken = getAccessToken();
		
		if (accessToken == null) {
			return "redirect:/admin"; // 토큰이 없으면 리다이렉트
		}
	
		adminMailService.WritMailProcess(dto,accessToken);
		
		
		return "redirect:/admin/mail/0";
	}
	

}
