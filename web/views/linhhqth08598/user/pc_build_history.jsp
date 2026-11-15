<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-desktop text-primary me-2"></i>Lịch sử cấu hình PC</h3>
        <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-outline-primary">
            <i class="fas fa-plus me-1"></i> Tạo cấu hình mới
        </a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty buildHistoryList}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/1067/1067357.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn chưa có cấu hình PC nào được lưu.</h5>
                <p class="text-muted mb-4">Hãy thử sức với công cụ Build PC để tạo cho mình một dàn máy ưng ý nhé!</p>
                <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-primary px-4 py-2" style="border-radius: 8px;">Build PC ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="build" items="${buildHistoryList}">
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm border-0 rounded-4 h-100">
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <h5 class="fw-bold text-dark mb-0"><c:out value="${build.buildName}"/></h5>
                                    <c:choose>
                                        <c:when test="${build.status == 'SAVED'}"><span class="badge bg-success">Đã lưu</span></c:when>
                                        <c:otherwise><span class="badge bg-warning text-dark">Bản nháp</span></c:otherwise>
                                    </c:choose>
                                </div>

                                <p class="text-muted mb-2" style="font-size: 14px;">
                                    <i class="far fa-calendar-alt me-1"></i> Ngày tạo: <fmt:formatDate value="${build.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>

                                <h4 class="text-danger fw-bold mb-3">
                                    <fmt:formatNumber value="${build.totalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </h4>

                                <button class="btn btn-outline-secondary btn-sm w-100 mb-3" type="button" data-bs-toggle="collapse" data-bs-target="#details-${build.buildId}" aria-expanded="false">
                                    <i class="fas fa-list me-1"></i> Xem chi tiết linh kiện
                                </button>

                                <div class="collapse" id="details-${build.buildId}">
                                    <c:if test="${empty build.details}"><p class="text-muted small">Cấu hình chưa có linh kiện.</p></c:if>
                                    <ul class="list-group list-group-flush mb-3">
                                        <c:forEach var="detail" items="${build.details}">
                                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                <div>
                                                    <span class="fw-semibold"><c:out value="${detail.partName}"/></span>
                                                    <small class="text-muted">x${detail.quantity}</small>
                                                </div>
                                                <span class="text-danger fw-bold">
                                                    <fmt:formatNumber value="${detail.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>

                            <div class="card-footer bg-white border-top-0 pb-3 d-flex flex-wrap gap-2 justify-content-end">
                                <a href="${pageContext.request.contextPath}/build-pc?id=${build.buildId}" class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-eye me-1"></i> Xem / sửa
                                </a>

                                <c:if test="${build.status != 'SAVED'}">
                                    <!-- UC 16: Lưu cấu hình (nháp -> đã lưu) -->
                                    <button type="button" class="btn btn-success btn-sm js-name-modal" data-bs-toggle="modal" data-bs-target="#nameModal"
                                            data-mode="save" data-id="${build.buildId}" data-name="<c:out value='${build.buildName}'/>">
                                        <i class="fas fa-save me-1"></i> Lưu cấu hình
                                    </button>
                                </c:if>

                                <c:if test="${build.status == 'SAVED'}">
                                    <!-- UC 17: Chia sẻ -->
                                    <button type="button" class="btn btn-outline-secondary btn-sm js-share-btn" data-bs-toggle="modal" data-bs-target="#shareModal"
                                            data-id="${build.buildId}" data-token="${build.shareToken}">
                                        <i class="fas fa-share-alt me-1"></i> Chia sẻ
                                    </button>
                                    <button type="button" class="btn btn-outline-secondary btn-sm js-name-modal" data-bs-toggle="modal" data-bs-target="#nameModal"
                                            data-mode="rename" data-id="${build.buildId}" data-name="<c:out value='${build.buildName}'/>" title="Đổi tên">
                                        <i class="fas fa-pen"></i>
                                    </button>
                                    <!-- UC 300: đánh giá cấu hình -->
                                    <button type="button" class="btn btn-outline-warning btn-sm js-review-btn" data-bs-toggle="modal" data-bs-target="#reviewModal"
                                            data-id="${build.buildId}" data-rating="${build.reviewRating}" data-comment="<c:out value='${build.reviewComment}'/>">
                                        <i class="fas fa-star me-1"></i> <c:choose><c:when test="${empty build.reviewRating}">Đánh giá</c:when><c:otherwise>${build.reviewRating}/5</c:otherwise></c:choose>
                                    </button>
                                </c:if>

                                <form action="${pageContext.request.contextPath}/user/pc-build-history" method="POST" class="d-inline">
                                    <input type="hidden" name="action" value="duplicate">
                                    <input type="hidden" name="buildId" value="${build.buildId}">
                                    <button type="submit" class="btn btn-outline-secondary btn-sm" title="Sao chép cấu hình"><i class="far fa-copy"></i></button>
                                </form>
                                <form action="${pageContext.request.contextPath}/user/pc-build-history" method="POST" class="d-inline"
                                      onsubmit="return confirm('Xóa cấu hình này? Link chia sẻ của nó cũng sẽ không còn dùng được.');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="buildId" value="${build.buildId}">
                                    <button type="submit" class="btn btn-outline-danger btn-sm" title="Xóa"><i class="fas fa-trash"></i></button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- ===== Modal Lưu / Đổi tên ===== -->
