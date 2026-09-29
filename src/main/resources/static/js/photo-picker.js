/*
 * 사진 선택(링크로 추가 / 파일로 추가) 위젯.
 *
 * 사진 업로드 API(10번, 정민 담당)가 아직 없어서 실제로 서버에 저장되는 값은 "링크로 추가" 탭의 URL뿐임.
 * "파일로 추가" 탭은 로컬 미리보기만 지원하고, 실제 제출값은 만들지 않음 — 10번 API가 올라오면 여기서
 * 업로드 호출 한 줄만 추가하면 됨(uploadedUrl을 채워서 getUrl()이 그 값을 반환하게 하면 됨).
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
        '<div class="photo-upload-hint">사진 업로드 기능은 아직 준비 중이에요. 지금은 미리보기만 되고, 실제 등록은 "링크로 추가" 탭의 URL로 저장돼요.</div>' +
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

    function showPreview(file) {
        if (!file) return;
        const url = URL.createObjectURL(file);
        dropZone.classList.add('has-preview');
        dropZone.innerHTML = '<img class="photo-preview" src="' + url + '" alt="선택한 사진">';
    }

    dropZone.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', () => showPreview(fileInput.files[0]));
    dropZone.addEventListener('dragover', (event) => {
        event.preventDefault();
        dropZone.classList.add('dragover');
    });
    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
    dropZone.addEventListener('drop', (event) => {
        event.preventDefault();
        dropZone.classList.remove('dragover');
        if (event.dataTransfer.files[0]) showPreview(event.dataTransfer.files[0]);
    });
}

/** 최종 제출용 URL. 지금은 "링크로 추가" 탭 값만 반환함. */
function getPhotoUrl(urlInputId) {
    const input = document.getElementById(urlInputId);
    return input ? input.value.trim() : '';
}
