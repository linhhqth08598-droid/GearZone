<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-microchip text-primary me-2"></i>Xây dựng cấu hình PC</h3>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/user/pc-build-history" class="btn btn-outline-secondary"><i class="fas fa-history me-1"></i> Lịch sử cấu hình</a>
            <form action="${pageContext.request.contextPath}/build-pc" method="POST" class="m-0">
                <input type="hidden" name="action" value="newBuild">
                <button type="submit" class="btn btn-outline-primary"><i class="fas fa-plus me-1"></i> Làm cấu hình mới</button>
            </form>
        </div>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="row g-3">
        <!-- Cột trái: chọn loại + danh sách linh kiện -->
        <div class="col-lg-7">
            <div class="card border-0 shadow-sm rounded-4">
                <div class="card-body p-3">
                    <ul class="nav nav-pills mb-3 flex-wrap gap-1">
                        <c:forEach var="s" items="${slots}">
                            <li class="nav-item">
                                <a class="nav-link py-1 ${s.categoryId == currentCat ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/build-pc?buildId=${buildId}&cat=${s.categoryId}">
                                    <c:out value="${s.categoryName}"/><c:if test="${s.required}"> <span class="text-danger">*</span></c:if>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                    <c:if test="${empty slots}">
                        <p class="text-muted mb-0">Chưa có danh mục linh kiện nào. Hãy nhờ quản trị viên thêm danh mục và linh kiện.</p>
                    </c:if>

                    <c:if test="${not empty slots}">
                        <form method="GET" action="${pageContext.request.contextPath}/build-pc" class="input-group mb-3">
                            <input type="hidden" name="buildId" value="${buildId}">
                            <input type="hidden" name="cat" value="${currentCat}">
                            <input type="text" name="q" class="form-control" placeholder="Tìm theo tên linh kiện..." value="<c:out value='${q}'/>">
                            <button class="btn btn-outline-primary" type="submit"><i class="fas fa-search"></i></button>
                        </form>

                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead class="table-light"><tr><th>Linh kiện</th><th>Hãng</th><th class="text-end">Giá</th><th class="text-center">Còn</th><th style="width:150px"></th></tr></thead>
                                <tbody>
                                    <c:forEach var="o" items="${options}">
                                        <tr>
                                            <td><c:out value="${o.partName}"/></td>
                                            <td><c:out value="${o.brandName}"/></td>
                                            <td class="text-end"><fmt:formatNumber value="${o.price}" type="number" maxFractionDigits="0"/> ₫</td>
                                            <td class="text-center ${o.available <= 0 ? 'text-danger fw-bold' : ''}">${o.available <= 0 ? 'Hết' : o.available}</td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/build-pc" method="POST" class="d-flex gap-1 m-0">
                                                    <input type="hidden" name="action" value="setPart">
                                                    <input type="hidden" name="buildId" value="${buildId}">
                                                    <input type="hidden" name="cat" value="${currentCat}">
                                                    <input type="hidden" name="partId" value="${o.partId}">
                                                    <input type="number" name="quantity" value="1" min="1" max="8" class="form-control form-control-sm" style="width:60px">
                                                    <button type="submit" class="btn btn-sm btn-primary">Chọn</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty options}">
                                        <tr><td colspan="5" class="text-center text-muted py-4">Không có linh kiện nào trong mục này.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Cột phải: cấu hình đang chọn -->
        <div class="col-lg-5">
            <div class="card border-0 shadow-sm rounded-4">
                <div class="card-body p-3">
                    <h5 class="fw-bold mb-3">Cấu hình của bạn</h5>
                    <c:forEach var="l" items="${lines}">
                        <div class="d-flex justify-content-between align-items-start border-bottom py-2">
                            <div>
                                <div class="small text-muted"><c:out value="${l.categoryName}"/></div>
                                <div class="fw-semibold"><c:out value="${l.partName}"/> <c:if test="${l.quantity > 1}"><span class="text-muted">x${l.quantity}</span></c:if></div>
                                <div class="small"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</div>
                                <c:if test="${l.shortage}"><div class="small text-danger"><i class="fas fa-box-open me-1"></i>Kho chỉ còn ${l.available}</div></c:if>
                            </div>
                            <form action="${pageContext.request.contextPath}/build-pc" method="POST" class="m-0">
                                <input type="hidden" name="action" value="removePart">
                                <input type="hidden" name="buildId" value="${buildId}">
                                <input type="hidden" name="cat" value="${currentCat}">
                                <input type="hidden" name="detailId" value="${l.detailId}">
                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa"><i class="fas fa-times"></i></button>
                            </form>
                        </div>
                    </c:forEach>
                    <c:if test="${empty lines}"><p class="text-muted my-3">Chưa chọn linh kiện nào.</p></c:if>

                    <div class="d-flex justify-content-between fs-5 fw-bold mt-3">
                        <span>Tổng cộng</span>
                        <span class="text-danger"><fmt:formatNumber value="${total}" type="number" maxFractionDigits="0"/> ₫</span>
                    </div>

                    <c:forEach var="e" items="${compatErrors}">
                        <div class="alert alert-danger small mt-3 mb-0 py-2"><i class="fas fa-times-circle me-1"></i> <c:out value="${e}"/></div>
                    </c:forEach>
                    <c:forEach var="w" items="${compatWarnings}">
                        <div class="alert alert-warning small mt-3 mb-0 py-2"><i class="fas fa-exclamation-triangle me-1"></i> <c:out value="${w}"/></div>
                    </c:forEach>
                    <c:if test="${not empty lines and empty compatErrors}">
                        <div class="small text-success mt-3"><i class="fas fa-check-circle me-1"></i> Các linh kiện đã chọn tương thích với nhau.</div>
                    </c:if>

                    <c:if test="${not empty missing}">
                        <div class="alert alert-warning small mt-3 mb-0">
                            <i class="fas fa-exclamation-triangle me-1"></i> Còn thiếu:
                            <c:forEach var="m" items="${missing}" varStatus="st"><c:out value="${m}"/><c:if test="${!st.last}">, </c:if></c:forEach>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/build-pc" method="POST" class="mt-3">
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="buildId" value="${buildId}">
                        <input type="hidden" name="cat" value="${currentCat}">
                        <label class="form-label fw-semibold small">Tên cấu hình</label>
                        <div class="input-group">
                            <input type="text" name="buildName" class="form-control" maxlength="150" required placeholder="VD: PC Gaming 20 triệu" value="<c:out value='${buildName}'/>">
                            <button type="submit" class="btn btn-success" ${empty lines ? 'disabled' : ''}><i class="fas fa-save me-1"></i> Lưu</button>
                        </div>
                        <div class="form-text">Sau khi lưu bạn có thể lấy link chia sẻ trong Lịch sử cấu hình.</div>
                    </form>

                    <form action="${pageContext.request.contextPath}/build-pc" method="POST" class="mt-2">
                        <input type="hidden" name="action" value="addToCart">
                        <input type="hidden" name="buildId" value="${buildId}">
                        <button type="submit" class="btn btn-outline-primary w-100" ${empty lines ? 'disabled' : ''}><i class="fas fa-cart-plus me-1"></i> Thêm cả cấu hình vào giỏ</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
