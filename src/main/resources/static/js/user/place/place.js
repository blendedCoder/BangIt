function openModal() {
	document.getElementById('roomModal').style.display = "block";
}

// 모달 닫기 함수
function closeModal() {
	document.getElementById('roomModal').style.display = "none";
}

function openModal(roomId) {
    fetch(`/rooms/${roomId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(room => {
            document.getElementById('modalRoomName').textContent = room.roomName;
            document.getElementById('modalRoomInfo').textContent = room.roomInformation;
            document.getElementById('modalCheckIn').textContent = room.checkInTime.substring(0, 5);
    document.getElementById('modalCheckOut').textContent = room.checkOutTime.substring(0, 5);
            document.getElementById('modalGuests').textContent = room.guests + '명';
            document.getElementById('modalPrice').textContent = room.roomPrice.toLocaleString() + '원 / 박';
            document.getElementById('modalRefundPolicy').textContent = room.refundPolicy;

            document.getElementById('roomModal').style.display = 'block';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('방 정보를 불러오는 데 실패했습니다.');
        });
}

// 모달 창 외부 클릭 시 닫기
window.onclick = function(event) {
	var modal = document.getElementById('roomModal');
	if (event.target == modal) {
		closeModal();
	}
}
var mapContainer = document.getElementById('map');
var mapOption = {
	center: new kakao.maps.LatLng(37.537187, 127.005476),
	level: 5
};

var map = new kakao.maps.Map(mapContainer, mapOption);
var geocoder = new kakao.maps.services.Geocoder();
var marker = new kakao.maps.Marker({
	map: map
});

var address = /*[[${place.detailedAddress}]]*/ '기본 주소';
var lat = /*[[${place.latitude}]]*/ 37.537187;
var lng = /*[[${place.longitude}]]*/ 127.005476;

if (lat && lng) {
	var coords = new kakao.maps.LatLng(lat, lng);
	map.setCenter(coords);
	marker.setPosition(coords);
} else {
	geocoder.addressSearch(address, function(results, status) {
		if (status === kakao.maps.services.Status.OK) {
			var result = results[0];
			var coords = new kakao.maps.LatLng(result.y, result.x);
			map.setCenter(coords);
			marker.setPosition(coords);
		}
	});
}

function prepareReservation(roomId) {
    const checkInDate = document.getElementById('checkin-date').value;
    const checkOutDate = document.getElementById('checkout-date').value;
    const reservationPeople = document.getElementById('adults').value;

    if (!checkInDate || !checkOutDate) {
        alert('체크인 및 체크아웃 날짜를 선택해주세요.');
        return;
    }

    // 날짜를 ISO 8601 형식으로 변환
    const formattedCheckInDate = new Date(checkInDate + 'T00:00:00').toISOString();
    const formattedCheckOutDate = new Date(checkOutDate + 'T00:00:00').toISOString();

    // 폼 필드 설정
    document.getElementById('roomId').value = roomId;
    document.getElementById('formCheckInDate').value = formattedCheckInDate;
    document.getElementById('formCheckOutDate').value = formattedCheckOutDate;
    document.getElementById('formReservationPeople').value = reservationPeople;

    // 폼 제출 전 확인
    if (confirm('예약을 진행하시겠습니까?')) {
        document.getElementById('reservationForm').submit();
    }
}

