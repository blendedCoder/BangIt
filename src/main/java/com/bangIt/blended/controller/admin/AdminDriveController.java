package com.bangIt.blended.controller.admin;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bangIt.blended.service.admin.AdminDriveService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Controller
public class AdminDriveController {
	
	private final AdminDriveService adminDriveService;
	
	private final HttpSession session;
	

	
    // 세션에서 accessToken 가져오는 공통 메서드
	private String getAccessToken() {
	    return (String) session.getAttribute("accessToken");
   }

	
   //내 드라이브 정보 가져오기
	@GetMapping("/admin/drive")
	public String adminPartner(Model model) {
		
		String accessToken = getAccessToken();
		
		adminDriveService.getMyDriveProcess(model, accessToken);
		adminDriveService.getMyDriveRootFileProcess(model, accessToken);
		
		return "views/admin/drive/list";
	}
	
	//특정 드라이브 파일 다운로드
	
	@ResponseBody
	@GetMapping("/admin/drive/download/{fileId}")
	public ResponseEntity<ByteArrayResource> adminPartner(@PathVariable(name = "fileId")String fileId) {
		
		String accessToken = getAccessToken();
		
		return  adminDriveService.getFildeDownloadUrl(accessToken,fileId);
		
	
	}
	
	//특정 파일 삭제
	@DeleteMapping("/admin/drive/delete/{fileId}")
	public String getMethodName(@PathVariable(name = "fileId")String fileId) {
		
		String accessToken = getAccessToken();
		
		adminDriveService.deleteFilde(accessToken,fileId);
		
		return "redirect:/admin/drive";
	}
	
	//파일 업로드
	@PostMapping("/admin/drive")
	public String postMethodName(@RequestParam("file") MultipartFile file) throws IOException {
		String accessToken = getAccessToken();
		adminDriveService.postFile(file,accessToken);
		
		return "redirect:/admin/drive";
	}
	
	
	
	

}
