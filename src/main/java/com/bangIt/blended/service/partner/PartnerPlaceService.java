package com.bangIt.blended.service.partner;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.bangIt.blended.domain.dto.place.PlaceSaveDTO;

public interface PartnerPlaceService {

	void saveProcess(PlaceSaveDTO dto);
	
    Map<String, String> s3TempUpload(MultipartFile file) throws IOException;

	void listProcess(Model model);

	void detailProcess(Long id, Model model);

}
