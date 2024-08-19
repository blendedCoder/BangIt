package com.bangIt.blended.domain.dto.placesList;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Setter;

@Setter
public class SearchPlaceDTO {
	private String region;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkinDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkoutDate;
	private int guestCount;
	
}
