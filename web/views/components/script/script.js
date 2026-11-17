/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */




/* =========================================================
   TỰ ĐỘNG THÊM ICON BÊN CẠNH Ô INPUT (giống ô input ở trang đăng nhập)
   - Bỏ qua: ô đã có input-group, ô có class "no-icon", ô trong bảng, form-floating, ô nhỏ (-sm)
   ========================================================= */
(function () {
    var RULES = [
        [/email|mail/, 'fa-envelope'],
        [/pass|mat_?khau|matkhau/, 'fa-lock'],
        [/phone|sdt|tel|dien_?thoai/, 'fa-phone'],
        [/recipient|fullname|full_name|username|ho_?ten|nguoinhan|customer|contactname/, 'fa-user'],
        [/province|city|tinh|thanh/, 'fa-city'],
        [/ward|district|phuong|xa\b|quan|huyen/, 'fa-map-marked-alt'],
        [/street|duong/, 'fa-road'],
        [/address|detail|diachi/, 'fa-location-dot'],
        [/price|amount|fee|cost|gia|tien|budget/, 'fa-coins'],
        [/qty|quantity|stock|soluong|number/, 'fa-hashtag'],
        [/watt/, 'fa-bolt'],
        [/fps|resolution/, 'fa-display'],
        [/game/, 'fa-gamepad'],
        [/brand|thuonghieu/, 'fa-copyright'],
        [/categor|danhmuc/, 'fa-layer-group'],
        [/socket/, 'fa-microchip'],
        [/date|from|to\b|ngay/, 'fa-calendar-alt'],
        [/search|keyword|query|\bq\b|tim/, 'fa-search'],
        [/comment|content|message|note|reason|desc|noidung|ghichu|lydo|error/, 'fa-comment-dots'],
        [/status|severity|type|role|trangthai|loai/, 'fa-tag'],
        [/title|name|ten|code|ma\b/, 'fa-pen']
    ];
    function pickIcon(el) {
        if (el.dataset && el.dataset.icon) return el.dataset.icon;
        if (el.type === 'email') return 'fa-envelope';
        if (el.type === 'password') return 'fa-lock';
        if (el.type === 'tel') return 'fa-phone';
        if (el.type === 'date' || el.type === 'datetime-local') return 'fa-calendar-alt';
        if (el.type === 'url') return 'fa-link';
        var key = ((el.name || '') + ' ' + (el.id || '') + ' ' + (el.getAttribute('placeholder') || '')).toLowerCase();
        for (var i = 0; i < RULES.length; i++) if (RULES[i][0].test(key)) return RULES[i][1];
        return el.tagName === 'SELECT' ? 'fa-list' : (el.tagName === 'TEXTAREA' ? 'fa-align-left' : 'fa-pen');
    }
    function decorate(el) {
        if (el.dataset.iconDone || el.classList.contains('no-icon')) return;
        if (el.classList.contains('otp-box') || el.closest('#otp-container, .otp-group')) return;   // ô OTP: không gắn icon
        if (el.closest('.input-group, .form-floating, table, .navbar, .no-icon, .pagination')) return;
        if (el.classList.contains('form-control-sm') || el.classList.contains('form-select-sm')) return;
        if (el.type === 'hidden' || el.type === 'checkbox' || el.type === 'radio' || el.type === 'file'
                || el.type === 'range' || el.type === 'color' || el.type === 'submit' || el.type === 'button') return;
        el.dataset.iconDone = '1';
        var wrap = document.createElement('div');
        wrap.className = 'input-group';
        el.className.split(/\s+/).forEach(function (c) {      // chuyển margin (mb-3...) lên wrapper
            if (/^m[bt]-\d$/.test(c)) { wrap.classList.add(c); el.classList.remove(c); }
        });
        var span = document.createElement('span');
        span.className = 'input-group-text';
        var i = document.createElement('i');
        i.className = 'fas ' + pickIcon(el);
        span.appendChild(i);
        el.parentNode.insertBefore(wrap, el);
        wrap.appendChild(span);
        wrap.appendChild(el);
    }
    function run() {
        document.querySelectorAll('form input.form-control, form select.form-select, form textarea.form-control, form select.form-control')
            .forEach(decorate);
    }
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', run); else run();
})();


/* =========================================================
   SIDEBAR ẨN/HIỆN BẰNG NÚT 3 GẠCH (dùng chung admin + user)
   - Trạng thái = class "sb-collapsed" trên <html> (đặt sớm trong <head> để không nhấp nháy)
   - Máy tính: nhớ lựa chọn bằng localStorage (gz_sidebar_open)
   - Điện thoại: sidebar trượt đè lên nội dung, bấm nền mờ / chọn 1 mục để đóng
   ========================================================= */
(function () {
    var root = document.documentElement;
    var KEY = 'gz_sidebar_open';
    var mq = window.matchMedia('(max-width: 991.98px)');

    function isMobile() { return mq.matches; }

    /* Đo chiều cao thật của navbar để sidebar dính đúng dưới navbar */
    function setNavH() {
        var nav = document.querySelector('.navbar-custom, .navbar-admin');
        if (nav) root.style.setProperty('--app-nav-h', nav.offsetHeight + 'px');
    }

    function syncUi() {
        var open = !root.classList.contains('sb-collapsed');
        var btn = document.getElementById('sidebarToggle');
        if (btn) btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    }

    function setCollapsed(collapsed) {
        root.classList.toggle('sb-collapsed', collapsed);
        if (!isMobile()) {
            try { localStorage.setItem(KEY, collapsed ? '0' : '1'); } catch (e) { }
        }
        syncUi();
    }

    function init() {
        setNavH();
        syncUi();
        if (!document.getElementById('appSidebar')) return;   // trang không có sidebar
        if (!document.getElementById('sbBackdrop')) {
            var bd = document.createElement('div');
            bd.id = 'sbBackdrop';
            bd.className = 'sb-backdrop';
            document.body.appendChild(bd);
        }
    }

    document.addEventListener('click', function (e) {
        if (e.target.closest('#sidebarToggle')) {
            setCollapsed(!root.classList.contains('sb-collapsed'));
        } else if (e.target.closest('#sbBackdrop')) {
            setCollapsed(true);
        } else if (isMobile() && e.target.closest('#appSidebar a')) {
            root.classList.add('sb-collapsed');   // chọn mục xong tự đóng (không lưu)
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && isMobile() && !root.classList.contains('sb-collapsed')) setCollapsed(true);
    });

    /* Đổi kích thước qua mốc điện thoại <-> máy tính: áp lại trạng thái phù hợp */
    function onBreakpoint() {
        var saved = null;
        try { saved = localStorage.getItem(KEY); } catch (e) { }
        root.classList.toggle('sb-collapsed', isMobile() || saved !== '1');
        setNavH();
        syncUi();
    }
    if (mq.addEventListener) mq.addEventListener('change', onBreakpoint);
    else if (mq.addListener) mq.addListener(onBreakpoint);

    window.addEventListener('resize', setNavH);
    window.addEventListener('load', setNavH);
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
    else init();
})();
