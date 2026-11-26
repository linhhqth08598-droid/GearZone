<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container mt-4">
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show shadow-lg"
             style="position: fixed; top: 85px; right: 20px; z-index: 9999; min-width: 300px;">
            <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
            <a href="${pageContext.request.contextPath}/cart" class="alert-link ms-2">Xem giỏ hàng</a>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
            <c:remove var="error" scope="session"/>
        </c:if>
</div>

<div class="container my-5">
    <c:set var="curPrice" value="${not empty part.salePrice and part.salePrice > 0 ? part.salePrice : part.basePrice}" />
    <div class="row g-4">
        <!-- Ảnh sản phẩm -->
        <div class="col-md-5">
            <div class="card shadow-sm border-0 rounded-4 position-relative">
                <div class="card-body p-4 text-center">
                    <c:choose>
                        <c:when test="${not empty primaryImage}">
                            <img src="<c:out value='${primaryImage}'/>" alt="<c:out value='${part.partName}'/>" class="img-fluid rounded" style="max-height:320px; object-fit:contain;">
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex flex-column align-items-center justify-content-center rounded bg-light text-muted" style="height:260px;">
                                <i class="fas fa-image fa-3x mb-2"></i><span>Chưa có ảnh sản phẩm</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- NÚT YÊU THÍCH -->
                <button type="button" id="wishlistBtn" onclick="toggleWishlist(${part.partId})"
                        class="btn btn-light rounded-circle shadow-sm position-absolute p-0"
                        style="top: 12px; right: 12px; width: 38px; height: 38px; color: ${isWishlisted ? '#ff4757' : '#ccc'};">
                    <i class="fas fa-heart"></i>
                </button>
            </div>
        </div>

        <!-- Thông tin sản phẩm -->
        <div class="col-md-7">
            <h1 class="fw-bold mb-2"><c:out value="${part.partName}"/></h1>
            <p class="text-muted mb-3">Mã sản phẩm: <code><c:out value="${part.productCode}"/></code></p>

            <div class="d-flex align-items-baseline gap-2 mb-3">
                <h3 class="text-danger fw-bold mb-0">
                    <fmt:formatNumber value="${curPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                </h3>
                <c:if test="${not empty part.salePrice and part.salePrice > 0 and part.salePrice < part.basePrice}">
                    <span class="text-muted text-decoration-line-through small"><fmt:formatNumber value="${part.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                </c:if>
            </div>

            <div class="mb-3 p-3 bg-light rounded-3">
                <p class="mb-1"><strong><i class="fas fa-shield-alt text-success me-2"></i> Bảo hành:</strong> ${part.warrantyMonths} tháng</p>
                <p class="mb-1"><strong><i class="fas fa-info-circle text-primary me-2"></i> Trạng thái:</strong>
                    <c:choose>
                        <c:when test="${part.status == 'ACTIVE' and availableStock > 0}"><span class="text-success">Còn hàng</span></c:when>
                        <c:otherwise><span class="text-danger">Hết hàng</span></c:otherwise>
                    </c:choose>
                </p>
                <p class="mb-0"><strong><i class="fas fa-boxes-stacked text-secondary me-2"></i> Số lượng sẵn có:</strong> <span id="detailAvailableStock">${availableStock}</span> sản phẩm</p>
            </div>

            <div class="mb-3">
                <h6 class="fw-bold mb-1">Mô tả sản phẩm</h6>
                <p class="text-secondary mb-0"><c:out value="${empty part.description ? 'Đang cập nhật mô tả...' : part.description}"/></p>
            </div>

            <div class="d-flex align-items-center gap-3 mb-3">
                <label class="form-label fw-semibold mb-0">Số lượng</label>
                <div class="d-inline-flex align-items-center gap-1" id="detailQtyBox" data-max="${availableStock > 0 ? (availableStock > 99 ? 99 : availableStock) : 1}">
                    <button type="button" class="btn btn-sm btn-outline-secondary" id="detailQtyMinus" tabindex="-1">−</button>
                    <input type="number" id="detailQtyInput" value="1" min="1" max="${availableStock > 0 ? (availableStock > 99 ? 99 : availableStock) : 1}" class="form-control form-control-sm text-center" style="width:72px">
                    <button type="button" class="btn btn-sm btn-outline-secondary" id="detailQtyPlus" tabindex="-1">+</button>
                </div>
                <span class="text-muted small"><c:out value="${availableStock}"/> sản phẩm có sẵn</span>
            </div>

            <div class="d-flex flex-wrap gap-2">
                <%-- MUA NGAY: thêm đúng số lượng đã chọn rồi chuyển thẳng sang /checkout --%>
                <form id="buyNowForm" action="${pageContext.request.contextPath}/cart" method="POST" class="flex-grow-1 m-0">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="buyNow" value="1">
                    <input type="hidden" name="partId" value="${part.partId}">
                    <input type="hidden" name="quantity" id="buyNowQty" value="1">
                    <button type="submit" class="btn btn-danger w-100" ${part.status == 'ACTIVE' and availableStock > 0 ? '' : 'disabled'}>
                        <i class="fas fa-bolt"></i> Mua ngay
                    </button>
                </form>

                <form id="addToCartForm" action="${pageContext.request.contextPath}/cart" method="POST" class="flex-grow-1 m-0">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="partId" value="${part.partId}">
                    <input type="hidden" name="quantity" id="addToCartQty" value="1">
                    <button type="submit" class="btn btn-outline-danger w-100" ${part.status == 'ACTIVE' and availableStock > 0 ? '' : 'disabled'}>
                        <i class="fas fa-cart-plus"></i> Thêm vào giỏ
                    </button>
                </form>

                <!-- NÚT THEO DÕI GIÁ -->
                <button type="button" id="priceAlertBtn" onclick="togglePriceAlert(${part.partId})"
                        class="btn btn-outline-warning ${isPriceAlerted ? 'active' : ''}">
                    <i class="fas fa-bell"></i> <span id="priceAlertText">${isPriceAlerted ? 'Hủy theo dõi' : 'Theo dõi giá'}</span>
                </button>
            </div>
        </div>
    </div>

    <!-- THÔNG SỐ KỸ THUẬT (đọc từ bảng spec_* trong CSDL) -->
    <div class="row mt-4">
        <div class="col-lg-8">
            <div class="spec-card">
                <div class="spec-head"><i class="fas fa-list-ul text-danger me-2"></i>Thông số kỹ thuật</div>
                <table class="table table-bordered table-striped spec-table mb-0">
                    <tbody>
                        <c:forEach var="r" items="${generalSpecs}">
                            <tr><th><c:out value="${r[0]}"/></th><td><c:out value="${r[1]}"/></td></tr>
                        </c:forEach>
                        <c:forEach var="r" items="${techSpecs}">
                            <tr><th><c:out value="${r[0]}"/></th><td><c:out value="${r[1]}"/></td></tr>
                        </c:forEach>
                        <c:if test="${empty techSpecs}">
                            <tr><td colspan="2" class="text-muted fst-italic">Thông số kỹ thuật chi tiết đang được cập nhật.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- MODAL THEO DÕI GIÁ -->
