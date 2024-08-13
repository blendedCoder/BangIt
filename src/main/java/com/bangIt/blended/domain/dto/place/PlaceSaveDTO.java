package com.bangIt.blended.domain.dto.place;

import java.util.List;
import java.util.Set;

import com.bangIt.blended.domain.enums.PlaceTheme;
import com.bangIt.blended.domain.enums.PlaceType;
import com.bangIt.blended.domain.enums.Region;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceSaveDTO {
	
	private String name;
    private String description;
    private Region region;
    private String detailedAddress;
    private PlaceType type;
    private Set<PlaceTheme> themes;
    private String mainImageUrl;
    private List<String> additionalImageUrls;
    private Double latitude;
    private Double longitude;

}
