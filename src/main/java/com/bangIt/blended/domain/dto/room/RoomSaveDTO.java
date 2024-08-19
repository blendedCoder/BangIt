package com.bangIt.blended.domain.dto.room;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomSaveDTO {
	private Long placeId;
    private String roomName;
    private String roomInformation;
    private Long roomPrice;
    private String roomStatus;
    private String refundPolicy;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Long guests;

}
