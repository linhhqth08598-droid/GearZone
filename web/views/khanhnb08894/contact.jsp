<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container my-5">
    <div class="row">
        <!-- Cột trái: Thông tin liên hệ -->
        <div class="col-md-5 mb-4">
            <h3 class="fw-bold mb-4">Thông tin liên hệ</h3>
            <p class="text-muted mb-4">Gọi hotline <span class="fw-bold text-danger">0123 456 789</span> để được tư vấn nhanh nhất.</p>
            
            <div class="border-bottom py-3">
                <h6 class="fw-bold mb-2">Công ty TNHH GearZone Việt Nam</h6>
                <p class="text-secondary mb-1"><i class="fas fa-map-marker-alt me-2 text-danger"></i> Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội</p>
                <p class="text-secondary mb-1"><i class="fas fa-map-marker-alt me-2 text-danger"></i> Số 2 Trần Phú, Hà Đông, Hà Nội</p>
            </div>
            <div class="border-bottom py-3">
                <h6 class="fw-bold mb-2">Giờ mở cửa</h6>
                <p class="mb-1"><span class="text-muted">Tư vấn & bán hàng:</span> <span class="fw-semibold">8h30 – 21h30</span></p>
                <p class="mb-1"><span class="text-muted">Hỗ trợ kỹ thuật:</span> <span class="fw-semibold">8h30 – 12h, 13h30 – 17h30</span></p>
            </div>
            <div class="border-bottom py-3">
                <h6 class="fw-bold mb-2">Hotline</h6>
                <p class="text-danger fw-bold fs-5 mb-1">0123 456 789</p>
            </div>
            <div class="py-3">
                <h6 class="fw-bold mb-2">Mạng xã hội</h6>
                <div class="d-flex gap-2 mt-2">
                    <a href="#" class="btn btn-primary btn-sm rounded-circle"><i class="fab fa-facebook-f"></i></a>
                    <a href="#" class="btn btn-danger btn-sm rounded-circle"><i class="fab fa-youtube"></i></a>
                    <a href="#" class="btn btn-info btn-sm rounded-circle text-white"><i class="fas fa-comment-dots"></i></a>
                </div>
            </div>
        </div>

        <!-- Cột phải: Form gửi yêu cầu -->
        <div class="col-md-7">
            <div class="bg-light p-4 rounded-4 shadow-sm">
                <h4 class="fw-bold mb-3">Gửi yêu cầu tư vấn</h4>
                <p class="text-muted mb-4">Các ô có dấu * là bắt buộc.</p>
                
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="message" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <form action="${pageContext.request.contextPath}/contact" method="POST">
                    <input type="hidden" name="action" value="contact">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                        <input type="text" name="fullName" class="form-control" required placeholder="Nguyễn Văn A">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                        <input type="text" name="phone" class="form-control" required placeholder="09xx xxx xxx">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Email (không bắt buộc)</label>
                        <input type="email" name="email" class="form-control" placeholder="email@vidu.com">
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold">Nội dung cần tư vấn <span class="text-danger">*</span></label>
                        <textarea name="content" class="form-control" rows="4" required placeholder="Ví dụ: Cần laptop đồ họa tầm 25 triệu..."></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-3 fw-bold" style="background-color: #3b82f6;">Gửi yêu cầu</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />