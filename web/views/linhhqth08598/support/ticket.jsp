<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<jsp:include page="/views/components/sidebar.jsp" />
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-headset text-primary me-2"></i>Hỗ trợ khách hàng</h3>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Về trang chủ
        </a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="row">
        <!-- Cột trái: Form gửi yêu cầu -->
        <div class="col-lg-5 mb-4">
            <div class="card shadow-sm border-0 rounded-4 mb-4">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-3"><i class="fas fa-paper-plane text-primary me-2"></i>Tạo yêu cầu mới</h5>
                    <form action="${pageContext.request.contextPath}/user/support" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Loại yêu cầu</label>
                            <select name="type" class="form-select" required>
                                <option value="TECH_SUPPORT">Hỗ trợ kỹ thuật</option>
                                <option value="PART_QUESTION">Hỏi về linh kiện</option>
                                <option value="BUILD_CONSULTING">Tư vấn Build PC</option>
                                <option value="UPGRADE_CONSULTING">Tư vấn nâng cấp</option>
                                <option value="COMPLAINT">Khiếu nại</option>
                                <option value="PRODUCT_REPORT">Báo cáo sản phẩm lỗi</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Tiêu đề</label>
                            <input type="text" name="subject" class="form-control" required placeholder="VD: Máy không lên nguồn...">
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-semibold">Nội dung chi tiết</label>
                            <textarea name="content" class="form-control" rows="4" required placeholder="Mô tả chi tiết vấn đề bạn gặp phải..."></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Gửi yêu cầu</button>
                    </form>
                </div>
            </div>

            <!-- Thông tin liên hệ (gộp từ trang Liên hệ cũ) -->
            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-3"><i class="fas fa-address-book text-danger me-2"></i>Thông tin liên hệ</h5>
                    <p class="text-muted mb-3" style="font-size: 13px;">Cần tư vấn nhanh? Gọi hotline <span class="fw-bold text-danger">0123 456 789</span> hoặc gửi yêu cầu ở trên, chúng tôi sẽ phản hồi sớm nhất.</p>

                    <div class="contact-block">
                        <h6>Công ty TNHH GearZone Việt Nam</h6>
                        <p class="text-secondary"><i class="fas fa-map-marker-alt me-2 text-danger"></i>Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội</p>
                        <p class="text-secondary"><i class="fas fa-map-marker-alt me-2 text-danger"></i>Số 2 Trần Phú, Hà Đông, Hà Nội</p>
                    </div>
                    <div class="contact-block">
                        <h6>Giờ mở cửa</h6>
                        <p><span class="text-muted">Tư vấn &amp; bán hàng:</span> <span class="fw-semibold">8h30 – 21h30</span></p>
                        <p><span class="text-muted">Hỗ trợ kỹ thuật:</span> <span class="fw-semibold">8h30 – 12h, 13h30 – 17h30</span></p>
                    </div>
                    <div class="contact-block">
                        <h6>Hotline</h6>
                        <p class="text-danger fw-bold fs-5 mb-0"><i class="fas fa-phone-alt me-2"></i>0123 456 789</p>
                    </div>
                    <div class="contact-block">
                        <h6>Mạng xã hội</h6>
                        <div class="d-flex gap-2">
                            <a href="#" class="btn btn-primary btn-sm rounded-circle p-0" style="width: 32px;" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
                            <a href="#" class="btn btn-danger btn-sm rounded-circle p-0" style="width: 32px;" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
                            <a href="#" class="btn btn-info btn-sm rounded-circle p-0 text-white" style="width: 32px;" aria-label="Chat"><i class="fas fa-comment-dots"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Cột phải: Danh sách ticket đã gửi -->
        <div class="col-lg-7">
            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-4">Lịch sử yêu cầu</h5>
                    
                    <c:choose>
                        <c:when test="${empty tickets}">
                            <p class="text-muted fst-italic text-center py-4">Bạn chưa gửi yêu cầu hỗ trợ nào.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="accordion" id="ticketAccordion">
                                <c:forEach var="t" items="${tickets}" varStatus="status">
                                    <div class="accordion-item mb-2 border rounded-3 overflow-hidden">
                                        <h2 class="accordion-header" id="heading-${t.ticketId}">
                                            <button class="accordion-button collapsed fw-bold text-dark" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${t.ticketId}">
                                                <div class="d-flex justify-content-between w-100 me-3">
                                                    <span><c:out value='${t.subject}'/></span>
                                                    <span class="badge 
                                                        <c:choose>
                                                            <c:when test="${t.status == 'OPEN'}">bg-warning text-dark</c:when>
                                                            <c:when test="${t.status == 'RESOLVED'}">bg-success</c:when>
                                                            <c:otherwise>bg-secondary</c:otherwise>
                                                        </c:choose>
                                                    ">${t.status}</span>
                                                </div>
                                            </button>
                                        </h2>
                                        <div id="collapse-${t.ticketId}" class="accordion-collapse collapse" data-bs-parent="#ticketAccordion">
                                            <div class="accordion-body bg-light">
                                                <p class="mb-2"><strong>Mã Ticket:</strong> #${t.ticketCode}</p>
                                                <p class="mb-2"><strong>Loại:</strong> ${t.ticketType}</p>
                                                <p class="mb-3"><strong>Nội dung:</strong> <c:out value='${t.content}'/></p>
                                                <small class="text-muted fst-italic">
                                                    <i class="far fa-clock me-1"></i> Ngày gửi: <fmt:formatDate value="${t.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>


<jsp:include page="/views/components/footer.jsp" />