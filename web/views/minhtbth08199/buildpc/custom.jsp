<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/views/components/header.jsp" />
<style>
    .build-hero{
        background:linear-gradient(130deg,#111827,#243b61);
        border-radius:20px;
        color:#fff;
        overflow:hidden;
        position:relative
    }
    .build-hero:after{
        content:'🖥';
        position:absolute;
        right:4%;
        bottom:-35px;
        font-size:180px;
        opacity:.13
    }
    .component-card{
        border:1px solid #e6eaf0;
        border-radius:14px;
        transition:.18s;
        height:100%;
        cursor:pointer;
        min-width:0
    }
    .component-card:hover{
        border-color:#ff4757;
        transform:translateY(-2px);
        box-shadow:0 .5rem 1.4rem rgba(15,23,42,.1)
    }
    .component-icon{
        width:42px;
        height:42px;
        flex:0 0 42px;
        border-radius:11px;
        background:#fff0f1;
        color:#ff4757;
        display:grid;
        place-items:center;
        font-size:19px
    }
    .component-card .name{
        font-weight:700;
        color:#1e293b
    }
    .component-card .selection{
        font-size:.82rem;
        color:#64748b;
        min-height:40px;
        white-space:normal;
        overflow-wrap:anywhere;
        word-break:break-word
    }
    .component-card .flex-grow-1{
        min-width:0
    }
    .summary-card{
        position:sticky;
        top:95px;
        border-radius:16px
    }
    .summary-total{
        font-size:1.9rem;
        font-weight:800;
        color:#ff4757
    }
    .progress{
        height:8px
    }
    .picker-modal{
        background:rgba(15,23,42,.58)
    }
    .part-row{
        border:1px solid #edf0f4;
        border-radius:12px;
        transition:.15s
    }
    .part-row:hover{
        border-color:#ff9da7;
        background:#fffafb
    }
    .build-empty{
        border:2px dashed #d8dee8;
        border-radius:18px;
        background:#fff
    }
    .btn-gear{
        background:#ff4757;
        border-color:#ff4757;
        color:#fff
    }
    .btn-gear:hover{
        background:#e53b4d;
        border-color:#e53b4d;
        color:#fff
    }
    @media(min-width:1200px){
        #slots>.col-md-6{
            width:50%
        }
    }
    @media(max-width:991px){
        .summary-card{
            position:static
        }
    }
