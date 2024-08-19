package com.bangIt.blended.service.partner.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.bangIt.blended.common.util.FileUploadUtil;
import com.bangIt.blended.domain.dto.PlaceListDTO;
import com.bangIt.blended.domain.dto.place.PlaceSaveDTO;
import com.bangIt.blended.domain.entity.ImageEntity;
import com.bangIt.blended.domain.entity.PlaceEntity;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.repository.ImageEntityRepository;
import com.bangIt.blended.domain.repository.PlaceEntityRepository;
import com.bangIt.blended.service.partner.PartnerPlaceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PartnerPlaceServiceProcess implements PartnerPlaceService{
	
	//private final EmployeesEntityRepository  employeesEntityRep;
	private final PlaceEntityRepository repository;
	private final ImageEntityRepository imageRepository;
	private final FileUploadUtil fileUploadUtil;
	
	@Override
    @Transactional
    public void saveProcess(PlaceSaveDTO dto) {
		
		// 메인 이미지 처리
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            List<String> mainImageKeys = new ArrayList<>();
            mainImageKeys.add(dto.getMainImageBucketKey());
            List<String> mainImageUrls = fileUploadUtil.s3TempToImages(mainImageKeys);
            if (!mainImageUrls.isEmpty()) {
                dto.setMainImageBucketKey(mainImageUrls.get(0));
            }
        }

        // 추가 이미지 처리
        if (dto.getAdditionalImageBucketKeys() != null && !dto.getAdditionalImageBucketKeys().isEmpty()) {
            List<String> additionalImageUrls = fileUploadUtil.s3TempToImages(dto.getAdditionalImageBucketKeys());
            dto.setAdditionalImageBucketKeys(additionalImageUrls);
        }
		
		//Place 엔티티 저장
        PlaceEntity placeEntity = toPlaceEntity(dto);
        repository.save(placeEntity);
        
        //이미지 정보 저장
        saveImages(placeEntity, dto);
        
    }
	
	private void saveImages(PlaceEntity place, PlaceSaveDTO dto) {
		// 메인 이미지 저장
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            ImageEntity mainImage = ImageEntity.builder()
                .place(place)
                .imageUrl(dto.getMainImageBucketKey()) // 전체 URL 저장
                .imageType(ImageEntity.ImageType.PLACE_MAIN)
                .build();
            imageRepository.save(mainImage);
        }
        
        // 추가 이미지 저장
        if (dto.getAdditionalImageBucketKeys() != null && !dto.getAdditionalImageBucketKeys().isEmpty()) {
            List<ImageEntity> additionalImages = new ArrayList<>();
            for (String url : dto.getAdditionalImageBucketKeys()) {
                ImageEntity additionalImage = ImageEntity.builder()
                    .place(place)
                    .imageUrl(url) // 전체 URL 저장
                    .imageType(ImageEntity.ImageType.PLACE_ADDITIONAL)
                    .build();
                additionalImages.add(additionalImage);
            }
            imageRepository.saveAll(additionalImages);
        }
	}

	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}

    private PlaceEntity toPlaceEntity(PlaceSaveDTO dto) {
        return PlaceEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .region(dto.getRegion())
                .detailedAddress(dto.getDetailedAddress())
                .type(dto.getType())
                .themes(dto.getThemes())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .status(PlaceStatus.PENDING_APPROVAL)
                .build();
    }

    //
	@Override
	public void listProcess(Model model) {
        List<PlaceEntity> placeEntities = repository.findAll();
        List<PlaceListDTO> placeDTOs = placeEntities.stream()
            .map(this::toPlaceListDTO)
            .collect(Collectors.toList());
        
        model.addAttribute("places", placeDTOs);
    }

	//숙소 목록 조회
    private PlaceListDTO toPlaceListDTO(PlaceEntity entity) {
        return PlaceListDTO.builder()
            .id(entity.getId())
            .region(entity.getRegion())
            .name(entity.getName())
            .type(entity.getType())
            .updatedAt(entity.getUpdatedAt())
            .status(entity.getStatus())
            .build();
	}

    // 상세페이지 조회
	@Override
	public void detailProcess(Long id, Model model) {
        PlaceEntity place = repository.findById(id).orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));            
        model.addAttribute("place", place.toPlaceDetailDTO());
		
	}

	//no해당하는 숙소 DB에서 삭제
	@Override
	public void deleteProcess(long id) {
		repository.delete(repository.findById(id).orElseThrow());
		
	}
}
