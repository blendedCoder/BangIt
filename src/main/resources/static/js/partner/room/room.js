document.addEventListener('DOMContentLoaded', function() {
    // DOM이 완전히 로드된 후 실행
    const searchResults = document.querySelector('.search-results ul');
    const reservationInfo = document.querySelector('.reservation-info');
    const noUserSelectedReservation = reservationInfo.querySelector('.no-user-selected');
    const reservationDetails = reservationInfo.querySelector('.reservation-details');
    const placeItems = document.querySelectorAll('.user-item');
    const placeDetails = document.querySelector('.place-details');
    const roomDetails = document.querySelector('.room-details');
    const roomList = document.getElementById('roomList');
    let selectedPlaceId = null;

    // 숙소 목록의 각 항목에 클릭 이벤트 리스너 추가
    placeItems.forEach(item => {
        item.addEventListener('click', function() {
            selectedPlaceId = this.getAttribute('data-place-id');
            loadPlaceDetails(selectedPlaceId);  // 숙소 상세 정보를 로드
        });
    });

    // 이벤트 위임을 사용하여 동적으로 생성된 요소에 대한 이벤트 처리
    document.addEventListener('click', function(event) {
        if (event.target && event.target.id === 'roomSave') {
            if (selectedPlaceId) {
                loadRoomRegisterForm(selectedPlaceId);  // 방 등록 폼을 로드
            } else {
                alert('숙소를 먼저 선택해주세요.');
            }
        }
    });

    // 숙소 상세 정보를 로드하는 함수
    function loadPlaceDetails(placeId) {
        fetch(`/partner/placeDetails/${placeId}`)
            .then(response => response.text())
            .then(html => {
                placeDetails.innerHTML = html;
                showPlaceDetails();  // 숙소 상세 정보를 표시
                loadRoomList(placeId);  // 방 목록을 로드
            })
            .catch(error => {
                console.error('숙소 상세 정보 로드 오류:', error);
                placeDetails.innerHTML = '<p>숙소 상세 정보를 로드하는 중 오류가 발생했습니다. 다시 시도해주세요.</p>';
            });
    }
    
    // 방 목록을 로드하는 함수
    function loadRoomList(placeId) {
        const placeItem = document.querySelector(`.user-item[data-place-id="${placeId}"]`);
        const placeStatus = placeItem.getAttribute('data-place-status');

        fetch(`/partner/roomListHtml?placeId=${placeId}&placeStatus=${placeStatus}`)
            .then(response => response.text())
            .then(html => {
                const roomListContainer = document.getElementById('roomList');
                roomListContainer.innerHTML = html;

                // 예약 정보 패널을 보이도록 설정
                document.querySelector('.reservation-details').style.display = 'block';
                document.querySelector('.no-user-selected').style.display = 'none';

                const roomSaveButton = document.getElementById('roomSave');
                if (roomSaveButton) {
                    // 숙소가 승인된 경우에만 방 등록 버튼을 표시
                    roomSaveButton.style.display = placeStatus === 'APPROVED' ? 'block' : 'none';
                }

                // 방 목록의 각 항목에 클릭 이벤트 리스너 추가
                addRoomItemClickListeners();
            })
            .catch(error => {
                console.error('방 목록 로드 오류:', error);
                document.getElementById('roomList').innerHTML = '<li>방 목록을 로드하는 중 오류가 발생했습니다.</li>';
            });
    }

    // 방 목록 항목에 클릭 이벤트 리스너를 추가하는 함수
    function addRoomItemClickListeners() {
        const roomItems = document.querySelectorAll('.room-item');
        roomItems.forEach(item => {
            item.addEventListener('click', function() {
                const roomId = this.getAttribute('data-room-id');
                loadRoomDetails(roomId);  // 방 상세 정보를 로드
            });
        });
    }

    // 방 상세 정보를 로드하는 함수
    function loadRoomDetails(roomId) {
        fetch(`/roomDetails/${roomId}`)
            .then(response => response.text())
            .then(html => {
                roomDetails.innerHTML = html;
                showRoomDetails();  // 방 상세 정보를 표시
            })
            .catch(error => {
                console.error('방 상세 정보 로드 오류:', error);
                roomDetails.innerHTML = '<p>방 상세 정보를 로드하는 중 오류가 발생했습니다.</p>';
            });
    }

    // 방 등록 폼을 로드하는 함수
    function loadRoomRegisterForm(placeId) {
        fetch(`/partner/roomRegisterForm?placeId=${placeId}`)
            .then(response => response.text())
            .then(html => {
                roomDetails.innerHTML = html;
                showRoomDetails();  // 방 등록 폼을 표시
            })
            .catch(error => {
                console.error('방 등록 폼 로드 오류:', error);
                roomDetails.innerHTML = '<p>방 등록 폼을 로드하는 중 오류가 발생했습니다. 다시 시도해주세요.</p>';
            });
    }

    // 폼 제출 이벤트를 이벤트 위임으로 처리
    document.addEventListener('submit', function(event) {
        if (event.target && event.target.matches('form[action="/partner/roomSave"]')) {
            event.preventDefault();  // 기본 폼 제출 동작을 막음
            const formData = new FormData(event.target);
            fetch('/partner/roomSave', {
                method: 'POST',
                body: formData,
            })
            .then(response => {
                if (response.ok) {
                    alert('방이 성공적으로 등록되었습니다.');
                    window.location.reload();  // 페이지를 새로 고침하여 변경사항을 반영
                } else {
                    alert('방 저장에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('방 저장 오류:', error);
                alert('방 저장 중 오류가 발생했습니다.');
            });
        }
    });

    // 검색 결과 항목 클릭 시 예약 정보 표시
    searchResults.addEventListener('click', function(e) {
        const userItem = e.target.closest('.user-item');
        if (userItem) {
            // 모든 항목에서 선택된 클래스 제거
            searchResults.querySelectorAll('.user-item').forEach(item => item.classList.remove('selected'));
            userItem.classList.add('selected');  // 클릭한 항목에 선택된 클래스 추가

            noUserSelectedReservation.style.display = 'none';
            reservationDetails.style.display = 'block';  // 예약 정보를 보이도록 설정

            console.log('사용자가 클릭한 항목:', userItem.textContent);
            console.log('예약 정보 및 활동 정보 표시됨');
        }
    });
    
    // 숙소 상세 정보를 표시하는 함수
    function showPlaceDetails() {
        placeDetails.style.display = 'block';
        roomDetails.style.display = 'none';  // 방 상세 정보 숨김
    }

    // 방 상세 정보를 표시하는 함수
    function showRoomDetails() {
        placeDetails.style.display = 'none';
        roomDetails.style.display = 'block';  // 숙소 상세 정보 숨김
    }
});
