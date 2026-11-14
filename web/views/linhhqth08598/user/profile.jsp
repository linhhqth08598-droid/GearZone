<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5" style="min-height: 60vh;">
    <div class="row">
        <!-- Cột trái: Sidebar của User -->
        <div class="col-md-4 col-lg-3">
            <jsp:include page="/views/components/sidebar.jsp" />
        </div>

        <!-- Cột phải: Nội dung hồ sơ -->
        <div class="col-md-8 col-lg-9">
            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-header bg-white py-3 border-bottom">
                    <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-user-edit text-danger me-2"></i>Hồ sơ cá nhân</h5>
                </div>
                <div class="card-body p-4">
                    
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

                    <form action="${pageContext.request.contextPath}/user/profile" method="POST">
                        <input type="hidden" name="action" value="updateProfile">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Họ và tên</label>
                                <input type="text" name="fullName" class="form-control" value="${sessionScope.loggedUser.fullName}" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Số điện thoại</label>
                                <input type="text" name="phone" class="form-control" value="${sessionScope.loggedUser.phoneNumber}">
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Email (Không thể thay đổi)</label>
                            <input type="email" class="form-control bg-light" value="${sessionScope.loggedUser.email}" readonly>
                        </div>
                        
                        <button type="submit" class="btn btn-danger px-4 py-2">Lưu thay đổi</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />