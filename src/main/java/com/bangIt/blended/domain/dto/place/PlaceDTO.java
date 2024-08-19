package com.bangIt.blended.domain.dto.place;

import com.bangIt.blended.domain.enums.PlaceType;
import com.bangIt.blended.domain.enums.Region;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceDTO {
	
	private String name;
	private String address;
	private PlaceType type;
	private int price ;
	

}
