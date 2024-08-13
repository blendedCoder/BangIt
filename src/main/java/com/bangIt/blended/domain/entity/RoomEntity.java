package com.bangIt.blended.domain.entity;

import java.time.LocalTime;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

	@Column(columnDefinition = "TEXT")
	private String roomDescription;

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

	// RoomStatus enum (방 상태를 나타내는 열거형)
	public enum RoomStatus {
		AVAILABLE, BOOKED, MAINTENANCE
	}

}