<div class="modal fade" id="priceAlertModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form id="priceAlertForm" action="${pageContext.request.contextPath}/user/price-alert" method="POST">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-bell me-2"></i>Theo dõi giá sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="register">
                    <input type="hidden" name="partId" value="${part.partId}">
                    <p>Sản phẩm: <b class="text-dark">${part.partName}</b></p>
                    <p>Giá hiện tại: <b class="text-danger"><fmt:formatNumber value="${curPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></b></p>

                    <div class="input-group mb-3">
                        <span class="input-group-text bg-light"><i class="fas fa-tag text-warning"></i></span>
                        <input type="number" class="form-control" name="targetPrice" required min="1000" step="1000" placeholder="Nhập số tiền bạn muốn mua...">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4">Đăng ký</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- PHẦN ĐÁNH GIÁ -->
<jsp:include page="/views/linhhqth08598/product/review_section.jsp" />

<!-- SCRIPT AJAX -->
<script>
    (function () {
        const box = document.getElementById('detailQtyBox');
        const input = document.getElementById('detailQtyInput');
        const minusBtn = document.getElementById('detailQtyMinus');
        const plusBtn = document.getElementById('detailQtyPlus');
        const buyNowQty = document.getElementById('buyNowQty');
        const addToCartQty = document.getElementById('addToCartQty');
        if (box && input && buyNowQty && addToCartQty) {
            const maxQty = Math.max(1, parseInt(box.dataset.max, 10) || 1);
            const clampQty = (val) => {
                const n = parseInt(val, 10);
                if (isNaN(n) || n < 1) return 1;
                return Math.min(maxQty, n);
            };
            const syncQty = () => {
                const q = clampQty(input.value);
                input.value = q;
                buyNowQty.value = q;
                addToCartQty.value = q;
            };
            input.addEventListener('input', syncQty);
            input.addEventListener('change', syncQty);
            if (minusBtn) {
                minusBtn.addEventListener('click', () => {
                    input.value = clampQty(input.value) - 1;
                    syncQty();
                });
            }
            if (plusBtn) {
                plusBtn.addEventListener('click', () => {
                    input.value = clampQty(input.value) + 1;
                    syncQty();
                });
            }
            syncQty();
        }
    })();

    function toggleWishlist(partId) {
        fetch('${pageContext.request.contextPath}/user/wishlist?action=toggle&partId=' + partId, {method: 'POST'})
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        const btn = document.getElementById('wishlistBtn');
                        btn.style.color = data.isWishlisted ? '#ff4757' : '#ccc';
                    }
                });
    }

    function togglePriceAlert(partId) {
        const btn = document.getElementById('priceAlertBtn');
        if (btn.classList.contains('active')) {
            // Huỷ theo dõi
            fetch('${pageContext.request.contextPath}/user/price-alert?action=cancel&partId=' + partId, {method: 'POST'})
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            btn.classList.remove('active');
                            document.getElementById('priceAlertText').innerText = 'Theo dõi giá';
                        }
                    });
        } else {
            // Mở modal nhập giá mong muốn (mở bằng JS để vẫn dùng được sau khi vừa hủy theo dõi)
            bootstrap.Modal.getOrCreateInstance(document.getElementById('priceAlertModal')).show();
        }
    }

    // Gửi form đăng ký theo dõi giá bằng AJAX (không chuyển trang)
    document.getElementById('priceAlertForm').addEventListener('submit', function (e) {
        e.preventDefault();
        const form = this;
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = true;

        // Lưu ý: form có <input name="action"> nên form.action trả về phần tử input, phải dùng getAttribute
        fetch(form.getAttribute('action'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: new URLSearchParams(new FormData(form)).toString()
        })
                .then(res => res.text().then(text => ({status: res.status, url: res.url, text: text})))
                .then(r => {
                    let data;
                    try {
                        data = JSON.parse(r.text);
                    } catch (err) {
                        // Server không trả về JSON -> in ra để biết lỗi thật
                        console.error('Price-alert response not JSON:', r);
                        alert('Server trả về dữ liệu không hợp lệ (HTTP ' + r.status + '). Mở F12 > Console để xem chi tiết.');
                        return;
                    }
                    if (data.success) {
                        const modalEl = document.getElementById('priceAlertModal');
                        bootstrap.Modal.getOrCreateInstance(modalEl).hide();
                        const btn = document.getElementById('priceAlertBtn');
                        btn.classList.add('active');
                        document.getElementById('priceAlertText').innerText = 'Hủy theo dõi';
                        form.reset();
                        alert(data.message || 'Đăng ký thành công');
                    } else {
                        alert(data.message || 'Đăng ký thất bại');
                    }
                })
                .catch(err => {
                    console.error('Price-alert error:', err);
                    alert('Lỗi: ' + err);
                })
                .finally(() => {
                    submitBtn.disabled = false;
                });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />