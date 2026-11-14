<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<!-- NỘI DUNG CHÍNH ĐẶT Ở ĐÂY (Không cần thẻ <main>) -->
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2 mb-0"><i class="fas fa-chart-line me-2"></i>Tổng quan Thống kê</h1>
    <div class="d-flex gap-3 mt-2 mt-md-0">
        <a href="${pageContext.request.contextPath}/admin/export-report?type=revenue" class="btn btn-success">
            <i class="fas fa-file-excel me-1"></i> Xuất Doanh Thu
        </a>
        <a href="${pageContext.request.contextPath}/admin/export-report?type=inventory" class="btn btn-warning text-white">
            <i class="fas fa-file-excel me-1"></i> Xuất Tồn Kho
        </a>
    </div>
</div>

<!-- Thẻ KPI -->
<div class="row mb-4">
    <div class="col-md-4">
        <div class="card shadow-sm border-0 rounded-4 bg-primary text-white h-100" style="background: linear-gradient(135deg, #3b82f6, #2563eb) !important;">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-2 fw-semibold" style="opacity: 0.8;">Tổng Doanh Thu</h6>
                        <h2 class="mb-0 fw-bold"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></h2>
                    </div>
                    <i class="fas fa-money-bill-wave fa-3x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm border-0 rounded-4 bg-success text-white h-100" style="background: linear-gradient(135deg, #10b981, #059669) !important;">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-2 fw-semibold" style="opacity: 0.8;">Tổng Đơn Hàng</h6>
                        <h2 class="mb-0 fw-bold">${totalOrders}</h2>
                    </div>
                    <i class="fas fa-shopping-cart fa-3x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm border-0 rounded-4 bg-warning text-dark h-100" style="background: linear-gradient(135deg, #f59e0b, #d97706) !important;">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-2 fw-semibold" style="opacity: 0.8;">Linh Kiện Đã Bán</h6>
                        <h2 class="mb-0 fw-bold">${totalPartsSold}</h2>
                    </div>
                    <i class="fas fa-microchip fa-3x" style="opacity: 0.5;"></i>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Biểu đồ -->
<div class="row">
    <!-- Biểu đồ tròn: Doanh thu theo danh mục -->
    <div class="col-md-6 mb-4">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-chart-pie me-2 text-primary"></i>Doanh thu theo Danh mục</h5>
            </div>
            <div class="card-body p-4">
                <canvas id="revenueChart"></canvas>
            </div>
        </div>
    </div>

    <!-- Biểu đồ cột: Top 5 linh kiện bán chạy -->
    <div class="col-md-6 mb-4">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-chart-bar me-2 text-success"></i>Top 5 Linh kiện Bán chạy</h5>
            </div>
            <div class="card-body p-4">
                <canvas id="topPartsChart"></canvas>
            </div>
        </div>
    </div>
</div>

<script>
    // 1. Vẽ biểu đồ tròn (Doanh thu)
    const catLabels = ${categoryLabels};
    const catData = ${categoryData};

    if (catLabels.length > 0) {
        new Chart(document.getElementById('revenueChart'), {
            type: 'doughnut',
            data: {
                labels: catLabels,
                datasets: [{
                        data: catData,
                        backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'],
                        borderWidth: 0
                    }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {position: 'bottom'}
                }
            }
        });
    } else {
        document.getElementById('revenueChart').parentElement.innerHTML = '<p class="text-center text-muted py-5">Chưa có dữ liệu doanh thu.</p>';
    }

    // 2. Vẽ biểu đồ cột (Top linh kiện)
    const partLabels = ${partLabels};
    const partData = ${partData};

    if (partLabels.length > 0) {
        new Chart(document.getElementById('topPartsChart'), {
            type: 'bar',
            data: {
                labels: partLabels,
                datasets: [{
                        label: 'Số lượng bán ra',
                        data: partData,
                        backgroundColor: '#10b981',
                        borderRadius: 5
                    }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {beginAtZero: true, ticks: {stepSize: 1}}
                }
            }
        });
    } else {
        document.getElementById('topPartsChart').parentElement.innerHTML = '<p class="text-center text-muted py-5">Chưa có dữ liệu linh kiện bán ra.</p>';
    }
</script>

<!-- BẢNG THỐNG KÊ CHI TIẾT -->
<div class="row mt-4 mb-4">
    <!-- Bảng Đơn Hàng Mới Nhất -->
    <div class="col-md-6 mb-4">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-clock me-2 text-primary"></i>Đơn Hàng Mới Nhất</h5>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-outline-primary">Xem tất cả</a>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive border-0">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Mã Đơn</th>
                                <th>Khách Hàng</th>
                                <th>Tổng Tiền</th>
                                <th>Ngày Đặt</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${recentOrders}">
                                <tr>
                                    <td class="fw-bold">#${order.orderId}</td>
                                    <td>${order.recipientName}</td>
                                    <td class="text-danger fw-semibold"><fmt:formatNumber value="${order.finalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                    <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentOrders}">
                                <tr><td colspan="4" class="text-center text-muted py-4">Chưa có đơn hàng nào.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Bảng Top Sản Phẩm Bán Chạy -->
    <div class="col-md-6 mb-4">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-bold text-dark"><i class="fas fa-fire me-2 text-danger"></i>Sản Phẩm Bán Chạy Nhất</h5>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-sm btn-outline-primary">Xem tất cả</a>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive border-0">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Tên Linh Kiện</th>
                                <th>Đã Bán</th>
                                <th>Doanh Thu</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="topPart" items="${topSellingPartsDetail}">
                                <tr>
                                    <td class="fw-bold text-truncate" style="max-width: 200px;" title="${topPart.partName}">${topPart.partName}</td>
                                    <td><span class="badge bg-success">${topPart.totalSold}</span></td>
                                    <td class="text-danger fw-semibold"><fmt:formatNumber value="${topPart.revenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty topSellingPartsDetail}">
                                <tr><td colspan="3" class="text-center text-muted py-4">Chưa có dữ liệu bán ra.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />