<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<style>
    .avatar-wrapper {
        position: relative;
        display: inline-block;
        cursor: pointer;
    }
    .avatar-wrapper:hover::after {
        content: "Thay đổi";
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        background: rgba(0, 0, 0, 0.6);
        color: white;
        font-size: 12px;
        padding: 4px 0;
        border-radius: 0 0 50% 50%;
        text-align: center;
    }
</style>

<jsp:include page="/views/components/sidebar.jsp" />
            
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

            <div class="row">
                <!-- Cột Đổi Ảnh Đại Diện -->
                <div class="col-md-4 mb-4">
                    <div class="card shadow-sm border-0 rounded-4 p-4 text-center h-100">
                        <h5 class="fw-bold mb-3">Ảnh đại diện</h5>
                        
                        <form action="${pageContext.request.contextPath}/user" method="POST" enctype="multipart/form-data" id="avatarForm">
                            <input type="hidden" name="action" value="updateAvatar">
                            
                            <label for="avatarInput" class="avatar-wrapper mb-3">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.loggedUser.avatarUrl}">
                                        <img src="${sessionScope.loggedUser.avatarUrl}" alt="Avatar" class="rounded-circle" style="width: 150px; height: 150px; object-fit: cover; border: 3px solid #ff4757;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center mx-auto" style="width: 150px; height: 150px; font-size: 60px;">
                                            ${sessionScope.loggedUser.fullName.substring(0, 1).toUpperCase()}
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </label>
                            
                            <input type="file" id="avatarInput" name="avatar" class="d-none" accept="image/*" onchange="document.getElementById('avatarForm').submit();">
                        </form>
                        <small class="text-muted">Ấn vào ảnh để thay đổi</small>
                    </div>
                </div>

                <!-- Cột Cập Nhật Thông Tin -->
                <div class="col-md-8">
                    <div class="card shadow-sm border-0 rounded-4 h-100">
                        <div class="card-header bg-white py-3 border-bottom">
                            <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-user-edit text-danger me-2"></i>Thông tin cá nhân</h5>
                        </div>
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/user" method="POST">
                                <input type="hidden" name="action" value="updateProfile">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Họ và tên</label>
                                        <input type="text" name="fullName" class="form-control" value="${sessionScope.loggedUser.fullName}" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Số điện thoại</label>
                                        <input type="text" name="phone" class="form-control" value="${sessionScope.loggedUser.phoneNumber}" pattern="0[0-9]{9}" maxlength="10" oninput="this.value = this.value.replace(/[^0-9]/g, '')">
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
            


<jsp:include page="/views/components/footer.jsp" />