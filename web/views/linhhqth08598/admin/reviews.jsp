<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />
<style>
    /* CSS cắt chữ 2 dòng có dấu ... */
    .text-clamp-2 {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: normal;
        max-height: 3em;
        line-height: 1.5em;
    }
    /* Cho phép tên sản phẩm xuống dòng thay vì cắt cụt */
    .product-name {
        white-space: normal; 
        word-wrap: break-word; 
        max-width: 200px;
    }
</style>

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-star me-2"></i>Quản lý Đánh giá</h1>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> ${sessionScope.message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Sản phẩm</th>
                        <th>Khách hàng</th>
                        <th>Đánh giá</th>
                        <th>Nhận xét</th>
                        <th>Ảnh</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${reviews}">
                        <tr>
                            <td>#${r.reviewId}</td>
                            <!-- SỬA: Dùng class product-name thay vì text-truncate -->
                            <td class="fw-bold product-name">${r.partName}</td>
                            <td>${r.userName}</td>
                            <td class="text-warning">
                                <!-- Hiển thị số sao (hỗ trợ nửa sao) -->
                                <c:set var="rating" value="${r.qualityRating}" />
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${rating >= i}"><i class="fas fa-star"></i></c:when>
                                        <c:when test="${rating >= i - 0.5}"><i class="fas fa-star-half-alt"></i></c:when>
                                        <c:otherwise><i class="far fa-star"></i></c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </td>
                            <!-- SỬA: Bỏ title="${r.comment}" để không hiện mảng trắng -->
                            <td class="text-clamp-2" style="max-width: 250px;">
                                ${r.comment}
                            </td>
                            <td>
                                <c:if test="${not empty r.imageUrl}">
                                    <img src="${r.imageUrl}" alt="Review Image" class="img-thumbnail" style="max-height: 50px; cursor: pointer;" onclick="window.open(this.src)">
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.status == 'VISIBLE'}"><span class="badge bg-success">Hiển thị</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-2 justify-content-center">
                                    <!-- NÚT XEM CHI TIẾT (Mở Modal) -->
                                    <button type="button" class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#viewReviewModal-${r.reviewId}">
                                        <i class="fas fa-eye"></i> Xem
                                    </button>

                                    <!-- FORM ẨN/HIỆN -->
                                    <form action="${pageContext.request.contextPath}/admin/reviews" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="toggleStatus">
                                        <input type="hidden" name="reviewId" value="${r.reviewId}">
                                        <input type="hidden" name="status" value="${r.status}">
                                        <c:choose>
                                            <c:when test="${r.status == 'VISIBLE'}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn đánh giá này?');">
                                                    <i class="fas fa-eye-slash"></i> Ẩn
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" class="btn btn-sm btn-outline-success" onclick="return confirm('Bạn có chắc muốn hiển thị lại đánh giá này?');">
                                                    <i class="fas fa-eye"></i> Hiện
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- MODAL XEM CHI TIẾT ĐÁNH GIÁ -->
                    <div class="modal fade" id="viewReviewModal-${r.reviewId}" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered modal-lg">
                            <div class="modal-content border-0 shadow-lg rounded-4">
                                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                    <h5 class="modal-title fw-bold text-primary">
                                        <i class="fas fa-comment-dots me-2"></i>Chi tiết Đánh giá #${r.reviewId}
                                    </h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body p-4">
                                    <div class="row">
                                        <!-- Cột trái: Thông tin sản phẩm + khách hàng -->
                                        <div class="col-md-5 border-end">
                                            <p class="mb-2"><strong>Sản phẩm:</strong> <br> <span class="text-primary fw-bold">${r.partName}</span></p>
                                            <p class="mb-2"><strong>Khách hàng:</strong> <br> ${r.userName}</p>
                                            <p class="mb-2"><strong>Ngày đánh giá:</strong> <br> <fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                                            <p class="mb-2"><strong>Trạng thái:</strong> 
                                                <c:choose>
                                                    <c:when test="${r.status == 'VISIBLE'}"><span class="badge bg-success">Hiển thị</span></c:when>
                                                    <c:otherwise><span class="badge bg-secondary">Đã ẩn</span></c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>

                                        <!-- Cột phải: Số sao + Nhận xét + Ảnh -->
                                        <div class="col-md-7">
                                            <p class="mb-2"><strong>Đánh giá chất lượng:</strong></p>
                                            <div class="text-warning fs-5 mb-3">
                                                <c:set var="rating" value="${r.qualityRating}" />
                                                <c:forEach begin="1" end="5" var="i">
                                                    <c:choose>
                                                        <c:when test="${rating >= i}"><i class="fas fa-star"></i></c:when>
                                                        <c:when test="${rating >= i - 0.5}"><i class="fas fa-star-half-alt"></i></c:when>
                                                        <c:otherwise><i class="far fa-star"></i></c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                                <span class="text-dark ms-2">(${r.qualityRating} sao)</span>
                                            </div>

                                            <p class="mb-2"><strong>Nhận xét:</strong></p>
                                            <div class="bg-light p-3 rounded border mb-3" style="white-space: pre-wrap;">${r.comment}</div>

                                            <c:if test="${not empty r.imageUrl}">
                                                <p class="mb-2"><strong>Ảnh đính kèm:</strong></p>
                                                <img src="${r.imageUrl}" alt="Review Image" class="img-fluid rounded border" style="max-height: 200px;">
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Đóng</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />