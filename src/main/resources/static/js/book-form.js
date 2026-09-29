/*
 * 책 등록(3번 API). 받고 싶은 조건은 칩 버튼 토글로 처리(체크박스 대신 active 클래스로 상태 표시).
 */

document.addEventListener('DOMContentLoaded', () => {
    if (!requireLogin()) return;

    initPhotoPicker({ containerId: 'cover-picker', urlInputId: 'coverImageUrl', label: '표지 사진' });

    document.querySelectorAll('[data-condition]').forEach(btn => {
        btn.addEventListener('click', () => btn.classList.toggle('active'));
    });

    document.getElementById('submit').addEventListener('click', async () => {
        const errorBox = document.getElementById('form-error');
        errorBox.classList.add('hidden');
        showFieldErrors({ fields: null }, '');

        const isActive = (name) =>
            document.querySelector('[data-condition="' + name + '"]').classList.contains('active');

        const coverImageUrl = getPhotoUrl('coverImageUrl');
        if (coverImageUrl === '') {
            const label = document.getElementById('coverImageUrl-error');
            label.textContent = '사진을 선택해주세요';
            label.classList.remove('hidden');
            return;
        }

        try {
            const book = await api.post('/api/books', {
                title: document.getElementById('title').value.trim(),
                author: document.getElementById('author').value.trim() || null,
                coverImageUrl: coverImageUrl,
                description: document.getElementById('description').value.trim(),
                acceptsPrice: isActive('acceptsPrice'),
                acceptsSwap: isActive('acceptsSwap'),
                acceptsGiveaway: isActive('acceptsGiveaway')
            });
            location.href = '/book-detail.html?bookId=' + book.id;
        } catch (error) {
            if (!showFieldErrors(error, '')) {
                showError(errorBox, error);
            }
        }
    });
});
