package com.bangIt.blended.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.DynamicUpdate;

import com.bangIt.blended.domain.dto.place.HotelListDTO;
import com.bangIt.blended.domain.dto.place.PlaceDetailDTO;
import com.bangIt.blended.domain.dto.place.PlaceDetailDTO2;
import com.bangIt.blended.domain.dto.place.PlaceListDTO;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamicUpdate
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "place")
@Getter
@Setter
@Entity
public class PlaceEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    public PlaceEntity status(PlaceStatus status) {
    	this.status=status;
    	return this;
    }
    
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageEntity> images;
    
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomEntity> rooms;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private UserEntity seller;
    
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;
    
    public PlaceListDTO toPlaceListDTO() {
        return PlaceListDTO.builder()
            .id(id)
            .region(region)
            .name(name)
            .type(type)
            .updatedAt(updatedAt)
            .status(status)
            .build();
	}
    
    public PlaceDetailDTO2 toPlaceDetailDTO2() {
        return PlaceDetailDTO2.builder()
            .id(id)
            .region(region)
            .name(name)
            .type(type)
            .updatedAt(updatedAt)
            .status(status)
            .description(description)
            .themes(themes)
            .images(images.stream().map(ImageEntity::toImageListDTO).collect(Collectors.toList()))
            .build();
	}
    
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
    
	
    // 거리 정보를 포함한 HotelListDTO 생성 메서드
    public HotelListDTO toHotelListDTOWithDistance(double distance) {
        String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/nowon.images.host0521/";
        String mainImage = null;

        // 이미지 리스트에서 메인 이미지 찾기
        for (ImageEntity image : images) {
            if (image.getImageUrl() == null || image.getImageUrl().isEmpty()) continue;

            String imageUrl = image.getImageUrl();
            String fullUrl = baseUrl + imageUrl;

            if (image.getImageType() == ImageEntity.ImageType.PLACE_MAIN) {
                mainImage = fullUrl;
                break;
            }
        }


        return HotelListDTO.builder()
        		.id(id)
                .name(name)
                .imageUrl(mainImage)
                .description(description)
                .distance(distance)
                .build();
    }

    
    //최근 등록 숙소 불러오기
    public HotelListDTO toLatestHotelListDTO() {
    	String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/nowon.images.host0521/";
        String mainImage = null;

        // 이미지 리스트에서 메인 이미지 찾기
        for (ImageEntity image : images) {
            if (image.getImageUrl() == null || image.getImageUrl().isEmpty()) continue;

            String imageUrl = image.getImageUrl();
            String fullUrl = baseUrl + imageUrl;

            if (image.getImageType() == ImageEntity.ImageType.PLACE_MAIN) {
                mainImage = fullUrl;
                break;
            }
        }
        return HotelListDTO.builder()
        		.id(id)
        		.name(name)
        		.description(description)
        		.createdAt(createdAt)
        		.description(description)
        		.imageUrl(mainImage)
        		.build();
    }
    
    
    
    // 거리 정보를 포함하지 않은 HotelListDTO 생성 메서드
    public HotelListDTO toHotelListDTOWithoutDistance() {
        String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/nowon.images.host0521/";
        String mainImage = null;

        // 이미지 리스트에서 메인 이미지 찾기
        for (ImageEntity image : images) {
            if (image.getImageUrl() == null || image.getImageUrl().isEmpty()) continue;

            String imageUrl = image.getImageUrl();
            String fullUrl = baseUrl + imageUrl;

            if (image.getImageType() == ImageEntity.ImageType.PLACE_MAIN) {
                mainImage = fullUrl;
                break;
            }
        }

        // 각 방의 최저가 찾기
        Long minPrice = rooms.stream()
                .map(RoomEntity::getRoomPrice)
                .min(Long::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("No valid room price found."));

        return HotelListDTO.builder()
        		.id(id)
                .name(name)
                .imageUrl(mainImage)
                .description(description)
                .price(minPrice)
                .build();
    }

}