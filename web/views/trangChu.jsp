<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Trang Chủ - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=1.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>

        <!-- NHÚNG HEADER & SIDEBAR -->
        <jsp:include page="includes/header.jsp" />
        <div class="container main-layout">
            <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
            <div class="layout-right">

                <!-- Banner -->
                <div class="slider-placeholder">
                    <div class="slider-content">
                        <h2>KHUYẾN MÃI BACK TO SCHOOL</h2>
                        <p>Giảm đến 50% cho sinh viên FPT Polytechnic</p>
                    </div>
                </div>

                <h2 class="section-title">Sản Phẩm Của Chúng Tôi</h2>

                <!-- Nút Thêm Sản Phẩm (Dành cho Admin/Nhân viên) -->
                <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                    <div style="margin-bottom: 20px; text-align: right;">
                        <button onclick="openProductModal('add')" class="btn-submit" style="width: auto; display: inline-block;">
                            <i class="fa-solid fa-plus"></i> Thêm Sản Phẩm Mới
                        </button>
                    </div>
                </c:if>

                <!-- LƯỚI SẢN PHẨM ĐỘNG TỪ DATABASE -->
                <div class="product-grid">

                    <%-- Duyệt qua danh sách productList được gửi từ Servlet --%>
                    <c:forEach items="${productList}" var="p">
                        <div class="product-card">
                            <div class="product-img">
                                <c:choose>
                                    <c:when test="${not empty p.imageBase64}">
                                        <img src="${p.imageBase64}" alt="${p.productName}" style="max-height: 140px; max-width: 100%;">
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fa-solid fa-box-open" style="font-size: 60px;"></i>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <h3 class="product-title">${p.productName}</h3>
                            <div class="product-price">${p.basePrice} ₫</div>

                            <!-- PHÂN QUYỀN NÚT BẤM -->
                            <c:choose>
                                <c:when test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                                    <div style="display: flex; gap: 10px;">
                                        <!-- Gọi Modal Sửa và truyền dữ liệu thật của sản phẩm vào -->
                                        <button onclick="openProductModal('edit', '${p.productId}', '${p.productName}', '${p.basePrice}', '${p.description}', '${p.imageBase64}')" class="btn-view" style="flex: 1; background: #3b82f6; border: none; cursor: pointer;">
                                            <i class="fa-solid fa-pen"></i> Sửa
                                        </button>
                                        <!-- Gọi Modal Xóa -->
                                        <button onclick="openDeleteModal('${p.productId}')" class="btn-view" style="flex: 1; background: #ef4444; border: none; cursor: pointer;">
                                            <i class="fa-solid fa-trash"></i> Xóa
                                        </button>
                                    </div>
                                </c:when>

                                <%-- DÀNH CHO KHÁCH HÀNG HOẶC NGƯỜI CHƯA ĐĂNG NHẬP --%>
                                <c:otherwise>
                                    <div style="display: flex; gap: 10px;">
                                        <!-- Nút Xem chi tiết -->
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${p.productId}" class="btn-view" style="flex: 1; background: #4a5568; border-color: #4a5568;">
                                            <i class="fa-solid fa-eye"></i> Xem
                                        </a>
                                        <!-- Nút Thêm Giỏ Hàng -->
                                        <a href="${pageContext.request.contextPath}/add-to-cart?id=${p.productId}" class="btn-view" style="flex: 1;">
                                            <i class="fa-solid fa-cart-plus"></i> Mua
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>

                    <!-- Nếu chưa có sản phẩm nào trong DB -->
                    <c:if test="${empty productList}">
                        <p style="color: #94a3b8; grid-column: 1 / -1; text-align: center;">Chưa có sản phẩm nào trong cửa hàng. Hãy thêm sản phẩm mới!</p>
                    </c:if>

                </div> <!-- Đóng thẻ product-grid -->
            </div>
        </div>
        <jsp:include page="includes/footer.jsp" />

        <!-- =============================================================== -->
        <!-- CÁC MODAL (POPUP GIAO DIỆN CHÌM) SẼ NẰM Ở ĐÂY -->
        <!-- =============================================================== -->

        <!-- 1. MODAL THÊM / SỬA SẢN PHẨM -->
        <div class="modal-overlay" id="productModal">
            <div class="modal-content form-box" style="margin: 0;">
                <div class="modal-header">
                    <h3 id="modalTitle">Thêm Sản Phẩm Mới</h3>
                    <i class="fa-solid fa-xmark close-btn" onclick="closeModal('productModal')"></i>
                </div>

                <form action="${pageContext.request.contextPath}/admin/saveProduct" method="POST">
                    <input type="hidden" name="actionType" id="actionType" value="add">
                    <input type="hidden" name="productId" id="modalProductId" value="">

                    <!-- Nơi chứa mã Base64 cực dài -->
                    <input type="hidden" name="imageBase64" id="imageBase64" value="">

                    <div class="form-group">
                        <label>Tên sản phẩm</label>
                        <input type="text" name="productName" id="modalProductName" required>
                    </div>
                    <div class="form-group">
                        <label>Giá bán (VND)</label>
                        <input type="number" name="basePrice" id="modalProductPrice" required>
                    </div>

                    <div class="form-group">
                        <label>Số lượng kho</label>
                        <input type="number" name="stockQuantity" id="modalProductStock" min="0" required>
                    </div>

                    <!-- BỔ SUNG TRƯỜNG CHI TIẾT SẢN PHẨM Ở ĐÂY -->
                    <div class="form-group">
                        <label>Chi tiết sản phẩm</label>
                        <textarea name="description" id="modalProductDesc" rows="4" placeholder="Nhập cấu hình, mô tả..." style="resize: vertical;"></textarea>
                    </div>

                    <div class="form-group">
                        <label>Hình ảnh sản phẩm (Sẽ tự mã hóa Base64)</label>
                        <input type="file" id="imageInput" accept="image/*">
                        <div style="margin-top: 10px; text-align: center;">
                            <img id="imagePreview" src="" style="max-height: 100px; display: none; border-radius: 6px; border: 1px solid #334155;">
                        </div>
                    </div>

                    <button type="submit" class="btn-submit">Lưu Thông Tin</button>
                </form>
            </div>
        </div>

        <!-- 2. MODAL XÁC NHẬN XÓA -->
        <div class="modal-overlay" id="deleteModal">
            <div class="modal-content form-box" style="margin: 0; width: 350px; text-align: center;">
                <i class="fa-solid fa-circle-exclamation" style="font-size: 50px; color: #ff4757; margin-bottom: 15px;"></i>
                <h3 style="color: #fff; margin-bottom: 10px;">Xác nhận xóa?</h3>
                <p style="color: #94a3b8; margin-bottom: 20px;">Hành động này không thể hoàn tác!</p>

                <form action="${pageContext.request.contextPath}/admin/deleteProduct" method="POST" style="display: flex; gap: 10px;">
                    <input type="hidden" name="productId" id="deleteProductId" value="">
                    <button type="button" class="btn-view" style="flex: 1; cursor: pointer;" onclick="closeModal('deleteModal')">Hủy</button>
                    <button type="submit" class="btn-submit" style="flex: 1; background: #ff4757; border: none;">Xóa ngay</button>
                </form>
            </div>
        </div>

        <!-- ================= SCRIPT XỬ LÝ POPUP & ĐỔI ẢNH ================= -->
        <script>
            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }

            function openDeleteModal(id) {
                document.getElementById('deleteProductId').value = id;
                document.getElementById('deleteModal').style.display = 'flex';
            }

            // Bổ sung tham số desc vào hàm
            function openProductModal(type, id = '', name = '', price = '', desc = '', base64 = '') {
                const modal = document.getElementById('productModal');
                const title = document.getElementById('modalTitle');
                const actionType = document.getElementById('actionType');
                const preview = document.getElementById('imagePreview');
                const inputBase64 = document.getElementById('imageBase64');

                // Gán dữ liệu vào form
                document.getElementById('modalProductId').value = id;
                document.getElementById('modalProductName').value = name;
                document.getElementById('modalProductPrice').value = price;
                document.getElementById('modalProductStock').value = stock;

                // Gán dữ liệu cho trường Mô tả mới thêm
                document.getElementById('modalProductDesc').value = desc;

                try {
                    document.getElementById('imageInput').value = '';
                } catch (e) {
                }

                if (type === 'add') {
                    title.innerText = 'Thêm Sản Phẩm Mới';
                    actionType.value = 'add';
                    inputBase64.value = '';
                    preview.style.display = 'none';
                } else if (type === 'edit') {
                    title.innerText = 'Sửa Sản Phẩm';
                    actionType.value = 'edit';
                    inputBase64.value = base64;
                    if (base64 !== '') {
                        preview.src = base64;
                        preview.style.display = 'inline-block';
                    } else {
                        preview.style.display = 'none';
                    }
                }
                modal.style.display = 'flex';
            }

            // Chuyển ảnh thành Base64
            document.getElementById('imageInput').addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const base64String = e.target.result;
                        document.getElementById('imageBase64').value = base64String;
                        const preview = document.getElementById('imagePreview');
                        preview.src = base64String;
                        preview.style.display = 'inline-block';
                    };
                    reader.readAsDataURL(file);
                }
            });
        </script>
    </body>
</html>