</style>
<div class="container py-4 py-lg-5">
    <div class="build-hero p-4 p-md-5 mb-4 shadow-sm"><div class="position-relative" style="z-index:1"><span class="badge text-bg-danger mb-2">GEARZONE PC BUILDER</span><h1 class="fw-bold mb-2">Tự xây dựng dàn PC của bạn</h1><p class="mb-0 text-white-50">Chọn linh kiện theo nhu cầu, kiểm tra giá và hoàn thiện cấu hình chỉ trong vài phút.</p></div></div>
    <div class="card border-0 shadow-sm rounded-4 mb-4" id="game-builder"><div class="card-body p-4"><c:if test="${not empty gameBuildError}"><div class="alert alert-danger py-2 mb-3" role="alert"><c:out value="${gameBuildError}"/></div></c:if><div class="row align-items-center g-3"><div class="col-lg-5"><span class="badge text-bg-danger mb-2">BUILD GAME 15–30 TRIỆU</span><h4 class="fw-bold mb-1">Gợi ý nhanh theo game</h4><p class="text-muted mb-0">Chọn game và tầm giá để GearZone tự ghép linh kiện đang có trong kho.</p></div><div class="col-lg-7"><form class="row g-2 align-items-end" method="post" action="${pageContext.request.contextPath}/recommend-pc"><input type="hidden" name="preset" value="true"><input type="hidden" name="purpose" value="gaming"><input type="hidden" name="resolution" value="1080p"><input type="hidden" name="priority" value="balanced"><div class="col-sm-5"><label class="form-label small fw-semibold" for="gameProfile">Game</label><select class="form-select" id="gameProfile" name="gameProfile" onchange="updateGameHint()"><option value="valorant">Valorant</option><option value="lol">Liên Minh Huyền Thoại</option><option value="cs2">Counter-Strike 2</option></select></div><div class="col-sm-4"><label class="form-label small fw-semibold" for="gameBudget">Tầm giá</label><select class="form-select" id="gameBudget" name="budget" onchange="updateGameHint()"><option value="15000000">15 triệu</option><option value="20000000">20 triệu</option><option value="25000000">25 triệu</option><option value="30000000">30 triệu</option></select></div><div class="col-sm-3 d-grid"><button class="btn btn-gear" type="submit">Tạo build</button></div></form></div></div><div id="gameHint" class="small text-muted mt-3" aria-live="polite"></div></div></div><script>function updateGameHint() {
         const g = document.getElementById('gameProfile'), b = document.getElementById('gameBudget'), h = document.getElementById('gameHint');
         if (!g || !b || !h)
             return;
         const focus = g.value === 'cs2' ? 'CS2 ưu tiên CPU để giữ nhịp khung hình ổn định.' : g.value === 'valorant' ? 'Valorant ưu tiên cấu hình cân bằng, thiên về CPU.' : 'LMHT ưu tiên cấu hình cân bằng CPU và đồ họa.';
         h.textContent = 'Build ' + g.options[g.selectedIndex].text + ' · ' + b.options[b.selectedIndex].text + ' · mục tiêu 1080p. ' + focus + ' RAM và màn hình được chọn theo hàng còn kho; hệ thống kiểm tra socket, chuẩn DDR và nguồn.'
     }
     updateGameHint();</script>
                            <c:choose><c:when test="${empty build}"><div class="build-empty text-center p-5"><i class="fas fa-desktop fa-3x text-danger mb-3"></i><h3 class="fw-bold">Bắt đầu cấu hình mới</h3><p class="text-muted mb-4">Đặt tên cho bộ PC để dễ lưu và chia sẻ sau này.</p><form class="d-flex justify-content-center gap-2" method="post" action="${pageContext.request.contextPath}/build-pc"><input class="form-control" style="max-width:330px" name="buildName" maxlength="150" placeholder="VD: PC Gaming 25 triệu" required><button class="btn btn-gear px-4" type="submit"><i class="fas fa-plus me-1"></i>Tạo cấu hình</button></form></div></c:when><c:otherwise>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4"><div><div class="text-muted small mb-1">CẤU HÌNH ĐANG CHỈNH SỬA</div><h2 class="fw-bold mb-0"><c:out value="${build.buildName}" default="Cấu hình PC mới"/></h2></div><div class="d-flex gap-2"><form method="post" action="${pageContext.request.contextPath}/build-pc"><input type="hidden" name="buildName" value="Cấu hình PC mới"><button class="btn btn-outline-secondary" type="submit"><i class="fas fa-plus me-1"></i>Build mới</button></form><a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/recommend-pc"><i class="fas fa-wand-magic-sparkles me-1"></i>Nhờ gợi ý</a><form method="post" action="${pageContext.request.contextPath}/build-pc"><input type="hidden" name="action" value="addToCart"><input type="hidden" name="buildId" value="${build.buildId}"><button class="btn btn-gear" type="submit"><i class="fas fa-cart-plus me-1"></i>Bỏ vào giỏ hàng</button></form></div></div>
            <div class="row g-4"><section class="col-lg-8"><div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3 p-md-4"><div class="d-flex align-items-center justify-content-between border-bottom pb-3 mb-3"><div><h5 class="fw-bold mb-1">Linh kiện cấu hình</h5><span class="small text-muted">Nhấn vào từng ô để tìm và chọn linh kiện.</span></div><span class="badge text-bg-light border text-secondary"><span id="selectedCount">0</span>/7 bắt buộc</span></div><div id="slots" class="row g-3"></div></div></div></section><aside class="col-lg-4"><div class="card border-0 shadow-sm summary-card"><div class="card-body p-4"><div class="d-flex justify-content-between"><span class="text-muted">Tạm tính</span><i class="fas fa-receipt text-danger"></i></div><div id="total" class="summary-total my-2">0 ₫</div><div class="d-flex justify-content-between small text-muted mb-2"><span>Tiến độ lắp ráp</span><span id="progressText">0/7</span></div><div class="progress mb-4"><div id="progressBar" class="progress-bar bg-danger" style="width:0%"></div></div><div id="notice" class="alert alert-danger small py-2 d-none" role="alert"></div><div id="compatibility" class="alert alert-info small py-2" role="status">Kiểm tra tương thích sẽ hiện ở đây.</div><div class="border-top pt-3 small text-muted"><p class="mb-2"><i class="fas fa-shield-halved text-success me-2"></i>Giá được kiểm tra lại tại máy chủ</p><p class="mb-0"><i class="fas fa-circle-info text-primary me-2"></i>Chưa gồm khuyến mãi và phí lắp ráp</p></div></div></div></aside></div>
            <div id="picker" class="modal fade picker-modal" tabindex="-1"><div class="modal-dialog modal-dialog-scrollable modal-lg"><div class="modal-content border-0 rounded-4"><div class="modal-header"><div><h5 id="dialogTitle" class="modal-title fw-bold">Chọn linh kiện</h5><span class="small text-muted">Chỉ hiển thị sản phẩm đang còn hàng.</span></div><button type="button" class="btn-close" onclick="closePicker()"></button></div><div class="modal-body"><div class="input-group mb-3"><span class="input-group-text bg-white"><i class="fas fa-search"></i></span><input id="search" class="form-control" placeholder="Tìm theo tên linh kiện"><button class="btn btn-gear" type="button" onclick="loadParts()">Tìm kiếm</button></div><div id="partList"></div></div></div></div></div>
                                </c:otherwise></c:choose>
    </div>
