$(document).ready(function() {
	// WebSocket 클라이언트 초기화 변수
	let stompClient = null;
	// 사용자 이름 생성 (랜덤 숫자를 붙인 'User')
	const username = 'User' + Math.floor(Math.random() * 1000);

	// WebSocket 연결을 설정하고 STOMP 클라이언트를 초기화하는 함수
	function connectWebSocket() {
		// SockJS를 통해 '/bangItBot' 엔드포인트에 연결
		const socket = new SockJS('/bangItBot');
		stompClient = Stomp.over(socket); // STOMP 클라이언트를 SockJS 위에 래핑

		const headers = {};
		const token = $("meta[name='_csrf']").attr("content"); // CSRF 토큰 추출
		const header = $("meta[name='_csrf_header']").attr("content"); // CSRF 헤더 이름 추출
		headers[header] = token; // 헤더에 CSRF 토큰 추가

		stompClient.connect({}, onConnected, onError); // 서버에 연결 시도
	}

	// WebSocket 연결 성공 시 호출되는 함수
	function onConnected() {
		console.log('WebSocket 연결 성공');
		// 공개 채팅과 개인 채팅의 두 개의 구독을 설정
		stompClient.subscribe('/topic/public', onMessageReceived);
		stompClient.subscribe('/user/' + username + '/private', onPrivateMessage);
		// 사용자가 채팅에 참여했음을 서버에 알림
		stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: username, type: 'JOIN' }));
	}

	// WebSocket 연결 오류 시 호출되는 함수
	function onError(error) {
		console.error('WebSocket 연결 오류:', error);
	}

	// 메시지를 서버로 전송하는 함수
	function sendMessage(message) {
		if (stompClient) {
			const chatMessage = {
				sender: username,
				content: message,
				type: 'CHAT'
			};
			// 서버로 메시지 전송
			stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));

			// 사용자 메시지를 화면에 표시
			displayMessage({ sender: username, content: message });

			// 사용자 메시지에서 숙소 검색 키워드 추출
			const accommodationKeywords = extractAccommodationKeywords(message);
			// '알려줘' 액션이 포함된 경우에만 숙소 검색 실행
			if (accommodationKeywords && accommodationKeywords.action === '알려줘') {
				queryAccommodations(accommodationKeywords);
			}
		}
	}

	// 숙소 검색 키워드 추출 함수
	function extractAccommodationKeywords(message) {
		const keywords = {
			region: null,
			detailed_address: null,
			type: null,
			action: null
		};

		// 지역(region) 키워드 추출 (예: '서울', '경기', '제주' 등)
		const regionMatch = message.match(/(서울|경기|강원|충북|충남|전북|전남|경북|경남|제주)/);
		if (regionMatch) {
			keywords.region = regionMatch[1];
		}

		// 상세주소(detailed_address) 키워드 추출 (예: '강남구', '노원구' 등)
		const addressMatch = message.match(/([가-힣]+[구시군동])/);
		if (addressMatch) {
			keywords.detailed_address = addressMatch[1];
		}

		// 숙소 유형(type) 추출 (예: '호텔', '모텔', '펜션' 등)
		const typeMatch = message.match(/(호텔|모텔|펜션|캠핑|콘도)/i);
		if (typeMatch) {
			keywords.type = typeMatch[1].toUpperCase();
		}

		// 동사(action) 추출 ('알려줘' 확인)
		if (message.includes('알려줘')) {
			keywords.action = '알려줘';
		}

		// 추출된 키워드가 하나라도 있으면 키워드 객체 반환, 없으면 null 반환
		return Object.values(keywords).some(v => v !== null) ? keywords : null;
	}

	// 숙소 정보를 조회하는 함수
	function queryAccommodations(keywords) {
		$.ajax({
			url: '/api/accommodations/search',
			method: 'GET',
			data: keywords,
			success: function(response) {
				// 서버 응답 로깅
				console.log('Server response:', response);
				let accommodations = response;
				// 문자열로 받은 경우 JSON 파싱 시도
				if (typeof response === 'string') {
					try {
						accommodations = JSON.parse(response);
					} catch (e) {
						console.error('Failed to parse server response:', e);
					}
				}
				// 배열인 경우에만 숙소 정보 표시
				if (Array.isArray(accommodations)) {
					displayAccommodations(accommodations, keywords);
				} else {
					console.error('Invalid accommodations data:', accommodations);
					displayMessage({ sender: 'bot', content: '숙소 정보 형식이 올바르지 않습니다.' });
				}
			},
			error: function(xhr, status, error) {
				console.error('숙소 정보 조회 중 오류 발생:', error);
				displayMessage({ sender: 'bot', content: '죄송합니다. 숙소 정보를 조회하는 중 오류가 발생했습니다.' });
			}
		});
	}

	// 숙소 정보를 화면에 표시하는 함수
	function displayAccommodations(accommodations, keywords) {
		console.log('Received accommodations:', accommodations);
		// 데이터 유효성 검사
		if (!Array.isArray(accommodations)) {
			console.error('Accommodations is not an array:', accommodations);
			displayMessage({ sender: 'bot', content: '숙소 정보를 불러오는 데 문제가 발생했습니다.' });
			return;
		}

		// 검색 결과 메시지 구성
		let message = '';
		if (keywords.region) message += `${keywords.region} `;
		if (keywords.detailed_address) message += `${keywords.detailed_address} `;
		if (keywords.type) message += `${keywords.type} `;
		message += `관련 숙소 정보입니다:\n\n`;

		// 각 숙소 정보 추가
		accommodations.forEach(function(accommodation, index) {
			message += `${index + 1}. ${accommodation.name}\n`;
			message += `   위치: ${accommodation.region} ${accommodation.detailed_address}\n`;
			message += `   유형: ${accommodation.type}\n`;
			message += `   설명: ${accommodation.description}\n`;
			if (accommodation.latitude && accommodation.longitude) {
				message += `   좌표: (${accommodation.latitude}, ${accommodation.longitude})\n`;
			}
			message += '\n';
		});

		message += '더 자세한 정보나 예약을 원하시면 숙소 이름을 말씀해 주세요.';
		displayMessage({ sender: 'bot', content: message });
	}

	// 공개 채팅 메시지를 수신할 때 호출되는 함수
	function onMessageReceived(payload) {
		const message = JSON.parse(payload.body);
		displayMessage(message);
	}

	// 개인 채팅 메시지를 수신할 때 호출되는 함수
	function onPrivateMessage(payload) {
		const message = JSON.parse(payload.body);
		displayMessage(message, true);
	}

	// 화면에 메시지를 표시하는 함수
	function displayMessage(message, isPrivate = false) {
		const $messagesContainer = $('#chatbot-messages');
		const messageClass = message.sender === 'bot' ? 'bot' : 'user';
		const privateClass = isPrivate ? 'private' : '';

		// 줄바꿈을 유지하면서 HTML 이스케이프 처리
		const content = message.content.replace(/\n/g, '<br>').replace(/</g, '&lt;').replace(/>/g, '&gt;');

		// 메시지 HTML 구성
		let messageHTML = '';
		if (messageClass === 'bot') {
			messageHTML = `
            <div class="message ${messageClass} ${privateClass}">
                <div class="profile">
                    <img src="/images/bangitbot.png" alt="Bot Profile">
                </div>
                <div class="bubble">
                    ${content}
                </div>
            </div>
            `;
		} else {
			messageHTML = `
            <div class="message ${messageClass} ${privateClass}">
                <div class="bubble">
                    ${content}
                </div>
            </div>
            `;
		}

		// 메시지를 채팅창에 추가하고 스크롤
		$messagesContainer.append(messageHTML);
		$messagesContainer.scrollTop($messagesContainer[0].scrollHeight);
	}

	// 채팅봇 토글 버튼 이벤트 핸들러
	$(document).off('click', '#chatbot-toggle').on('click', '#chatbot-toggle', function(e) {
		e.preventDefault();
		const $chatbotContainer = $('#chatbot-container');
		const $chatbotIcon = $('#chatbot-toggle i');
		const $messagesContainer = $('#chatbot-messages');

		if ($chatbotContainer.is(':visible')) {
			// 채팅창 숨기기
			$chatbotContainer.stop().fadeOut(300, function() {
				$messagesContainer.empty();
				// 기본 메시지 추가
				$messagesContainer.append(`
                <div class="message bot">
                    <div class="profile">
                        <img src="/images/bangitbot.png" alt="Bot Profile">
                    </div>
                    <div class="bubble">
                        무엇을 도와드릴까요?
                    </div>
                </div>
                `);
			});
			$(this).removeClass('open');
			$chatbotIcon.attr('class', 'fas fa-comment-dots fa-flip-horizontal');
		} else {
			// 채팅창 보이기
			$chatbotContainer.stop().fadeIn(300);
			$(this).addClass('open');
			$chatbotIcon.attr('class', 'fas fa-times fa-flip-horizontal');
		}
	});

	// 채팅봇 전송 버튼 클릭 이벤트 핸들러
	$('#chatbot-send').click(function() {
		const message = $('#chatbot-input').val().trim();
		if (message) {
			sendMessage(message);
			$('#chatbot-input').val('');
		}
	});

	// 채팅봇 입력 필드 Enter 키 이벤트 핸들러
	$('#chatbot-input').keypress(function(e) {
		if (e.which == 13) { // Enter 키 코드
			$('#chatbot-send').click(); // 전송 버튼 클릭 이벤트 트리거
		}
	});

	// 페이지 로드 시 채팅창 초기 상태 설정
	$('#chatbot-container').hide();

	// WebSocket 연결 시작
	connectWebSocket();
});