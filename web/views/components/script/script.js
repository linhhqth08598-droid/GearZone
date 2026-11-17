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
