document.addEventListener('DOMContentLoaded', function() {
    // HTML 요소를 변수로 선언
    const openModalBtn = document.getElementById("open-modal-btn"); // 모달 열기 버튼
    const closeModalBtn = document.getElementById("close-modal-btn"); // 모달 닫기 버튼
    const modalPaymentBtn = document.getElementById("modal-payment-button"); // 결제 요청 버튼
    const modal = document.getElementById("reservation-modal"); // 모달 창

    // 모달 열기
    openModalBtn.addEventListener('click', function() {
        modal.style.display = 'block'; // 모달을 화면에 표시
        initTossPayments(); // Toss Payments 초기화 함수 호출
    });

    // 모달 닫기
    closeModalBtn.addEventListener('click', function() {
        modal.style.display = 'none'; // 모달을 화면에서 숨김
    });

    // Toss Payments 초기화 함수 (비동기 함수)
    async function initTossPayments() {
        const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm"; // Toss Payments Client Key (테스트용)
        const tossPayments = TossPayments(clientKey); // Toss Payments 객체 생성
        const customerKey = "3hAXYvqhkTWwG5abB0FWy"; // Customer Key (고객 식별자)
        
        // 결제 위젯 설정
        const widgets = tossPayments.widgets({
            customerKey,
        });

        // reservationId를 포함하는 HTML 요소 가져오기
        const reservationIdElement = document.getElementById("reservationId");
        if (!reservationIdElement) {
            console.error("reservationId 요소를 찾을 수 없습니다.");
            return; // 요소가 없을 경우 함수 종료
        }
        const reservationId = reservationIdElement.value;

        if (!reservationId) {
            console.error("유효하지 않은 reservationId");
            return; // reservationId가 유효하지 않을 경우 함수 종료
        }

        // 서버로부터 orderId 가져오기
        let orderId;
        try {
            // 서버에 POST 요청을 보내 orderId를 생성
            const response = await fetch('/generate-order-id', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ reservationId: reservationId }) // 예약 ID를 요청 본문에 포함
            });

            if (!response.ok) {
                throw new Error(`서버 응답 오류: ${response.status}`); // 서버 응답이 정상적이지 않을 경우 오류 발생
            }
            const data = await response.json();
            orderId = data.orderId; // 서버로부터 생성된 orderId를 가져옴
            console.log("Generated Order ID:", orderId);
        } catch (error) {
            console.error("orderId 생성 중 오류 발생:", error); // orderId 생성 중 오류가 발생하면 로그 출력
            return;
        }

        try {
            // 서버로부터 결제 금액 가져오기
            const response = await fetch(`/getAmount?reservationId=${reservationId}`);
            if (!response.ok) {
                throw new Error(`서버 응답 오류: ${response.status}`); // 서버 응답이 정상적이지 않을 경우 오류 발생
            }
            const data = await response.json();
            const amount = data.amount; // 결제 금액 가져오기

            if (!amount || isNaN(amount)) {
                throw new Error("결제 금액이 올바르게 설정되지 않았습니다."); // 결제 금액이 유효하지 않을 경우 오류 발생
            }

            // 결제 금액 설정
            widgets.setAmount({
                currency: "KRW", // 통화 설정
                value: amount, // 결제 금액 설정
            });

            // 결제 방법과 약관을 렌더링
            await Promise.all([
                widgets.renderPaymentMethods({
                    selector: "#payment-method", // 결제 방법 선택 UI를 렌더링할 요소 선택자
                    variantKey: "DEFAULT", // 기본 변형 키 설정
                }),
                widgets.renderAgreement({
                    selector: "#agreement", // 결제 약관 UI를 렌더링할 요소 선택자
                    variantKey: "AGREEMENT" // 약관 변형 키 설정
                }),
            ]);

            // 결제 요청 처리
            modalPaymentBtn.addEventListener("click", async function() {
                try {
                    // 사용자가 선택한 결제 방법 가져오기
                    const paymentMethodElement = document.querySelector('input[name="paymentMethod"]:checked');
                    const paymentMethod = paymentMethodElement ? paymentMethodElement.value : 'CARD'; // 기본값은 'CARD'

                    // Toss Payments 결제 요청
                    await widgets.requestPayment({
                        orderId: orderId, // 주문 ID
                        orderName: "예약", // 주문 이름
                        successUrl: `${window.location.origin}/success?reservationId=${reservationId}&paymentMethod=${paymentMethod}`, // 결제 성공 시 리다이렉트할 URL
                        failUrl: `${window.location.origin}/fail?reservationId=${reservationId}&paymentMethod=${paymentMethod}`, // 결제 실패 시 리다이렉트할 URL
                    });

                    // 결제 요청 후 모달 닫기
                    modal.style.display = 'none';
                } catch (error) {
                    console.error("결제 요청 처리 중 오류 발생:", error); // 결제 요청 중 오류가 발생하면 로그 출력
                }
            });
        } catch (error) {
            console.error("결제 금액 설정에 오류가 발생했습니다:", error); // 결제 금액 설정 중 오류 발생 시 로그 출력
        }
    }
});
