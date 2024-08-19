document.addEventListener('DOMContentLoaded', function() {
    const openModalBtn = document.getElementById("open-modal-btn");
    const closeModalBtn = document.getElementById("close-modal-btn");
    const modalPaymentBtn = document.getElementById("modal-payment-button");
    const modal = document.getElementById("reservation-modal");

    // 모달 열기
    openModalBtn.addEventListener('click', function() {
        modal.style.display = 'block';
        initTossPayments();
    });

    // 모달 닫기
    closeModalBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    function initTossPayments() {
        const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
        const tossPayments = TossPayments(clientKey);
        const customerKey = "3hAXYvqhkTWwG5abB0FWy";
        const widgets = tossPayments.widgets({
            customerKey,
        });

        const reservationIdElement = document.getElementById("reservationId");
        if (!reservationIdElement) {
            console.error("reservationId 요소를 찾을 수 없습니다.");
            return;
        }
        const reservationId = reservationIdElement.value;

        fetch(`/getAmount?reservationId=${reservationId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`서버 응답 오류: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                const amount = data.amount;
                if (!amount || isNaN(amount)) {
                    throw new Error("결제 금액이 올바르게 설정되지 않았습니다.");
                }

                widgets.setAmount({
                    currency: "KRW",
                    value: amount,
                });

                Promise.all([
                    widgets.renderPaymentMethods({
                        selector: "#payment-method",
                        variantKey: "DEFAULT",
                    }),
                    widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
                ]);

                modalPaymentBtn.addEventListener("click", async function() {
                    await widgets.requestPayment({
                        orderId: reservationId,
                        orderName: "세인트존스 호텔 숙박 예약",
                        successUrl: window.location.origin + "/success",
                        failUrl: window.location.origin + "/fail",
                        customerEmail: "customer123@gmail.com",
                        customerName: "김토스",
                        customerMobilePhone: "01012341234",
                    });
                });
            })
            .catch(error => {
                console.error("결제 금액 설정에 오류가 발생했습니다:", error);
            });
    }
});
