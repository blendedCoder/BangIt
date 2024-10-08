document.addEventListener('DOMContentLoaded', function() {
	// 전역 변수 선언
	let stompClient = null; // STOMP 클라이언트 인스턴스
	let subscription = null; // WebSocket 구독 인스턴스
	const userId = 'User' + Math.floor(Math.random() * 1000); // 랜덤 유저 ID 생성
	let isChatbotVisible = false; // 챗봇 UI 가시성 상태를 추적하는 변수
	let lastMessageContent = null; // 마지막으로 받은 메시지 내용을 저장하는 변수
	let lastMessageId = null; // 마지막으로 받은 메시지 ID를 저장하는 변수

	// WebSocket 연결 함수
	function connectWebSocket() {
		// 기존 WebSocket 연결이 있다면 먼저 해제
		if (stompClient !== null) {
			stompClient.disconnect();
		}
		// SockJS와 STOMP 클라이언트를 사용하여 WebSocket 연결 생성
		const socket = new SockJS('/bangItBot');
		stompClient = Stomp.over(socket);

		// CSRF 토큰을 헤더에 추가
		const headers = {};
		const token = document.querySelector("meta[name='_csrf']").getAttribute("content");
		const header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
		headers[header] = token;

		// WebSocket 연결 시도
		stompClient.connect(headers, onConnected, onError);
	}

	// WebSocket 연결 성공 시 호출되는 함수
	function onConnected() {
		console.log('WebSocket 연결 성공');
		// 기존 구독이 없다면 구독을 설정
		if (!subscription) {
			subscription = stompClient.subscribe('/topic/responses', onMessageReceived);
		}
	}

	// WebSocket 연결 오류 시 호출되는 함수
	function onError(error) {
		console.error('WebSocket 연결 오류:', error);
		displayMessage({ sender: 'bot', content: '죄송합니다. 서버와의 연결에 문제가 있습니다. 잠시 후 다시 시도해 주세요.' });
	}

	// 메시지 전송 함수
	function sendMessage(message) {
		if (stompClient && stompClient.connected) {
			const chatMessage = {
				content: message,
				key: userId,
				userId: userId
			};
			stompClient.send("/app/query", {}, JSON.stringify(chatMessage));
			displayMessage({ sender: 'user', content: message });
		} else {
			console.error('WebSocket is not connected');
			//displayMessage({ sender: 'bot', content: '서버와의 연결이 끊어졌습니다. 페이지를 새로고침 해주세요.' });
		}
	}

	// 메시지 수신 처리 함수
	function onMessageReceived(payload) {
		try {
			const message = JSON.parse(payload.body);
			if (message.content) {
				if (message.id && message.id !== lastMessageId) {
					lastMessageId = message.id;
					displayMessage({ sender: 'bot', content: message.content });
				} else if (!message.id && message.content !== lastMessageContent) {
					lastMessageContent = message.content;
					displayMessage({ sender: 'bot', content: message.content });
				} else {
					console.log('Duplicate or invalid message, ignoring. ID:', message.id);
				}
			}
		} catch (error) {
			console.error('Error parsing message:', error);
		}
	}

	// 메시지 화면 표시 함수
	function displayMessage(message) {
		const messagesContainer = document.getElementById('chatbot-messages');
		const messageClass = message.sender === 'bot' ? 'bot' : 'user';

		// XSS 방지를 위한 이스케이프 처리 및 링크 변환
		let content = message.content.replace(/\n/g, '<br>').replace(/</g, '&lt;').replace(/>/g, '&gt;');
		content = content.replace(/(https?:\/\/[^\s]+|\/\w+\/[\w-]+)/g, '<a href="$1" target="_blank">$1</a>');

		let messageHTML = '';
		if (messageClass === 'bot') {
			messageHTML = `
                <div class="message ${messageClass}">
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
                <div class="message ${messageClass}">
                    <div class="bubble">
                        ${content}
                    </div>
                </div>
            `;
		}

		// 메시지를 화면에 추가하고 스크롤을 최신 메시지로 이동
		messagesContainer.insertAdjacentHTML('beforeend', messageHTML);
		messagesContainer.scrollTop = messagesContainer.scrollHeight;
	}

	// 챗봇 UI 토글 함수
	function toggleChatbot() {
		const chatbotContainer = document.getElementById('chatbot-container');
		const chatbotIcon = document.querySelector('#chatbot-toggle i');
		const chatbotToggle = document.getElementById('chatbot-toggle');

		if (isChatbotVisible) {
			chatbotContainer.classList.remove('visible');
			setTimeout(() => {
				chatbotContainer.style.display = 'none';
			}, 300);
			chatbotToggle.classList.remove('open');
			chatbotIcon.className = 'fas fa-comment-dots fa-flip-horizontal';
		} else {
			chatbotContainer.style.display = 'block';
			setTimeout(() => {
				chatbotContainer.classList.add('visible');
			}, 10);
			chatbotToggle.classList.add('open');
			chatbotIcon.className = 'fas fa-times fa-flip-horizontal';
		}

		isChatbotVisible = !isChatbotVisible; // 상태 토글
	}

	// 이벤트 리스너 설정
	document.getElementById('chatbot-toggle').addEventListener('click', function(e) {
		e.preventDefault();
		toggleChatbot();
	});

	document.getElementById('chatbot-send').addEventListener('click', function() {
		const input = document.getElementById('chatbot-input');
		const message = input.value.trim();
		if (message) {
			sendMessage(message);
			input.value = '';
		}
	});

	document.getElementById('chatbot-input').addEventListener('keypress', function(e) {
		if (e.key === 'Enter') {
			document.getElementById('chatbot-send').click();
		}
	});

	// 초기 상태 설정
	document.getElementById('chatbot-container').style.display = 'none';

	// WebSocket 연결 시작
	connectWebSocket();
});
