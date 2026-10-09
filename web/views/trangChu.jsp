<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Trang Chủ - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.0">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
    </head>
    <body>
        <jsp:include page="includes/header.jsp" />
        <div class="container main-layout">
            <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
            <div class="layout-right">
                <!-- Nút thêm Slider cho Admin -->
                <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                    <div style="margin-bottom: 15px;">
                        <button onclick="document.getElementById('sliderModal').style.display = 'flex'" class="btn-submit" style="width: auto; display: inline-block; background-color: #3b82f6;">
                            <i class="fa-solid fa-image"></i> Thêm Ảnh Slider
                        </button>
                    </div>
                </c:if>

                <!-- Swiper Slider -->
                <div class="swiper mySwiper" style="width: 100%; border-radius: 12px; overflow: hidden; margin-bottom: 40px; border: 1px solid #2d3748;">
                    <div class="swiper-wrapper">
                        <c:forEach items="${sliderList}" var="s">
                            <div class="swiper-slide" style="position: relative;">
                                <img src="${s.imageBase64}" alt="${s.title}" style="width: 100%; height: 480px; object-fit: contain; background-color: #11141e;">

                                <!-- Nút Xóa dành cho Admin, nổi trên ảnh -->
                                <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                                    <form action="${pageContext.request.contextPath}/admin/deleteSlider" method="POST" style="position: absolute; top: 15px; right: 15px; z-index: 10;">
                                        <input type="hidden" name="sliderId" value="${s.sliderId}">
                                        <button type="submit" onclick="return confirm('Bạn chắc chắn muốn xóa ảnh này?')" style="background: rgba(239, 68, 68, 0.9); color: white; border: none; padding: 8px 15px; border-radius: 6px; cursor: pointer;">
                                            <i class="fa-solid fa-trash"></i> Xóa ảnh
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </c:forEach>

                        <!-- Nếu Database chưa có ảnh nào thì hiện mặc định -->
                        <c:if test="${empty sliderList}">
                            <div class="swiper-slide">
                                <div class="slider-placeholder" style="margin: 0; border: none;">
                                    <div class="slider-content">
                                        <h2>KHUYẾN MÃI BACK TO SCHOOL</h2>
                                        <p>Giảm đến 50% cho sinh viên FPT Polytechnic</p>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    <!-- Các nút điều hướng của Swiper -->
                    <div class="swiper-button-next" style="color: #ff4757;"></div>
                    <div class="swiper-button-prev" style="color: #ff4757;"></div>
                    <div class="swiper-pagination"></div>
                </div>

                <h2 class="section-title">Sản Phẩm Của Chúng Tôi</h2>

                <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                    <div style="margin-bottom: 20px; text-align: right;">
                        <button onclick="openProductModal('add')" class="btn-submit" style="width: auto; display: inline-block;">
                            <i class="fa-solid fa-plus"></i> Thêm Sản Phẩm Mới
                        </button>
                    </div>
                </c:if>

                <!-- LƯỚI SẢN PHẨM ĐÃ FIX LỖI 500 -->
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
                            <div class="product-price">
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${p.basePrice}" pattern="#,###"/> ₫
                            </div>
                            <c:choose>
                                <c:when test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                                    <div style="display: flex; gap: 10px;">
                                        <button onclick="openProductModal('edit', '${p.productId}', '${p.categoryId}', '${p.brandId}', '${p.productName}', '${p.basePrice}', '${p.stockQuantity}', '${p.description}', '${p.imageBase64}')" class="btn-view" style="flex: 1; background: #3b82f6; border: none; cursor: pointer;">
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
                        <p style="color: #94a3b8; grid-column: 1 / -1; text-align: center;">Chưa có sản phẩm nào trong cửa hàng.</p>
                    </c:if>
                </div>
            </div>
        </div>
        <jsp:include page="includes/footer.jsp" />

        <!-- MODAL THÊM / SỬA -->
        <div class="modal-overlay" id="productModal">
            <div class="modal-content form-box" style="margin: 0; width: 500px;">
                <div class="modal-header">
                    <h3 id="modalTitle">Thêm Sản Phẩm Mới</h3>
                    <i class="fa-solid fa-xmark close-btn" onclick="closeModal('productModal')"></i>
                </div>
                <form action="${pageContext.request.contextPath}/admin/saveProduct" method="POST">
                    <input type="hidden" name="actionType" id="actionType" value="add">
                    <input type="hidden" name="productId" id="modalProductId" value="">
                    <input type="hidden" name="imageBase64" id="imageBase64" value="">

                    <div style="display: flex; gap: 10px; margin-bottom: 18px;">

                        <!-- CHỌN DANH MỤC (Lấy động 100% từ SQL) -->
                        <div class="form-group" style="flex: 1; margin-bottom: 0;">
                            <label>Loại sản phẩm (Danh mục)</label>
                            <select name="categoryId" id="modalProductCategory" required>
                                <option value="">-- Chọn Loại --</option>
                                <c:forEach items="${categoryList}" var="c">
                                    <option value="${c.categoryId}">${c.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- CHỌN HÃNG (Lấy động 100% từ SQL kèm data-category để Javascript lọc) -->
                        <div class="form-group" style="flex: 1; margin-bottom: 0;">
                            <label>Thương hiệu (Hãng)</label>
                            <select name="brandId" id="modalProductBrand" required>
                                <option value="">-- Chọn Hãng --</option>
                                <c:forEach items="${brandList}" var="b">
                                    <option value="${b.brandId}" data-category="${b.categoryId}">${b.brandName}</option>
                                </c:forEach>
                            </select>
                        </div>

                    </div>

                    <div class="form-group">
                        <label>Tên sản phẩm</label>
                        <input type="text" name="productName" id="modalProductName" required>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <div class="form-group" style="flex: 1;">
                            <label>Giá bán (VND)</label>
                            <input type="number" name="basePrice" id="modalProductPrice" required>
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Số lượng kho</label>
                            <input type="number" name="stockQuantity" id="modalProductStock" min="0" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Chi tiết sản phẩm</label>
                        <textarea name="description" id="modalProductDesc" rows="3"></textarea>
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
        <!-- MODAL THÊM SLIDER -->
        <div class="modal-overlay" id="sliderModal">
            <div class="modal-content form-box" style="margin: 0; width: 400px;">
                <div class="modal-header">
                    <h3>Thêm Ảnh Slider Mới</h3>
                    <i class="fa-solid fa-xmark close-btn" onclick="closeModal('sliderModal')"></i>
                </div>
                <form action="${pageContext.request.contextPath}/admin/saveSlider" method="POST">
                    <input type="hidden" name="sliderBase64" id="sliderBase64" value="">

                    <div class="form-group">
                        <label>Tiêu đề / Ghi chú</label>
                        <input type="text" name="sliderTitle" required>
                    </div>
                    <div class="form-group">
                        <label>Chọn hình ảnh</label>
                        <input type="file" id="sliderImageInput" accept="image/*" required>
                        <div style="margin-top: 10px; text-align: center;">
                            <img id="sliderPreview" src="" style="width: 100%; max-height: 150px; display: none; border-radius: 6px; object-fit: cover;">
                        </div>
                    </div>
                    <button type="submit" class="btn-submit" style="background-color: #3b82f6;">Lưu Slider</button>
                </form>
            </div>
        </div>

        <!-- MODAL XÓA -->
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
            // Thêm catId và brandId vào tham số
            function openProductModal(type, id = '', catId = '', brandId = '', name = '', price = '', stock = '', desc = '', base64 = '') {
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

                const brandOptions = document.querySelectorAll('#modalProductBrand option');
                brandOptions.forEach(opt => opt.style.display = 'block');

                if (type === 'add') {
                    title.innerText = 'Thêm Sản Phẩm Mới';
                    actionType.value = 'add';
                    inputBase64.value = '';
                    preview.style.display = 'none';
                    document.getElementById('imageInput').value = '';
                    document.getElementById('modalProductCategory').value = '';
                    document.getElementById('modalProductBrand').value = '';
                } else if (type === 'edit') {
                    title.innerText = 'Sửa Sản Phẩm';
                    actionType.value = 'edit';
                    inputBase64.value = base64;

                    // THÊM 3 DÒNG NÀY ĐỂ TỰ ĐỘNG CHỌN LOẠI VÀ HÃNG
                    document.getElementById('modalProductCategory').value = catId;
                    document.getElementById('modalProductCategory').dispatchEvent(new Event('change')); // Kích hoạt bộ lọc hãng
                    document.getElementById('modalProductBrand').value = brandId;

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
        <!-- THÔNG BÁO POPUP THÀNH CÔNG / THẤT BẠI -->
        <c:if test="${not empty sessionScope.errorMsg}">
            <script>alert('${sessionScope.errorMsg}');</script>
            <c:remove var="errorMsg" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.successMsg}">
            <script>alert('${sessionScope.successMsg}');</script>
            <c:remove var="successMsg" scope="session" />
        </c:if>
        <!-- Thư viện JS Swiper -->
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <script>
                // Khởi chạy Swiper Slider
                var swiper = new Swiper(".mySwiper", {
                    spaceBetween: 30,
                    centeredSlides: true,
                    grabCursor: true, // Hiển thị icon bàn tay, cho phép vuốt/kéo thả
                    loop: true, // THÊM DÒNG NÀY ĐỂ SLIDER CHẠY LẶP VÒNG
                    autoplay: {
                        delay: 3500,
                        disableOnInteraction: false,
                    },
                    pagination: {
                        el: ".swiper-pagination",
                        clickable: true,
                    },
                    navigation: {
                        nextEl: ".swiper-button-next",
                        prevEl: ".swiper-button-prev",
                    },
                });

                // Xử lý chuyển ảnh Slider thành Base64
                document.getElementById('sliderImageInput').addEventListener('change', function (event) {
                    const file = event.target.files[0];
                    if (file) {
                        const reader = new FileReader();
                        reader.onload = function (e) {
                            const base64 = e.target.result;
                            document.getElementById('sliderBase64').value = base64;
                            document.getElementById('sliderPreview').src = base64;
                            document.getElementById('sliderPreview').style.display = 'block';
                        };
                        reader.readAsDataURL(file);
                    }
                });
        </script>
    </body>
</html>