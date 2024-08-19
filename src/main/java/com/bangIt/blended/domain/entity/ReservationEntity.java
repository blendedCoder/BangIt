package com.bangIt.blended.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import com.bangIt.blended.domain.dto.room.ReservationDTO;
import com.bangIt.blended.domain.dto.room.RoomDTO;
import com.bangIt.blended.domain.enums.ReservationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicUpdate
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 사용하려고 생성자초기화 막아주는것
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class ReservationEntity {
	
	//예약 ID
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	//예약일
	@Column(nullable = false)
    private LocalDateTime reservationDate;

	//체크인 날짜
    @Column(nullable = false)
    private LocalDateTime checkInDate;

    //체크아웃 날짜
    @Column(nullable = false)
    private LocalDateTime checkOutDate;
    
    //예약인원
    @Column(nullable = false)
    private Long reservationPeople;
    
    //예약상태 (대기중, 예약확정, 예약 취소)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus;
	
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;
	
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    // SaleEntity와 1대1 관계
    @OneToOne(mappedBy = "reservation")
    private SaleEntity sale;
	
    @Column(nullable = false, unique = true)  // 주문 번호는 고유해야 하므로 unique = true를 추가했습니다.
    private String orderId;
    
    public ReservationDTO toReservationDTO() {
    	return ReservationDTO.builder()
    			.reservationDate(reservationDate)
    			.checkInDate(checkInDate)
    			.checkOutDate(checkOutDate)
    			.roomName(room.getRoomName())
    			.roomPrice(room.getRoomPrice())
    			.build();
    }
}
