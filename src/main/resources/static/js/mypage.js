/*
 * 마이페이지 3종(5·6·7번 API).
 *
 * 받은 요청의 수락·거절 버튼은 8·9번 API(POST /api/requests/{requestId}/accept·reject) 경로를 미리
 * 연결해둠 — 병현님 컨트롤러가 올라오면 별도 프론트 수정 없이 그대로 동작함. 그 전까지는 눌러도 404.
 *
 * offeredPhotoUrl은 기존 등록된 책을 참조하는 게 아니라 그냥 올린 사진이라(02_ERD.md), 와이어프레임 목업처럼
 * "「어떤 책」과 교환 희망" 같은 책 이름을 보여줄 수 없어 제시 사진 자체를 함께 보여주는 방식으로 대신함.
 */

let booksCache = null;

function describeOffer(req) {
    const hasPhoto = !!req.offeredPhotoUrl;
    if (req.offeredPrice > 0 && hasPhoto) return '웃돈 ' + req.offeredPrice.toLocaleString() + '원 + 교환 사진 제시';
    if (req.offeredPrice > 0) return '가격 제안 ' + req.offeredPrice.toLocaleString() + '원';
    if (hasPhoto) return '책 교환 제안 (사진 첨부)';
    return '나눔 요청';
}

function receivedStatusBadge(status) {
    const label = { PENDING: '대기중', ACCEPTED: '수락함', REJECTED: '거절함' }[status] || status;
    return '<span class="badge badge-' + status + '">' + label + '</span>';
}

function sentStatusBadge(status) {
    const label = { PENDING: '대기중', ACCEPTED: '수락됨', REJECTED: '거절됨' }[status] || status;
    return '<span class="badge badge-' + status + '">' + label + '</span>';
}

async function respondToRequest(requestId, action) {
    try {
        await api.post('/api/requests/' + requestId + '/' + action, {});
        loadTab('received');
    } catch (error) {
        alert(error.message || '처리에 실패했어요');
    }
}

const TABS = {
    books: {
        url: '/api/mypage/books',
        empty: '등록한 책이 없어요',
        render: (book) => '<div class="row-item ' + (book.status === 'COMPLETED' ? 'closed' : '') + '">' +
            '<img class="row-thumb" src="' + escapeHtml(book.coverImageUrl || '/img/logo.svg') + '" alt="">' +
            '<div class="row-body">' +
            '<div class="row-title"><a href="/book-detail.html?bookId=' + book.id + '">' + escapeHtml(book.title) + ' · ' + escapeHtml(book.author) + '</a></div>' +
            '<div class="row-sub">' + shortDate(book.createdAt) + '</div>' +
            '</div>' + badge(book.status) + '</div>'
    },
    received: {
        url: '/api/mypage/requests/received',
        empty: '받은 요청이 없어요',
        render: (req) => '<div class="row-item">' +
            '<img class="row-thumb" src="' + escapeHtml(req.bookCoverImageUrl || '/img/logo.svg') + '" alt="">' +
            '<div class="row-body">' +
            '<div class="row-title">' + escapeHtml(req.requesterNickname) + '님 → 내 책 "' + escapeHtml(req.bookTitle) + '"</div>' +
            '<div class="row-sub">' + escapeHtml(describeOffer(req)) + (req.message ? ' · ' + escapeHtml(req.message) : '') + '</div>' +
            '</div>' +
            receivedStatusBadge(req.status) +
            (req.status === 'PENDING'
                ? '<div class="row-actions">' +
                  '<button class="primary" data-accept="' + req.id + '">수락</button>' +
                  '<button data-reject="' + req.id + '">거절</button>' +
                  '</div>'
                : '') +
            '</div>'
    },
    sent: {
        url: '/api/mypage/requests/sent',
        empty: '보낸 요청이 없어요',
        render: (req) => '<div class="row-item">' +
            '<img class="row-thumb" src="' + escapeHtml(req.bookCoverImageUrl || '/img/logo.svg') + '" alt="">' +
            '<div class="row-body">' +
            '<div class="row-title"><a href="/book-detail.html?bookId=' + req.bookId + '">' + escapeHtml(req.bookTitle) + '</a> · 판매자 ' + escapeHtml(req.ownerNickname) + '</div>' +
            '<div class="row-sub">' + escapeHtml(describeOffer(req)) + '</div>' +
            '</div>' + sentStatusBadge(req.status) + '</div>'
    }
};

async function loadProfileStats() {
    try {
        booksCache = await api.get('/api/mypage/books');
        const completed = booksCache.filter(b => b.status === 'COMPLETED').length;
        document.getElementById('profile-stat').textContent =
            '등록한 책 ' + booksCache.length + '권 · 거래완료 ' + completed + '건';
    } catch (error) {
        document.getElementById('profile-stat').textContent = '';
    }
}

async function loadTab(name) {
    const tab = TABS[name];
    const list = document.getElementById('list');
    const empty = document.getElementById('empty');
    empty.classList.add('hidden');

    try {
        const items = name === 'books' && booksCache ? booksCache : await api.get(tab.url);
        if (items.length === 0) {
            list.innerHTML = '';
            empty.textContent = tab.empty;
            empty.classList.remove('hidden');
        } else {
            list.innerHTML = items.map(tab.render).join('');
            if (name === 'received') {
                list.querySelectorAll('[data-accept]').forEach(btn =>
                    btn.addEventListener('click', () => respondToRequest(btn.dataset.accept, 'accept')));
                list.querySelectorAll('[data-reject]').forEach(btn =>
                    btn.addEventListener('click', () => respondToRequest(btn.dataset.reject, 'reject')));
            }
        }
    } catch (error) {
        list.innerHTML = '';
        empty.textContent = error.message;
        empty.classList.remove('hidden');
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    if (!requireLogin()) return;

    document.getElementById('profile-nickname').textContent = auth.nickname;

    document.querySelectorAll('.tabs button').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            loadTab(btn.dataset.tab);
        });
    });

    await loadProfileStats();
    loadTab('books');
});
