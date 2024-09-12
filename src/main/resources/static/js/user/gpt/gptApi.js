$(document).ready(function() {
    let conversationHistory = [];
    
    // 페이지 로드 시 초기 메시지 표시
    function displayWelcomeMessage() {
        const welcomeMessage = "안녕하세요. 무엇을 도와드릴까요?";
        addMessageToChat('assistant', welcomeMessage);
    }

    // 페이지 로드 시 실행
    displayWelcomeMessage();

    function sendQuestion() {
        const question = $('#question').val();
        if (!question.trim()) return; // 빈 질문은 무시

        // 사용자 질문을 대화 기록에 추가
        addMessageToChat('user', question);

        // 이전 대화 기록을 포함하여 API 요청
        $.ajax({
            url: '/ask',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                messages: conversationHistory
            }),
            beforeSend: function(xhr) {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            },
            success: function(response) {
                try {
                    let aiResponse;
                    if (typeof response === 'string') {
                        // JSON 문자열인 경우 파싱
                        const jsonResponse = JSON.parse(response);
                        if (jsonResponse.messages && jsonResponse.messages.length > 0) {
                            aiResponse = jsonResponse.messages[0].content;
                        } else if (jsonResponse.choices && jsonResponse.choices.length > 0) {
                            aiResponse = jsonResponse.choices[0].message.content;
                        } else {
                            throw new Error("Unexpected response format");
                        }
                    } else if (typeof response === 'object') {
                        // 이미 객체인 경우
                        if (response.messages && response.messages.length > 0) {
                            aiResponse = response.messages[0].content;
                        } else if (response.choices && response.choices.length > 0) {
                            aiResponse = response.choices[0].message.content;
                        } else {
                            throw new Error("Unexpected response format");
                        }
                    } else {
                        throw new Error("Unexpected response type");
                    }

                    addMessageToChat('assistant', aiResponse);
                } catch (e) {
                    console.error("Error processing response:", e);
                    addMessageToChat('error', "죄송합니다. 정상적으로 처리할수 없으니 새로고침(F5)을 눌러주세요.");
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX error:", status, error);
                addMessageToChat('error', 'Error occurred while fetching response: ' + error);
            }
        });

        // 입력 필드 초기화
        $('#question').val('');
    }

    // 질문 버튼 클릭 시
    $('#askButton').click(sendQuestion);

    // Enter 키를 눌렀을 때
    $('#question').keypress(function(e) {
        if (e.which === 13 && !e.shiftKey) { // Enter 키를 눌렀고 Shift 키를 누르지 않은 경우
            e.preventDefault(); // 기본 엔터 동작 방지 (폼 제출 등)
            sendQuestion();
        }
    });

    function addMessageToChat(role, content) {
        const messageDiv = $('<div>').addClass('chat-message').addClass(role);
        messageDiv.text(content);
        $('#chatContainer').append(messageDiv);

        // 대화 기록 업데이트
        conversationHistory.push({ role: role, content: content });

        // 스크롤을 최신 메시지로 이동
        $('#chatContainer').scrollTop($('#chatContainer')[0].scrollHeight);
    }
});