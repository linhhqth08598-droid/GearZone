<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chương Trình Khuyến Mãi - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        <div class="layout-right">
            
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #334155; padding-bottom: 10px;">
                <h2 class="section-title" style="margin: 0; border: none; padding: 0;">
                    <i class="fa-solid fa-tags" style="color: #ff4757;"></i> Chương Trình Khuyến Mãi
                </h2>
            </div>

            <!-- Nút Thêm Khuyến Mãi (Chỉ Admin) -->
            <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                <div style="margin-bottom: 20px; text-align: right;">
                    <button onclick="openPromoModal('add')" class="btn-submit" style="width: auto; display: inline-block;">
                        <i class="fa-solid fa-plus"></i> Thêm Khuyến Mãi Mới
                    </button>
                </div>
            </c:if>

            <div class="product-grid">
                <c:forEach items="${promotionList}" var="pr">
                    <div class="product-card" style="background: ${pr.isActive ? '#1a1d29' : '#1e1e24'}; border-color: ${pr.isActive ? '#3b82f6' : '#4a5568'};">
                        
                        <div style="text-align: center; margin-bottom: 15px;">
                            <i class="fa-solid fa-ticket" style="font-size: 50px; color: ${pr.isActive ? '#10b981' : '#64748b'};"></i>
                        </div>
                        
                        <h3 style="color: #fff; font-size: 18px; margin-bottom: 5px;">${pr.promotionName}</h3>
                        
                        <div style="background: #0f1117; padding: 8px; border-radius: 6px; border: 1px dashed #ff4757; margin-bottom: 15px;">
                            <span style="color: #ff4757; font-weight: bold; font-size: 16px; letter-spacing: 2px;">${pr.promoCode}</span>
                        </div>

                        <p style="color: #cbd5e1; font-size: 14px; margin-bottom: 8px; text-align: left;">
                            <i class="fa-solid fa-gift" style="width: 20px;"></i>
                            <c:choose>
                                <c:when test="${pr.discountPercent > 0}">Giảm ${pr.discountPercent}%</c:when>
                                <c:otherwise>Giảm <fmt:formatNumber value="${pr.discountAmount}" pattern="#,###"/>₫</c:otherwise>
                            </c:choose>
                        </p>
                        
                        <p style="color: #cbd5e1; font-size: 14px; margin-bottom: 8px; text-align: left;">
                            <i class="fa-solid fa-money-bill-wave" style="width: 20px;"></i> 
                            Đơn tối thiểu: <fmt:formatNumber value="${pr.minOrderValue}" pattern="#,###"/>₫
                        </p>
                        
                        <p style="color: #cbd5e1; font-size: 14px; margin-bottom: 15px; text-align: left;">
                            <i class="fa-solid fa-calendar-days" style="width: 20px;"></i> 
                            HSD: <fmt:formatDate value="${pr.endDate}" pattern="dd/MM/yyyy"/>
                        </p>

                        <!-- Tình trạng -->
                        <div style="margin-bottom: 15px;">
                            <c:choose>
                                <c:when test="${pr.isActive}">
                                    <span style="background: rgba(16, 185, 129, 0.2); color: #10b981; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: bold;">ĐANG ÁP DỤNG</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="background: rgba(100, 116, 139, 0.2); color: #94a3b8; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: bold;">HẾT HẠN</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Các nút (Chỉ Admin mới thấy) -->
                        <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                            <div style="display: flex; gap: 10px; margin-top: 15px; border-top: 1px solid #334155; padding-top: 15px;">
                                <button onclick="openPromoModal('edit', '${pr.promotionId}', '${pr.promoCode}', '${pr.promotionName}', '${pr.discountPercent}', '${pr.discountAmount}', '${pr.minOrderValue}', '${pr.startDate}', '${pr.endDate}', '${pr.usageLimit}', ${pr.isActive})" class="btn-view" style="flex: 1; background: #3b82f6; border: none; cursor: pointer;">
                                    <i class="fa-solid fa-pen"></i> Sửa
                                </button>
                                <button onclick="openPromoDeleteModal('${pr.promotionId}')" class="btn-view" style="flex: 1; background: #ef4444; border: none; cursor: pointer;">
                                    <i class="fa-solid fa-trash"></i> Xóa
                                </button>
                            </div>
                        </c:if>

                    </div>
                </c:forEach>
                
                <c:if test="${empty promotionList}">
                    <p style="color: #94a3b8; grid-column: 1 / -1; text-align: center; padding: 40px 0;">Hiện chưa có chương trình khuyến mãi nào.</p>
                </c:if>
            </div>
            
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />

    <!-- MODAL THÊM / SỬA KHUYẾN MÃI -->
    <div class="modal-overlay" id="promoModal">
        <div class="modal-content form-box" style="margin: 0; width: 550px;">
            <div class="modal-header">
                <h3 id="promoModalTitle">Thêm Khuyến Mãi Mới</h3>
                <i class="fa-solid fa-xmark close-btn" onclick="document.getElementById('promoModal').style.display='none'"></i>
            </div>
            
            <form action="${pageContext.request.contextPath}/admin/savePromotion" method="POST">
                <input type="hidden" name="actionType" id="promoActionType" value="add">
                <input type="hidden" name="promotionId" id="promoId" value="">

                <div class="form-group">
                    <label>Tên Chương Trình Khuyến Mãi</label>
                    <input type="text" name="promotionName" id="promoName" required>
                </div>
                
                <div class="form-group">
                    <label>Mã Giảm Giá (Code)</label>
                    <input type="text" name="promoCode" id="promoCode" style="text-transform: uppercase;" required>
                </div>

                <div style="display: flex; gap: 10px;">
                    <div class="form-group" style="flex: 1;">
                        <label>% Giảm giá</label>
                        <input type="number" step="0.1" name="discountPercent" id="promoPercent" min="0" max="100">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Hoặc Số tiền giảm (VND)</label>
                        <input type="number" name="discountAmount" id="promoAmount" min="0">
                    </div>
                </div>

                <div style="display: flex; gap: 10px;">
                    <div class="form-group" style="flex: 1;">
                        <label>Đơn tối thiểu (VND)</label>
                        <input type="number" name="minOrderValue" id="promoMinOrder" min="0">
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Giới hạn lượt dùng</label>
                        <input type="number" name="usageLimit" id="promoLimit" min="0">
                    </div>
                </div>

                <div style="display: flex; gap: 10px;">
                    <div class="form-group" style="flex: 1;">
                        <label>Ngày bắt đầu</label>
                        <input type="date" name="startDate" id="promoStart" required>
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Ngày kết thúc</label>
                        <input type="date" name="endDate" id="promoEnd" required>
                    </div>
                </div>

                <div class="form-group" style="display: flex; align-items: center; gap: 10px;">
                    <input type="checkbox" name="isActive" id="promoActive" value="true" style="width: auto; transform: scale(1.5);">
                    <label style="margin: 0; color: #10b981; font-weight: bold;">Cho phép áp dụng (Active)</label>
                </div>

                <button type="submit" class="btn-submit" style="margin-top: 10px;">Lưu Khuyến Mãi</button>
            </form>
        </div>
    </div>

    <!-- MODAL XÓA KHUYẾN MÃI -->
    <div class="modal-overlay" id="promoDeleteModal">
        <div class="modal-content form-box" style="margin: 0; width: 350px; text-align: center;">
            <i class="fa-solid fa-circle-exclamation" style="font-size: 50px; color: #ff4757; margin-bottom: 15px;"></i>
            <h3 style="color: #fff; margin-bottom: 10px;">Xác nhận xóa mã này?</h3>
            <form action="${pageContext.request.contextPath}/admin/deletePromotion" method="POST" style="display: flex; gap: 10px; margin-top: 20px;">
                <input type="hidden" name="promotionId" id="promoDeleteId" value="">
                <button type="button" class="btn-view" style="flex: 1; cursor: pointer;" onclick="document.getElementById('promoDeleteModal').style.display='none'">Hủy</button>
                <button type="submit" class="btn-submit" style="flex: 1; background: #ff4757; border: none;">Xóa ngay</button>
            </form>
        </div>
    </div>

    <script>
        function openPromoDeleteModal(id) {
            document.getElementById('promoDeleteId').value = id;
            document.getElementById('promoDeleteModal').style.display = 'flex';
        }

        function openPromoModal(type, id = '', code = '', name = '', percent = '', amount = '', minOrder = '', start = '', end = '', limit = '', active = true) {
            const modal = document.getElementById('promoModal');
            document.getElementById('promoActionType').value = type;
            
            if (type === 'add') {
                document.getElementById('promoModalTitle').innerText = 'Thêm Khuyến Mãi Mới';
                document.getElementById('promoId').value = '';
                document.getElementById('promoName').value = '';
                document.getElementById('promoCode').value = '';
                document.getElementById('promoPercent').value = '';
                document.getElementById('promoAmount').value = '';
                document.getElementById('promoMinOrder').value = '';
                document.getElementById('promoStart').value = '';
                document.getElementById('promoEnd').value = '';
                document.getElementById('promoLimit').value = '';
                document.getElementById('promoActive').checked = true;
            } else {
                document.getElementById('promoModalTitle').innerText = 'Sửa Khuyến Mãi';
                document.getElementById('promoId').value = id;
                document.getElementById('promoName').value = name;
                document.getElementById('promoCode').value = code;
                document.getElementById('promoPercent').value = percent;
                document.getElementById('promoAmount').value = amount;
                document.getElementById('promoMinOrder').value = minOrder;
                
                // Format date yyyy-MM-dd để gán vào input date
                if(start) document.getElementById('promoStart').value = start.split(' ')[0];
                if(end) document.getElementById('promoEnd').value = end.split(' ')[0];
                
                document.getElementById('promoLimit').value = limit;
                document.getElementById('promoActive').checked = active;
            }
            modal.style.display = 'flex';
        }
    </script>

    <!-- THÔNG BÁO POPUP -->
    <c:if test="${not empty sessionScope.errorMsg}">
        <script>alert('${sessionScope.errorMsg}');</script>
        <c:remove var="errorMsg" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.successMsg}">
        <script>alert('${sessionScope.successMsg}');</script>
        <c:remove var="successMsg" scope="session" />
    </c:if>

</body>
</html>