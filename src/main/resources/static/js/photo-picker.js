/*
 * 사진 선택(파일로 추가 / 링크로 추가) 위젯.
 *
 * "파일로 추가" 탭에서 고른 파일은 선택 즉시 사진 업로드(10번 API, POST /api/books/images)로 올리고,
 * 돌아온 URL을 "링크로 추가" 탭의 입력란에 채워 넣음 — 실제 제출값은 항상 이 입력란 하나로 통일함.
 * "링크로 추가" 탭은 이미 어딘가에 있는 이미지 URL을 그대로 쓰고 싶을 때 씀.
 *
 * 사용법: initPhotoPicker({ containerId, urlInputId, label }) — containerId 안에 탭 UI를 그려 넣고,
 * urlInputId로 만든 링크 입력란의 값을 getPhotoUrl(urlInputId)로 읽음. 입력란 id를 백엔드 필드명과
 * 맞춰두면(예: "coverImageUrl") 검증 실패 시 showFieldErrors가 그대로 이 입력란을 가리킬 수 있음.
 */

function initPhotoPicker({ containerId, urlInputId, label }) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.classList.add('photo-picker');
    container.innerHTML =
        '<div class="field-head"><label for="' + urlInputId + '">' + escapeHtml(label) + '</label></div>' +
        '<div class="photo-tabs">' +
        '<button type="button" class="active" data-photo-tab="file">파일로 추가</button>' +
        '<button type="button" data-photo-tab="link">링크로 추가</button>' +
        '</div>' +
        '<div class="photo-panel" data-panel="file">' +
        '<div class="drop-zone" data-role="drop-zone">' +
        '<span class="drop-label">+ 사진 추가</span>' +
        '<span class="drop-hint">클릭해서 선택하거나, 파일을 끌어다 놓으세요</span>' +
        '</div>' +
        '<input type="file" accept="image/*" data-role="file-input" class="hidden">' +
        '<div class="field-error hidden" data-role="upload-error"></div>' +
        '<div class="photo-upload-hint">jpg · jpeg · png · webp, 5MB 이하만 가능해요</div>' +
        '</div>' +
        '<div class="photo-panel hidden" data-panel="link">' +
        '<input type="text" id="' + urlInputId + '" placeholder="https://...">' +
        '<div class="field-error hidden" id="' + urlInputId + '-error"></div>' +
        '</div>';

    const tabs = container.querySelectorAll('[data-photo-tab]');
    const panels = container.querySelectorAll('.photo-panel');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            panels.forEach(p => p.classList.toggle('hidden', p.dataset.panel !== tab.dataset.photoTab));
        });
    });

    const dropZone = container.querySelector('[data-role="drop-zone"]');
    const fileInput = container.querySelector('[data-role="file-input"]');
    const urlInput = container.querySelector('#' + urlInputId);
    const uploadError = container.querySelector('[data-role="upload-error"]');

    function showPreview(localUrl) {
        dropZone.classList.add('has-preview');
        dropZone.innerHTML = '<img class="photo-preview" src="' + localUrl + '" alt="선택한 사진">';
    }

    function resetDropZone() {
        dropZone.classList.remove('has-preview');
        dropZone.innerHTML =
            '<span class="drop-label">+ 사진 추가</span>' +
            '<span class="drop-hint">클릭해서 선택하거나, 파일을 끌어다 놓으세요</span>';
    }

    async function handleFile(file) {
        if (!file) return;
        uploadError.classList.add('hidden');

        const localUrl = URL.createObjectURL(file);
        showPreview(localUrl);
        dropZone.classList.add('uploading');

        try {
            const result = await api.upload('/api/books/images', file);
            urlInput.value = result.url;
        } catch (error) {
            resetDropZone();
            uploadError.textContent = error.message || '업로드에 실패했어요';
            uploadError.classList.remove('hidden');
        } finally {
            dropZone.classList.remove('uploading');
        }
    }

    dropZone.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', () => handleFile(fileInput.files[0]));
    dropZone.addEventListener('dragover', (event) => {
        event.preventDefault();
        dropZone.classList.add('dragover');
    });
    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
    dropZone.addEventListener('drop', (event) => {
        event.preventDefault();
        dropZone.classList.remove('dragover');
        if (event.dataTransfer.files[0]) handleFile(event.dataTransfer.files[0]);
    });
}

/** 최종 제출용 URL. 파일 업로드가 성공하면 링크 입력란에 그 URL이 채워져 있으므로 항상 이 값만 읽으면 됨. */
function getPhotoUrl(urlInputId) {
    const input = document.getElementById(urlInputId);
    return input ? input.value.trim() : '';
}
