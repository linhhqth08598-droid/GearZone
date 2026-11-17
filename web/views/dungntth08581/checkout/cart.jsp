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
                    <table class="table align-middle">
                        <thead class="table-light"><tr><th>Linh kiện</th><th class="text-end">Đơn giá</th><th style="width:190px">Số lượng</th><th class="text-end">Thành tiền</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="l" items="${cartLines}">
                                <tr>
                                    <td>
                                        <c:out value="${l.partName}"/>
                                        <c:if test="${l.shortage}"><div class="small text-danger stock-warn"><i class="fas fa-exclamation-triangle me-1"></i>Chỉ còn ${l.available} trong kho</div></c:if>
                                    </td>
                                    <td class="text-end"><fmt:formatNumber value="${l.unitPrice}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td>
                                        <div class="d-flex align-items-center gap-1 cart-qty" data-cart-id="${l.cartId}" data-price="${l.unitPrice}">
                                            <button type="button" class="btn btn-sm btn-outline-secondary qty-minus" tabindex="-1">−</button>
                                            <input type="number" value="${l.quantity}" min="1" max="99" class="form-control form-control-sm text-center qty-input" style="width:70px">
                                            <button type="button" class="btn btn-sm btn-outline-secondary qty-plus" tabindex="-1">+</button>
                                        </div>
                                    </td>
                                    <td class="text-end fw-semibold line-total"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td>
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
                <div class="d-flex justify-content-between align-items-center mt-2">
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
        let timer = null, last = parseInt(input.value) || 1;
        const clamp = v => Math.min(99, Math.max(1, parseInt(v) || 1));

        function recalcTotal() {
            let t = 0;
            document.querySelectorAll('.cart-qty').forEach(b => {
                t += parseFloat(b.dataset.price) * clamp(b.querySelector('.qty-input').value);
            });
            document.getElementById('cartTotal').textContent = fmt(t);
        }
        function apply() {
            const q = clamp(input.value);
            input.value = q;
            // Hiện ngay thành tiền + tổng (không cần chờ server)
            row.querySelector('.line-total').textContent = fmt(price * q);
            recalcTotal();
            clearTimeout(timer);
            timer = setTimeout(() => {
                if (q === last) return;
                const body = new URLSearchParams({action: 'update', ajax: '1', cartId: box.dataset.cartId, quantity: q});
                fetch(url, {method: 'POST', body})
                    .then(r => r.json())
                    .then(d => {
                        if (!d.success) { input.value = last; apply(); return; }
                        last = q;
                        row.querySelector('.line-total').textContent = fmt(d.lineTotal);
                        document.getElementById('cartTotal').textContent = fmt(d.total);
                        let warn = row.querySelector('.stock-warn');
                        if (d.shortage) {
                            if (!warn) { warn = document.createElement('div'); warn.className = 'small text-danger stock-warn'; row.children[0].appendChild(warn); }
                            warn.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Chỉ còn ' + d.available + ' trong kho';
                        } else if (warn) warn.remove();
                    })
                    .catch(() => { input.value = last; apply(); });
            }, 350);
        }
        input.addEventListener('input', apply);
        input.addEventListener('change', apply);
        box.querySelector('.qty-minus').addEventListener('click', () => { input.value = clamp(input.value) - 1; apply(); });
        box.querySelector('.qty-plus').addEventListener('click', () => { input.value = clamp(input.value) + 1; apply(); });
    });
})();
</script>

<jsp:include page="/views/components/footer.jsp" />
