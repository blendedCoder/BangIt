package com.bangIt.blended.domain.entity;

import java.time.LocalTime;

import org.hibernate.annotations.DynamicUpdate;

import com.bangIt.blended.domain.dto.room.RoomDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@DynamicUpdate
@SequenceGenerator(name = "gen_room", sequenceName = "seq_room", initialValue = 1, allocationSize = 1)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "room")
@Getter
@Entity
public class RoomEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_room")
	private long id;

	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	private PlaceEntity place;

	@Column(nullable = false)
	private String roomName;

	@Column(columnDefinition = "TEXT")
	private String roomInformation;

	@Column(nullable = false)
	private Long roomPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RoomStatus roomStatus;

	@Column(nullable = false)
	private String refundPolicy;

	@Column(nullable = false)
	private LocalTime checkInTime;

	@Column(nullable = false)
	private LocalTime checkOutTime;
	
	@Column(nullable = false)
	private Long guests;

	// RoomStatus enum (방 상태를 나타내는 열거형)
	@Getter
	@RequiredArgsConstructor
	public enum RoomStatus {
		AVAILABLE(1, "이용 가능"), 
		BOOKED(2, "예약됨"), 
		MAINTENANCE(3, "유지보수 중");

		private final int number;
		private final String KoName;
	}
	
	
	public RoomDTO toRoomDTO() {
		return RoomDTO.builder()
				.checkInTime(checkInTime)
				.checkOutTime(checkOutTime)
				.roomName(roomName)
				.roomPrice(roomPrice)
				.build();
	}
}
