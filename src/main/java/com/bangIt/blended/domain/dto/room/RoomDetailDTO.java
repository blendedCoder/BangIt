package com.bangIt.blended.domain.dto.room;

import java.time.LocalTime;

import java.util.List;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomDetailDTO {

	private Long placeId;
	private Long id;
    private String roomName;
    private String roomInformation;
    private Long roomPrice;
    private String roomStatus;
    private String refundPolicy;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Long guests;

    
 // 새로 추가된 필드
    private String mainImage;
    private List<String> additionalImages;


}
