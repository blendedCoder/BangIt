package com.bangIt.blended.domain.dto.place;

import java.util.Set;

import com.bangIt.blended.domain.enums.PlaceTheme;
import com.bangIt.blended.domain.enums.PlaceType;
import com.bangIt.blended.domain.enums.Region;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceDetailDTO {

	private long id;
	private String name;
    private String description;
    private Region region;
    private String detailedAddress;
    private PlaceType type;
    private Set<PlaceTheme> themes;
    private Double latitude;
    private Double longitude;

}
