/*
 * 거래 제안하기(4번 API, 화면 설계서 5번 화면). 도서 상세의 "가격 제안하기"/"책 교환 제안하기" 버튼에서
 * bookId·intent를 받아 들어옴. intent는 어느 입력란에 먼저 초점을 줄지만 정할 뿐 — 실제 요청 종류는
 * 가격·사진 조합으로 그대로 계산됨(02_ERD.md).
 */

function requestTypeLabel(price, hasPhoto) {
    if (price > 0 && hasPhoto) return '웃돈 주고 교환 제안';
    if (price > 0 && !hasPhoto) return '구매 제안';
    if (price === 0 && hasPhoto) return '교환 요청';
    return '나눔 요청';
}

function updateComboHint() {
    const price = Number(document.getElementById('offeredPrice').value) || 0;
    const hasPhoto = getPhotoUrl('offeredPhotoUrl') !== '';
    document.getElementById('combo-current').textContent =
        '지금 입력대로면 "' + requestTypeLabel(price, hasPhoto) + '"(으)로 전달돼요';
}

async function loadTarget(bookId) {
    const book = await api.get('/api/books/' + bookId);
    document.getElementById('target-title').textContent = book.title + ' · ' + book.author;
    document.getElementById('target-owner').textContent = '판매자: ' + book.ownerNickname;
    if (book.coverImageUrl) {
        document.getElementById('target-cover').src = book.coverImageUrl;
    }
    return book;
}

document.addEventListener('DOMContentLoaded', async () => {
    if (!requireLogin()) return;

    const bookId = param('bookId');
    const intent = param('intent');
    if (!bookId) {
        location.href = '/index.html';
        return;
    }

    let book;
    try {
        book = await loadTarget(bookId);
    } catch (error) {
        location.href = '/index.html';
        return;
    }

    if (auth.memberId === book.ownerId) {
        alert('본인이 등록한 책에는 거래를 제안할 수 없어요');
        location.href = '/book-detail.html?bookId=' + bookId;
        return;
    }
    if (book.status !== 'TRADING') {
        alert('이미 거래가 끝난 책이에요');
        location.href = '/book-detail.html?bookId=' + bookId;
        return;
    }

    initPhotoPicker({ containerId: 'offer-photo-picker', urlInputId: 'offeredPhotoUrl', label: '교환할 책 사진' });

    updateComboHint();
    if (intent === 'swap') {
        document.getElementById('offeredPhotoUrl').focus();
    } else {
        document.getElementById('offeredPrice').focus();
    }

    document.getElementById('offeredPrice').addEventListener('input', updateComboHint);
    document.getElementById('offeredPhotoUrl').addEventListener('input', updateComboHint);

    document.getElementById('submit-request').addEventListener('click', async () => {
        const errorBox = document.getElementById('request-error');
        errorBox.classList.add('hidden');

        const offeredPhotoUrl = getPhotoUrl('offeredPhotoUrl');
        try {
            await api.post('/api/books/' + bookId + '/requests', {
                offeredPrice: Number(document.getElementById('offeredPrice').value) || 0,
                offeredPhotoUrl: offeredPhotoUrl === '' ? null : offeredPhotoUrl,
                message: document.getElementById('message').value.trim() || null
            });
            alert('거래 요청을 보냈어요. 마이페이지에서 확인할 수 있어요.');
            location.href = '/mypage.html';
        } catch (error) {
            showError(errorBox, error);
        }
    });
});