<div class="modal fade" id="nameModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" action="${pageContext.request.contextPath}/user/pc-build-history" method="POST">
            <input type="hidden" name="action" id="nm_action">
            <input type="hidden" name="buildId" id="nm_id">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="nm_title"></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <label class="form-label">Tên cấu hình</label>
                <input type="text" name="buildName" id="nm_name" class="form-control" required maxlength="150" placeholder="VD: PC Gaming 20 triệu">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Xác nhận</button>
            </div>
        </form>
    </div>
</div>

<!-- ===== Modal Chia sẻ ===== -->
<!-- UC 300: Đánh giá cấu hình -->
<div class="modal fade" id="reviewModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" action="${pageContext.request.contextPath}/review" method="POST">
            <input type="hidden" name="action" value="reviewBuild">
            <input type="hidden" name="buildId" id="rv_id">
            <div class="modal-header">
                <h5 class="modal-title fw-bold">Đánh giá cấu hình</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <label class="form-label fw-semibold">Số sao</label>
                <select name="rating" id="rv_rating" class="form-select mb-3">
                    <option value="5">5 - Rất hài lòng</option><option value="4">4 - Hài lòng</option>
                    <option value="3">3 - Bình thường</option><option value="2">2 - Chưa tốt</option><option value="1">1 - Rất tệ</option>
                </select>
                <label class="form-label fw-semibold">Nhận xét</label>
                <textarea name="comment" id="rv_comment" class="form-control" rows="3" maxlength="2000"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
            </div>
        </form>
    </div>
</div>
<script>
    document.querySelectorAll('.js-review-btn').forEach(function (b) {
        b.addEventListener('click', function () {
            document.getElementById('rv_id').value = b.dataset.id;
            document.getElementById('rv_rating').value = b.dataset.rating || '5';
            document.getElementById('rv_comment').value = b.dataset.comment || '';
        });
    });
</script>

<div class="modal fade" id="shareModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="fas fa-share-alt me-2"></i>Chia sẻ cấu hình</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="text-muted small">Bất kỳ ai có link đều <b>xem</b> được cấu hình này (không sửa được và không thấy thông tin liên hệ của bạn).</p>
                <div class="input-group mb-2">
                    <input type="text" id="sh_link" class="form-control" readonly>
                    <button type="button" class="btn btn-primary" id="sh_copy"><i class="far fa-copy me-1"></i> Sao chép</button>
                </div>
                <div id="sh_copied" class="text-success small d-none"><i class="fas fa-check me-1"></i>Đã sao chép link!</div>
            </div>
            <div class="modal-footer justify-content-between">
                <form action="${pageContext.request.contextPath}/user/pc-build-history" method="POST"
                      onsubmit="return confirm('Tạo link mới? Link cũ sẽ không còn dùng được.');">
                    <input type="hidden" name="action" value="regenerate">
                    <input type="hidden" name="buildId" id="sh_id">
                    <button type="submit" class="btn btn-outline-danger btn-sm"><i class="fas fa-sync-alt me-1"></i> Tạo link mới (thu hồi link cũ)</button>
                </form>
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {
        var base = window.location.origin + "${pageContext.request.contextPath}/build-share?token=";

        // Modal lưu / đổi tên
        document.getElementById('nameModal').addEventListener('show.bs.modal', function (ev) {
            var b = ev.relatedTarget; if (!b) return;
            var save = b.dataset.mode === 'save';
            document.getElementById('nm_action').value = save ? 'save' : 'rename';
            document.getElementById('nm_title').textContent = save ? 'Lưu cấu hình PC' : 'Đổi tên cấu hình';
            document.getElementById('nm_id').value = b.dataset.id;
            document.getElementById('nm_name').value = b.dataset.name;
        });

        // Modal chia sẻ
        var shareModalEl = document.getElementById('shareModal');
        shareModalEl.addEventListener('show.bs.modal', function (ev) {
            var b = ev.relatedTarget; if (!b) return;
            document.getElementById('sh_id').value = b.dataset.id;
            document.getElementById('sh_link').value = base + b.dataset.token;
            document.getElementById('sh_copied').classList.add('d-none');
        });
        document.getElementById('sh_copy').addEventListener('click', function () {
            var input = document.getElementById('sh_link');
            var done = function () { document.getElementById('sh_copied').classList.remove('d-none'); };
            if (navigator.clipboard && window.isSecureContext) {
                navigator.clipboard.writeText(input.value).then(done);
            } else { input.select(); document.execCommand('copy'); done(); }
        });

        // Sau khi tạo link mới: tự mở lại hộp chia sẻ của cấu hình đó
        var m = window.location.search.match(/[?&]share=(\d+)/);
        if (m) {
            var btn = document.querySelector('.js-share-btn[data-id="' + m[1] + '"]');
            if (btn) btn.click();
            history.replaceState(null, '', window.location.pathname);
        }
    })();
</script>

<jsp:include page="/views/components/footer.jsp" />
