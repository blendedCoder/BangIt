package com.bangIt.blended.domain.dto.room;


import com.bangIt.blended.domain.entity.RoomEntity.RoomStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomListDTO {
    private long id;
    private String roomName;
    private long roomPrice;
    private RoomStatus roomStatus;
    private long guests;
    private String placeName; // 숙소 이름 필드 추가

}