<c:if test="${not empty build}"><script>
    const ctx = '${pageContext.request.contextPath}', buildId =${build.buildId};
    const slots = [{id: 1, name: 'CPU', icon: 'fa-microchip', required: true}, {id: 2, name: 'Mainboard', icon: 'fa-server', required: true}, {id: 3, name: 'RAM', icon: 'fa-memory', required: true}, {id: 4, name: 'Card đồ họa', icon: 'fa-gamepad', required: true}, {id: 5, name: 'SSD', icon: 'fa-hard-drive', required: true}, {id: 6, name: 'HDD', icon: 'fa-database'}, {id: 7, name: 'Nguồn (PSU)', icon: 'fa-bolt', required: true}, {id: 8, name: 'Vỏ máy (Case)', icon: 'fa-box', required: true}, {id: 9, name: 'Tản nhiệt', icon: 'fa-fan'}, {id: 10, name: 'Màn hình', icon: 'fa-display'}, {id: 11, name: 'Phụ kiện gaming', icon: 'fa-keyboard'}];
    let selected = {}, activeSlot = null, modal;
    const money = v => new Intl.NumberFormat('vi-VN', {maximumFractionDigits: 0}).format(v || 0) + ' ₫';
    function el(t, c, x) {
        let e = document.createElement(t);
        e.className = c || '';
        if (x !== undefined)
            e.textContent = x;
        return e
    }
    function render() {
        let root = document.getElementById('slots');
        root.replaceChildren();
        let n = 0;
        slots.forEach(s => {
            let i = selected[s.id];
            if (i && s.required)
                n++;
            let col = el('div', 'col-md-6'), card = el('div', 'component-card p-3'), head = el('div', 'd-flex gap-3 align-items-start'), icon = el('div', 'component-icon');
            icon.innerHTML = '<i class="fas ' + s.icon + '"></i>';
            let info = el('div', 'flex-grow-1');
            info.append(el('div', 'name', s.name + (s.required ? ' *' : '')), el('div', 'selection text-truncate', i ? i.name + ' · SL ' + i.quantity : 'Chưa chọn linh kiện'));
            head.append(icon, info);
            card.append(head);
            let foot = el('div', 'd-flex justify-content-between align-items-center mt-3 pt-2 border-top');
            foot.append(el('small', i ? 'text-danger fw-bold' : 'text-muted', i ? money(i.price * i.quantity) : 'Chọn ngay'));
            let b = el('button', i ? 'btn btn-sm btn-outline-danger' : 'btn btn-sm btn-gear', i ? 'Thay đổi' : 'Chọn');
            b.type = 'button';
            b.onclick = () => openPicker(s);
            foot.append(b);
            if (i) {
                let rm = el('button', 'btn btn-sm text-secondary ms-2', 'Xóa');
                rm.type = 'button';
                rm.onclick = () => removePart(i.id);
                foot.append(rm)
            }
            card.append(foot);
            card.onclick = () => openPicker(s);
            col.append(card);
            root.append(col)
        });
        document.getElementById('selectedCount').textContent = n;
        document.getElementById('progressText').textContent = n + '/7';
        document.getElementById('progressBar').style.width = (n / 7 * 100) + '%'
    }
    async function api(u, o) {
        let r = await fetch(u, o), d = await r.json();
        if (!r.ok)
            throw Error(d.error || 'Không thể xử lý yêu cầu');
        return d
    }
    function notice(m = '') {
        let n = document.getElementById('notice');
        n.textContent = m;
        n.classList.toggle('d-none', !m)
    }
    function showCompatibility(c) {
        let box = document.getElementById('compatibility');
        if (!c) {
            box.className = 'alert alert-info small py-2';
            box.textContent = 'Chưa đủ linh kiện để kiểm tra tương thích.';
            return
        }
        box.replaceChildren();
        box.className = 'alert small py-2 ' + (c.compatible ? 'alert-success' : 'alert-danger');
        (c.errors || []).forEach(x => box.append(el('div', 'mb-1', 'Không tương thích: ' + x)));
        (c.warnings || []).forEach(x => box.append(el('div', 'mb-1', 'Lưu ý: ' + x)));
        if (c.compatible && !(c.warnings || []).length)
            box.append(el('div', '', 'Các linh kiện đã chọn tương thích theo thông số hiện có.'));
        box.append(el('div', 'mt-2 border-top pt-2', 'DDR4/DDR5 của RAM hệ thống cần khớp với Mainboard; GDDR trên card đồ họa là bộ nhớ riêng, không cần trùng chuẩn DDR của RAM.'))
    }
    function state(d) {
        selected = {};
        d.items.forEach(x => selected[x.categoryId] = x);
        document.getElementById('total').textContent = money(d.totalPrice);
        showCompatibility(d.compatibility);
        render()
    }
    async function loadState() {
        try {
            notice();
            state(await api(ctx + '/api/build/state?buildId=' + buildId))
        } catch (e) {
            render();
            notice(e.message)
        }
    }
    function openPicker(s) {
        activeSlot = s;
        document.getElementById('dialogTitle').textContent = 'Chọn ' + s.name;
        document.getElementById('search').value = '';
        if (modal)
            modal.show();
        else {
            let p = document.getElementById('picker');
            p.style.display = 'block';
            p.classList.add('show')
        }
        loadParts()
    }
    function closePicker() {
        if (modal)
            modal.hide();
        else {
            let p = document.getElementById('picker');
            p.style.display = 'none';
            p.classList.remove('show')
        }
    }
    async function loadParts() {
        let l = document.getElementById('partList');
        l.replaceChildren(el('div', 'text-center py-4 text-muted', 'Đang tải linh kiện…'));
        try {
            let d = await api(ctx + '/api/build/parts?categoryId=' + activeSlot.id + '&q=' + encodeURIComponent(document.getElementById('search').value));
            l.replaceChildren();
            if (!d.items.length) {
                l.append(el('div', 'text-center py-4 text-muted', 'Chưa có linh kiện phù hợp hoặc còn hàng.'));
                return
            }
            d.items.forEach(p => {
                let row = el('div', 'part-row p-3 mb-2 d-flex justify-content-between align-items-center gap-3'), info = el('div', 'overflow-hidden');
                info.append(el('div', 'fw-semibold text-truncate', p.name), el('small', 'text-danger fw-bold', money(p.price)));
                let a = el('div', 'd-flex align-items-center gap-2 flex-shrink-0'), q = {value: 1};
                if (activeSlot.id === 3) {
                    let input = document.createElement('input');
                    input.type = 'number';
                    input.min = 1;
                    input.max = 32;
                    input.value = selected[3]?.quantity || 1;
                    input.className = 'form-control form-control-sm';
                    input.style.width = '64px';
                    a.append(input);
                    q = input
                }
                let b = el('button', 'btn btn-sm btn-gear', 'Chọn');
                b.type = 'button';
                b.onclick = () => choose(p.id, q.value);
                a.append(b);
                row.append(info, a);
                l.append(row)
            })
        } catch (e) {
            l.replaceChildren(el('div', 'alert alert-danger mb-0', e.message))
        }
    }
    async function choose(id, q) {
        try {
            notice();
            let body = new URLSearchParams({buildId, partId: id, quantity: q});
            state(await api(ctx + '/api/build/part', {method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}, body}));
            closePicker()
        } catch (e) {
            notice(e.message)
        }
    }
    async function removePart(id) {
        try {
            notice();
            let body = new URLSearchParams({buildId, partId: id});
            state(await api(ctx + '/api/build/remove', {method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}, body}))
        } catch (e) {
            notice(e.message)
        }
    }
    document.addEventListener('DOMContentLoaded', () => {
        render();
        loadState();
        if (window.bootstrap && window.bootstrap.Modal)
            modal = new bootstrap.Modal(document.getElementById('picker'));
        document.getElementById('search').addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                e.preventDefault();
                loadParts()
            }
        })
    });
    </script></c:if><jsp:include page="/views/components/footer.jsp" />
