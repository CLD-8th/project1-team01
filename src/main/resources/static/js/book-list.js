/*
 * 도서 목록 화면(1번 API) + 인기 랭킹(11번 API, "인기순위" 필터로 통합).
 *
 * 화면 설계서 1번 화면 기준 필터는 전체/거래중/거래완료/인기순위 4개 칩. 인기순위는 별도 목록(11번 API)이라
 * 검색·페이지 이동 없이 TOP 10만 보여줌.
 */

const PAGE_SIZE = 20;
let currentStatus = '';
let currentPage = 0;

function conditionSummary(book) {
    if (book.status === 'COMPLETED') return '-';
    const parts = [];
    if (book.acceptsPrice) parts.push('가격');
    if (book.acceptsSwap) parts.push('교환');
    if (book.acceptsGiveaway) parts.push('나눔');

    if (parts.length === 3) return '가격·교환·나눔 가능';
    if (parts.length === 0) return '조건 없음(무엇이든 제안 가능)';
    if (parts.length === 1) return parts[0] === '교환' ? '책 교환만' : parts[0] + ' 제안 가능';
    return parts.join('·') + ' 가능';
}

function bookCardHtml(book) {
    const cover = book.coverImageUrl || '/img/logo.svg';
    const closed = book.status === 'COMPLETED' ? ' completed' : '';
    return '<a class="book-card' + closed + '" href="/book-detail.html?bookId=' + book.id + '">' +
        '<img class="book-cover" src="' + escapeHtml(cover) + '" alt="">' +
        '<div class="book-title">' + escapeHtml(book.title) + '</div>' +
        '<div class="book-author">' + escapeHtml(book.author) + '</div>' +
        badge(book.status) +
        '<div class="book-condition">' + escapeHtml(conditionSummary(book)) + '</div>' +
        '</a>';
}

function rankingCardHtml(book) {
    const cover = book.coverImageUrl || '/img/logo.svg';
    return '<a class="book-card" href="/book-detail.html?bookId=' + book.id + '">' +
        '<span class="rank-no">' + book.rank + '위</span>' +
        '<img class="book-cover" src="' + escapeHtml(cover) + '" alt="">' +
        '<div class="book-title">' + escapeHtml(book.title) + '</div>' +
        '<div class="book-author">' + escapeHtml(book.author) + '</div>' +
        '<div class="book-condition">거래 요청 ' + book.requestCount + '회</div>' +
        '</a>';
}

async function loadRanking() {
    document.getElementById('search-bar').classList.add('hidden');
    document.getElementById('pager').innerHTML = '';

    const ranking = await api.get('/api/books/ranking');
    const grid = document.getElementById('book-grid');
    const empty = document.getElementById('book-empty');
    document.getElementById('total-count').textContent = '';

    if (ranking.length === 0) {
        grid.innerHTML = '';
        empty.textContent = '아직 거래 요청이 없어요';
        empty.classList.remove('hidden');
    } else {
        empty.classList.add('hidden');
        grid.innerHTML = ranking.map(rankingCardHtml).join('');
    }
}

async function loadBooks(page) {
    document.getElementById('search-bar').classList.remove('hidden');
    currentPage = page;
    const keyword = document.getElementById('keyword').value.trim();

    const query = new URLSearchParams({ page, size: PAGE_SIZE });
    if (keyword) query.set('keyword', keyword);
    if (currentStatus) query.set('status', currentStatus);

    const result = await api.get('/api/books?' + query.toString());

    const grid = document.getElementById('book-grid');
    const empty = document.getElementById('book-empty');
    document.getElementById('total-count').textContent = '총 ' + result.totalElements + '권';

    if (result.content.length === 0) {
        grid.innerHTML = '';
        empty.textContent = '등록된 책이 없어요';
        empty.classList.remove('hidden');
    } else {
        empty.classList.add('hidden');
        grid.innerHTML = result.content.map(bookCardHtml).join('');
    }

    renderPager(result.totalPages, result.page);
}

function renderPager(totalPages, page) {
    const pager = document.getElementById('pager');
    if (totalPages <= 1) {
        pager.innerHTML = '';
        return;
    }
    let html = '';
    for (let i = 0; i < totalPages; i++) {
        html += '<button class="' + (i === page ? 'current' : '') + '" data-page="' + i + '">' + (i + 1) + '</button>';
    }
    pager.innerHTML = html;
    pager.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => loadBooks(Number(btn.dataset.page)));
    });
}

function selectFilter(status) {
    currentStatus = status;
    document.querySelectorAll('#status-filter button').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.status === status);
    });
    if (status === 'RANKING') {
        loadRanking();
    } else {
        loadBooks(0);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    selectFilter('');

    document.querySelectorAll('#status-filter button').forEach(btn => {
        btn.addEventListener('click', () => selectFilter(btn.dataset.status));
    });

    document.getElementById('keyword').addEventListener('keyup', (event) => {
        if (event.key === 'Enter') loadBooks(0);
    });
});
