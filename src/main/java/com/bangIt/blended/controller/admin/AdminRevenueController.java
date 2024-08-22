package com.bangIt.blended.controller.admin;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bangIt.blended.domain.dto.revenue.SearchRservationDTO;
import com.bangIt.blended.service.admin.AdminRevenueService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AdminRevenueController {
	
	private final AdminRevenueService adminRevenueService;

	

	@GetMapping("/admin/revenue")
	public String adminPartner(SearchRservationDTO dto, Model model) {
		
		System.out.println("SearchRservationDTO :" + dto);
		
	    if (dto == null || dto.getStartDate() == null || dto.getEndDate() == null) {
	        dto = new SearchRservationDTO();
	        dto.setStartDate(LocalDate.now().minus(1, ChronoUnit.MONTHS));
	        dto.setEndDate(LocalDate.now());
	    }
	
		adminRevenueService.getPartnerRevenue(dto,model);
		
		return "views/admin/revenue/list";
	}
	
	
	 @ResponseBody
	 @GetMapping("/admin/revenue/export")
	    public ResponseEntity<Resource> exportToExcel(SearchRservationDTO dto) {
		 
		 if (dto == null || dto.getStartDate() == null || dto.getEndDate() == null) {
		        dto = new SearchRservationDTO();
		        dto.setStartDate(LocalDate.now().minus(1, ChronoUnit.MONTHS));
		        dto.setEndDate(LocalDate.now());
		    }
		
	        
		 ByteArrayResource resource = adminRevenueService.exportToExcel(dto);
	        


		 return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue_data.xlsx")
	                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	                .body(resource);
	    }
	
	

}
