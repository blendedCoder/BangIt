document.addEventListener('DOMContentLoaded', function() {
    const searchResults = document.querySelector('.search-results ul');
    const reservationInfo = document.querySelector('.reservation-info');
    const noUserSelectedReservation = reservationInfo.querySelector('.no-user-selected');
    const reservationDetails = reservationInfo.querySelector('.reservation-details');
    const placeItems = document.querySelectorAll('.user-item');
    const roomSaveBtn = document.getElementById('roomSave');
    const placeDetails = document.querySelector('.place-details');
    const roomDetails = document.querySelector('.room-details');
	const roomList = document.getElementById('roomList');
    let selectedPlaceId = null;

    placeItems.forEach(item => {
        item.addEventListener('click', function() {
            selectedPlaceId = this.getAttribute('data-place-id');
            loadPlaceDetails(selectedPlaceId);
        });
    });

    if (roomSaveBtn) {
        roomSaveBtn.addEventListener('click', function() {
            if (selectedPlaceId) {
                loadRoomRegisterForm(selectedPlaceId);
            } else {
                alert('숙소를 먼저 선택해주세요.');
            }
        });
    } else {
        console.error('방 저장 버튼을 찾을 수 없습니다');
    }

    function loadPlaceDetails(placeId) {
        fetch(`/partner/placeDetails/${placeId}`)
            .then(response => response.text())
            .then(html => {
                placeDetails.innerHTML = html;
				showPlaceDetails(); // 숙소 정보를 클릭할 때 호출
				loadRoomList(placeId); // 방 목록도 함께 로드
            })
            .catch(error => {
                console.error('숙소 상세 정보 로드 오류:', error);
                placeDetails.innerHTML = '<p>숙소 상세 정보를 로드하는 중 오류가 발생했습니다. 다시 시도해주세요.</p>';
            });
    }
	
	function loadRoomList(placeId) {
	    fetch(`/partner/roomListHtml?placeId=${placeId}`)
	        .then(response => response.text())
	        .then(html => {
	            const roomListContainer = document.getElementById('roomList');
	            roomListContainer.innerHTML = html;

	            // 항상 reservation-details를 표시
	            document.querySelector('.reservation-details').style.display = 'block';

	            if (roomListContainer.querySelectorAll('.room-item').length > 0) {
	                // 방이 있을 경우
	                document.querySelector('.no-user-selected').style.display = 'none';
	            } else {
	                // 방이 없을 경우
	                document.querySelector('.no-user-selected').style.display = 'block';
	                document.querySelector('.no-user-selected').innerHTML = 
	                    '<i class="fa-solid"></i><br>등록된 방이 없습니다.<br>방을 등록해주세요.';
	            }

	            // 방 등록 버튼이 항상 보이도록 함
	            const roomSaveButton = document.getElementById('roomSave');
	            if (roomSaveButton) {
	                roomSaveButton.style.display = 'block';
	            }
	        })
	        .catch(error => {
	            console.error('방 목록 로드 오류:', error);
	            document.getElementById('roomList').innerHTML = '<li>방 목록을 로드하는 중 오류가 발생했습니다.</li>';
	            
	            // 오류 발생 시에도 reservation-details와 방 등록 버튼을 표시
	            document.querySelector('.reservation-details').style.display = 'block';
	            const roomSaveButton = document.getElementById('roomSave');
	            if (roomSaveButton) {
	                roomSaveButton.style.display = 'block';
	            }
	        });
	}

    function loadRoomRegisterForm(placeId) {
        fetch(`/partner/roomRegisterForm?placeId=${placeId}`)
            .then(response => response.text())
            .then(html => {
                roomDetails.innerHTML = html;
                setupRoomRegisterForm();
                showRoomDetails(); // 방 등록 폼을 보여줄 때 호출
            })
            .catch(error => {
                console.error('방 등록 폼 로드 오류:', error);
                roomDetails.innerHTML = '<p>방 등록 폼을 로드하는 중 오류가 발생했습니다. 다시 시도해주세요.</p>';
            });
    }

    function setupRoomRegisterForm() {
        const form = document.querySelector('form[action="/partner/roomSave"]');
        if (form) {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                const formData = new FormData(this);
                fetch('/partner/roomSave', {
                    method: 'POST',
                    body: formData,
                })
                .then(response => {
                    if (response.ok) {
                        alert('방이 성공적으로 등록되었습니다.');
                        window.location.reload();
                    } else {
                        alert('방 저장에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('방 저장 오류:', error);
                    alert('방 저장 중 오류가 발생했습니다.');
                });
            });
        }
    }

    searchResults.addEventListener('click', function(e) {
        const userItem = e.target.closest('.user-item');
        if (userItem) {
            searchResults.querySelectorAll('.user-item').forEach(item => item.classList.remove('selected'));
            userItem.classList.add('selected');

            noUserSelectedReservation.style.display = 'none';
            reservationDetails.style.display = 'block';

            console.log('사용자가 클릭한 항목:', userItem.textContent);
            console.log('예약 정보 및 활동 정보 표시됨');
        }
    });
    
    function showPlaceDetails() {
        placeDetails.style.display = 'block';
        roomDetails.style.display = 'none';
    }

    function showRoomDetails() {
        placeDetails.style.display = 'none';
        roomDetails.style.display = 'block';
    }
});
