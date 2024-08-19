package com.bangIt.blended.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;

import com.bangIt.blended.domain.dto.place.PlaceDetailDTO;
import com.bangIt.blended.domain.entity.ImageEntity.ImageType;
import com.bangIt.blended.domain.enums.PlaceStatus;
import com.bangIt.blended.domain.enums.PlaceTheme;
import com.bangIt.blended.domain.enums.PlaceType;
import com.bangIt.blended.domain.enums.Region;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.Bucket;

@DynamicUpdate
@SequenceGenerator(name = "gen_place", sequenceName = "seq+place", initialValue = 1, allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "place")
@Getter
@Entity
public class PlaceEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_place")
	private long id;
	
	@Column(nullable = false)
	private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Column(nullable = false)
    private String detailedAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceType type;

    @ElementCollection(targetClass = PlaceTheme.class)
    @CollectionTable(name = "place_themes", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    private Set<PlaceTheme> themes;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceStatus status;
    
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageEntity> images;
    
    //숙소 상세 페이지 dto
    public PlaceDetailDTO toPlaceDetailDTO() {
    	String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/nowon.images.host0521/";
        String mainImage = null;
        List<String> additionalImages = new ArrayList<>();
        for (ImageEntity image : images) {
            if(image.getImageUrl() == null || image.getImageUrl().isEmpty()) continue;

            String imageUrl = image.getImageUrl();
            // 이미 전체 경로가 포함되어 있으므로 추가 처리 불필요
            String fullUrl = baseUrl + imageUrl;

            if (image.getImageType() == ImageType.PLACE_MAIN) {
                mainImage = fullUrl;
            } else if (image.getImageType() == ImageType.PLACE_ADDITIONAL) {
                additionalImages.add(fullUrl);
            }
        }

        return PlaceDetailDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .detailedAddress(detailedAddress)
                .region(region)
                .type(type)
                .themes(themes)
                .latitude(latitude)
                .longitude(longitude)
                .mainImage(mainImage)  // 이 부분이 PlaceDetailDTO의 필드 이름과 일치해야 합니다
                .additionalImages(additionalImages)  // 이 부분도 마찬가지입니다
                .build();
    }
}
