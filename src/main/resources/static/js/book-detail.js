/*
 * 도서 상세(2번 API). 거래 요청 입력 자체는 book-request.html(5번 화면)로 분리돼있음 — 여기서는
 * "가격 제안하기"/"책 교환 제안하기" 버튼으로 그 화면에 힌트만 넘겨줌.
 */

async function loadBook(bookId) {
    const book = await api.get('/api/books/' + bookId);

    document.getElementById('title').textContent = book.title;
    document.getElementById('owner').textContent = book.author + ' · 등록자: ' + book.ownerNickname;
    document.getElementById('description').textContent = book.description;
    document.getElementById('badge-slot').innerHTML = badge(book.status);
    if (book.coverImageUrl) {
        document.getElementById('cover').src = book.coverImageUrl;
    }

    return book;
}

async function loadRankHint(bookId) {
    try {
        const ranking = await api.get('/api/books/ranking');
        const entry = ranking.find(item => item.id === Number(bookId));
        if (!entry) return;
        document.getElementById('rank-hint-text').textContent =
            '인기 도서 랭킹 ' + entry.rank + '위 · 거래 요청 ' + entry.requestCount + '회';
        document.getElementById('rank-hint').classList.remove('hidden');
    } catch (error) {
        // 랭킹은 부가 정보라 실패해도 상세 화면 전체를 막지 않음.
    }
}

function decideActionArea(book) {
    const actions = document.getElementById('action-buttons');
    if (!auth.loggedIn) {
        actions.classList.add('hidden');
        document.getElementById('login-hint').classList.remove('hidden');
        return;
    }
    if (auth.memberId === book.ownerId) {
        actions.classList.add('hidden');
        document.getElementById('own-book-hint').classList.remove('hidden');
        return;
    }
    if (book.status !== 'TRADING') {
        actions.classList.add('hidden');
        document.getElementById('not-trading-hint').classList.remove('hidden');
        return;
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const bookId = param('bookId');
    if (!bookId) {
        location.href = '/index.html';
        return;
    }
    try {
        const book = await loadBook(bookId);
        decideActionArea(book);
        loadRankHint(bookId);

        document.getElementById('offer-price').addEventListener('click', () => {
            location.href = '/book-request.html?bookId=' + bookId + '&intent=price';
        });
        document.getElementById('offer-swap').addEventListener('click', () => {
            location.href = '/book-request.html?bookId=' + bookId + '&intent=swap';
        });
    } catch (error) {
        document.getElementById('detail-card').innerHTML =
            '<div class="empty">책을 찾을 수 없어요</div>';
    }
});
