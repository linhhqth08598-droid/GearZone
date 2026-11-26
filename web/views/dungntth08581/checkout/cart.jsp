<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <h3 class="fw-bold mb-4"><i class="fas fa-shopping-cart text-primary me-2"></i>Giỏ hàng</h3>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty cartLines}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <p class="text-muted mb-3">Giỏ hàng đang trống.</p>
                <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-primary">Xây dựng cấu hình PC</a>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty compat.errors}">
                <div class="alert alert-danger"><strong><i class="fas fa-times-circle me-1"></i>Linh kiện trong giỏ không tương thích:</strong>
                    <ul class="mb-0"><c:forEach var="e" items="${compat.errors}"><li><c:out value="${e}"/></li></c:forEach></ul></div>
            </c:if>
            <c:if test="${not empty compat.warnings}">
                <div class="alert alert-warning"><strong><i class="fas fa-exclamation-triangle me-1"></i>Lưu ý:</strong>
                    <ul class="mb-0"><c:forEach var="w" items="${compat.warnings}"><li><c:out value="${w}"/></li></c:forEach></ul></div>
            </c:if>
            <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                <div class="table-responsive">
                    <table class="table align-middle mb-0" style="table-layout:fixed;width:100%;">
                        <colgroup>
                            <col>
                            <col style="width:140px;">
                            <col style="width:155px;">
                            <col style="width:150px;">
                            <col style="width:56px;">
                        </colgroup>
                        <thead class="table-light"><tr><th>Linh kiện</th><th class="text-end">Đơn giá</th><th class="text-center">Số lượng</th><th class="text-end">Thành tiền</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="l" items="${cartLines}">
                                <tr>
                                    <td>
                                        <div class="fw-medium text-break"><c:out value="${l.partName}"/></div>
                                        <c:if test="${l.shortage}">
                                            <div class="small text-danger stock-warn mt-1">
                                                <i class="fas fa-exclamation-triangle me-1"></i>Chỉ còn ${l.available} trong kho
                                            </div>
                                        </c:if>
                                    </td>
                                    <td class="text-end text-nowrap"><fmt:formatNumber value="${l.unitPrice}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td class="text-center text-nowrap">
                                        <div class="d-inline-flex align-items-center justify-content-center gap-1 cart-qty" data-cart-id="${l.cartId}" data-price="${l.unitPrice}" data-available="${l.available}">
                                            <button type="button" class="btn btn-sm btn-outline-secondary qty-minus" tabindex="-1">−</button>
                                            <input type="number" value="${l.quantity}" min="0" max="99" class="form-control form-control-sm text-center qty-input" style="width:68px">
                                            <button type="button" class="btn btn-sm btn-outline-secondary qty-plus" tabindex="-1">+</button>
                                        </div>
                                    </td>
                                    <td class="text-end text-nowrap fw-semibold line-total"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td class="text-end text-nowrap">
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" class="m-0">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="cartId" value="${l.cartId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <span class="fs-5 fw-bold">Tổng: <span class="text-danger" id="cartTotal"><fmt:formatNumber value="${cartTotal}" type="number" maxFractionDigits="0"/> ₫</span></span>
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">Tiến hành đặt hàng</a>
                </div>
            </div></div>
        </c:otherwise>
    </c:choose>
</div>


<script>
(function () {
    const url = '${pageContext.request.contextPath}/cart';
    const fmt = n => new Intl.NumberFormat('vi-VN').format(n) + ' ₫';
    document.querySelectorAll('.cart-qty').forEach(box => {
        const input = box.querySelector('.qty-input'), row = box.closest('tr');
        const price = parseFloat(box.dataset.price);
        let timer = null, last = parseInt(input.value, 10);
        if (isNaN(last) || last < 1) last = 1;
        const clamp = v => {
            const n = parseInt(v, 10);
            if (isNaN(n)) return 0;
            return Math.min(99, Math.max(0, n));
        };

        function recalcTotal() {
            let t = 0;
            document.querySelectorAll('.cart-qty').forEach(b => {
                const inp = b.querySelector('.qty-input');
                const raw = parseInt(inp.value, 10);
                const q = isNaN(raw) ? 0 : Math.min(99, Math.max(0, raw));
                t += parseFloat(b.dataset.price) * q;
            });
            document.getElementById('cartTotal').textContent = fmt(t);
        }
        function renderStockWarn(avail, qty) {
            const limit = typeof avail === 'number' ? avail : (parseInt(box.dataset.available, 10) || 0);
            if (typeof avail === 'number') {
                box.dataset.available = String(avail);
            }
            let warn = row.querySelector('.stock-warn');
            if (qty > limit) {
                if (!warn) {
                    warn = document.createElement('div');
                    warn.className = 'small text-danger stock-warn mt-1';
                    row.children[0].appendChild(warn);
                }
                warn.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Chỉ còn ' + limit + ' trong kho';
            } else if (warn) {
                warn.remove();
            }
        }
        function removeRowNow() {
            clearTimeout(timer);
            const body = new URLSearchParams({action: 'remove', cartId: box.dataset.cartId});
            fetch(url, {method: 'POST', body}).then(() => {
                row.remove();
                recalcTotal();
                if (document.querySelectorAll('.cart-qty').length === 0) {
                    location.reload();
                }
            });
        }
        function commitTypedValue() {
            const rawStr = input.value.trim();
            if (rawStr === '') {
                input.value = last;
                row.querySelector('.line-total').textContent = fmt(price * last);
                renderStockWarn(null, last);
                recalcTotal();
                return;
            }
            const q = clamp(rawStr);
            input.value = q;
            if (q === 0) {
                removeRowNow();
                return;
            }
            row.querySelector('.line-total').textContent = fmt(price * q);
            renderStockWarn(null, q);
            recalcTotal();
            clearTimeout(timer);
            if (q === last) return;
            const body = new URLSearchParams({action: 'update', ajax: '1', cartId: box.dataset.cartId, quantity: q});
            fetch(url, {method: 'POST', body})
                .then(r => r.json())
                .then(d => {
                    if (d.removed) {
                        row.remove();
                        recalcTotal();
                        if (document.querySelectorAll('.cart-qty').length === 0) location.reload();
                        return;
                    }
                    if (!d.success) { input.value = last; commitTypedValue(); return; }
                    last = q;
                    row.querySelector('.line-total').textContent = fmt(d.lineTotal);
                    document.getElementById('cartTotal').textContent = fmt(d.total);
                    renderStockWarn(d.available, q);
                })
                .catch(() => { input.value = last; commitTypedValue(); });
        }
        function apply() {
            const rawStr = input.value.trim();
            if (rawStr === '') {
                clearTimeout(timer);
                return;
            }
            const q = clamp(rawStr);
            input.value = q;
            if (q === 0) {
                removeRowNow();
                return;
            }
            row.querySelector('.line-total').textContent = fmt(price * q);
            renderStockWarn(null, q);
            recalcTotal();
            clearTimeout(timer);
            timer = setTimeout(() => {
                if (q === last) return;
                const body = new URLSearchParams({action: 'update', ajax: '1', cartId: box.dataset.cartId, quantity: q});
                fetch(url, {method: 'POST', body})
                    .then(r => r.json())
                    .then(d => {
                        if (d.removed) {
                            row.remove();
                            recalcTotal();
                            if (document.querySelectorAll('.cart-qty').length === 0) location.reload();
                            return;
                        }
                        if (!d.success) { input.value = last; apply(); return; }
                        last = q;
                        row.querySelector('.line-total').textContent = fmt(d.lineTotal);
                        document.getElementById('cartTotal').textContent = fmt(d.total);
                        renderStockWarn(d.available, q);
                    })
                    .catch(() => { input.value = last; apply(); });
            }, 450);
        }
        input.addEventListener('input', apply);
        input.addEventListener('change', commitTypedValue);
        input.addEventListener('blur', commitTypedValue);
        box.querySelector('.qty-minus').addEventListener('click', () => {
            const cur = parseInt(input.value, 10);
            const next = (isNaN(cur) ? last : cur) - 1;
            if (next <= 0) {
                input.value = 0;
                removeRowNow();
                return;
            }
            input.value = clamp(next);
            commitTypedValue();
        });
        box.querySelector('.qty-plus').addEventListener('click', () => {
            const cur = parseInt(input.value, 10);
            input.value = clamp((isNaN(cur) ? last : cur) + 1);
            commitTypedValue();
        });
    });
})();
</script>

<jsp:include page="/views/components/footer.jsp" />
