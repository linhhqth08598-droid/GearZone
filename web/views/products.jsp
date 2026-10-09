<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tất cả Sản Phẩm - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=1.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>

        <!-- NHÚNG HEADER -->
        <jsp:include page="includes/header.jsp" />

        <div class="container main-layout">
            <!-- NHÚNG SIDEBAR -->
            <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>

            <div class="layout-right">

                <!-- Bỏ qua Banner, tập trung vào Tiêu đề và Thanh điều hướng/sắp xếp -->
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #334155; padding-bottom: 10px;">
                    <h2 class="section-title" style="margin: 0; border: none; padding: 0;"><i class="fa-solid fa-boxes-stacked" style="color: #ff4757;"></i> Tất cả Sản Phẩm</h2>

                    <!-- Khung sắp xếp (Ví dụ giao diện tĩnh) -->
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <span style="color: #94a3b8; font-size: 14px;">Sắp xếp:</span>
                        <select style="background: #1e2336; color: #fff; border: 1px solid #334155; padding: 6px 12px; border-radius: 4px; outline: none; cursor: pointer;">
                            <option>Mới nhất</option>
                            <option>Giá tăng dần</option>
                            <option>Giá giảm dần</option>
                        </select>
                    </div>
                </div>

                <!-- Nút Thêm Sản Phẩm (Dành cho Admin/Nhân viên) -->
                <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                    <div style="margin-bottom: 20px; text-align: right;">
                        <button onclick="openProductModal('add')" class="btn-submit" style="width: auto; display: inline-block;">
                            <i class="fa-solid fa-plus"></i> Thêm Sản Phẩm Mới
                        </button>
                    </div>
                </c:if>

                <!-- LƯỚI SẢN PHẨM -->
                <div class="product-grid">

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

                            <c:choose>
                                <c:when test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                                    <div style="display: flex; gap: 10px;">
                                        <button onclick="openProductModal('edit', '${p.productId}', '${p.productName}', '${p.basePrice}', '${p.description}', '${p.imageBase64}')" class="btn-view" style="flex: 1; background: #3b82f6; border: none; cursor: pointer;">
                                            <i class="fa-solid fa-pen"></i> Sửa
                                        </button>
                                        <button onclick="openDeleteModal('${p.productId}')" class="btn-view" style="flex: 1; background: #ef4444; border: none; cursor: pointer;">
                                            <i class="fa-solid fa-trash"></i> Xóa
                                        </button>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div style="display: flex; gap: 10px;">
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${p.productId}" class="btn-view" style="flex: 1; background: #4a5568; border-color: #4a5568;">
                                            <i class="fa-solid fa-eye"></i> Xem
                                        </a>
                                        <a href="${pageContext.request.contextPath}/add-to-cart?id=${p.productId}" class="btn-view" style="flex: 1;">
                                            <i class="fa-solid fa-cart-plus"></i> Mua
                                        </a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>

                    <c:if test="${empty productList}">
                        <p style="color: #94a3b8; grid-column: 1 / -1; text-align: center; padding: 40px 0;">Không tìm thấy sản phẩm nào phù hợp.</p>
                    </c:if>

                </div> 

                <!-- Phân trang tĩnh (Ví dụ) -->
                <div style="margin-top: 30px; display: flex; justify-content: center; gap: 8px;">
                    <button style="padding: 8px 12px; background: #ff4757; color: #fff; border: none; border-radius: 4px; font-weight: bold; cursor: pointer;">1</button>
                    <button style="padding: 8px 12px; background: #1e2336; color: #cbd5e1; border: 1px solid #334155; border-radius: 4px; cursor: pointer;">2</button>
                    <button style="padding: 8px 12px; background: #1e2336; color: #cbd5e1; border: 1px solid #334155; border-radius: 4px; cursor: pointer;"><i class="fa-solid fa-chevron-right"></i></button>
                </div>

            </div>
        </div>

        <!-- NHÚNG FOOTER -->
        <jsp:include page="includes/footer.jsp" />

        <!-- =============================================================== -->
        <!-- MODAL (GIỐNG TRANG CHỦ) ĐỂ THÊM/SỬA/XÓA NGAY TẠI CHỖ -->
        <!-- =============================================================== -->

        <div class="modal-overlay" id="productModal">
            <div class="modal-content form-box" style="margin: 0;">
                <div class="modal-header">
                    <h3 id="modalTitle">Thêm Sản Phẩm Mới</h3>
                    <i class="fa-solid fa-xmark close-btn" onclick="closeModal('productModal')"></i>
                </div>

                <form action="${pageContext.request.contextPath}/admin/saveProduct" method="POST">
                    <input type="hidden" name="actionType" id="actionType" value="add">
                    <input type="hidden" name="productId" id="modalProductId" value="">
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

                    <div class="form-group">
                        <label>Chi tiết sản phẩm</label>
                        <textarea name="description" id="modalProductDesc" rows="4" placeholder="Nhập cấu hình, mô tả..." style="resize: vertical;"></textarea>
                    </div>

                    <div class="form-group">
                        <label>Hình ảnh sản phẩm</label>
                        <input type="file" id="imageInput" accept="image/*">
                        <div style="margin-top: 10px; text-align: center;">
                            <img id="imagePreview" src="" style="max-height: 100px; display: none; border-radius: 6px; border: 1px solid #334155;">
                        </div>
                    </div>

                    <button type="submit" class="btn-submit">Lưu Thông Tin</button>
                </form>
            </div>
        </div>

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

        <script>
            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }
            function openDeleteModal(id) {
                document.getElementById('deleteProductId').value = id;
                document.getElementById('deleteModal').style.display = 'flex';
            }
            function openProductModal(type, id = '', name = '', price = '', desc = '', base64 = '') {
                const modal = document.getElementById('productModal');
                const title = document.getElementById('modalTitle');
                const actionType = document.getElementById('actionType');
                const preview = document.getElementById('imagePreview');
                const inputBase64 = document.getElementById('imageBase64');

                document.getElementById('modalProductId').value = id;
                document.getElementById('modalProductName').value = name;
                document.getElementById('modalProductPrice').value = price;
                document.getElementById('modalProductStock').value = stock;
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