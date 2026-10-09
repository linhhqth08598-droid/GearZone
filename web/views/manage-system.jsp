<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Hệ Thống - GearZone</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <h2 class="section-title"><i class="fa-solid fa-gears" style="color: #ff4757;"></i> Quản Lý Danh Mục & Thương Hiệu</h2>
            
            <div style="display: flex; gap: 30px; align-items: flex-start;">
                
                <!-- BẢNG QUẢN LÝ DANH MỤC -->
                <div style="flex: 1; background: #1a1d29; border: 1px solid #2d3748; border-radius: 10px; padding: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                        <h3 style="color: #fff;">Loại Sản Phẩm</h3>
                        <button onclick="openCatModal('add')" class="btn-submit" style="width: auto; padding: 6px 12px; font-size: 13px;"><i class="fa-solid fa-plus"></i> Thêm</button>
                    </div>
                    <table class="cart-table">
                        <thead><tr><th>ID</th><th>Tên Danh Mục</th><th>Hành Động</th></tr></thead>
                        <tbody>
                            <c:forEach items="${categoryList}" var="c">
                                <tr>
                                    <td>${c.categoryId}</td>
                                    <td style="color: #fff;">${c.categoryName}</td>
                                    <td>
                                        <button onclick="openCatModal('edit', '${c.categoryId}', '${c.categoryName}')" style="background: none; border: none; color: #3b82f6; cursor: pointer; margin-right: 10px;"><i class="fa-solid fa-pen"></i></button>
                                        <form action="${pageContext.request.contextPath}/admin/deleteCategory" method="POST" style="display:inline;">
                                            <input type="hidden" name="categoryId" value="${c.categoryId}">
                                            <button type="submit" onclick="return confirm('Bạn có chắc chắn xóa danh mục này?');" style="background: none; border: none; color: #ef4444; cursor: pointer;"><i class="fa-solid fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- BẢNG QUẢN LÝ THƯƠNG HIỆU -->
                <div style="flex: 1.5; background: #1a1d29; border: 1px solid #2d3748; border-radius: 10px; padding: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                        <h3 style="color: #fff;">Thương Hiệu (Hãng)</h3>
                        <button onclick="openBrandModal('add')" class="btn-submit" style="width: auto; padding: 6px 12px; font-size: 13px;"><i class="fa-solid fa-plus"></i> Thêm Hãng</button>
                    </div>
                    <table class="cart-table">
                        <thead><tr><th>ID</th><th>Tên Hãng</th><th>Thuộc Danh Mục ID</th><th>Hành Động</th></tr></thead>
                        <tbody>
                            <c:forEach items="${brandList}" var="b">
                                <tr>
                                    <td>${b.brandId}</td>
                                    <td style="color: #fff; font-weight: bold;">${b.brandName}</td>
                                    <td>${b.categoryId}</td>
                                    <td>
                                        <button onclick="openBrandModal('edit', '${b.brandId}', '${b.brandName}', '${b.categoryId}')" style="background: none; border: none; color: #3b82f6; cursor: pointer; margin-right: 10px;"><i class="fa-solid fa-pen"></i></button>
                                        <form action="${pageContext.request.contextPath}/admin/deleteBrand" method="POST" style="display:inline;">
                                            <input type="hidden" name="brandId" value="${b.brandId}">
                                            <button type="submit" onclick="return confirm('Bạn có chắc chắn xóa hãng này?');" style="background: none; border: none; color: #ef4444; cursor: pointer;"><i class="fa-solid fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL THÊM/SỬA DANH MỤC -->
    <div class="modal-overlay" id="catModal">
        <div class="modal-content form-box" style="margin: 0; width: 400px;">
            <div class="modal-header"><h3 id="catModalTitle">Thêm Danh Mục</h3><i class="fa-solid fa-xmark close-btn" onclick="document.getElementById('catModal').style.display='none'"></i></div>
            <form action="${pageContext.request.contextPath}/admin/saveCategory" method="POST">
                <input type="hidden" name="actionType" id="catActionType" value="add">
                <input type="hidden" name="categoryId" id="catId" value="">
                <div class="form-group"><label>Tên Danh Mục</label><input type="text" name="categoryName" id="catName" required></div>
                <button type="submit" class="btn-submit">Lưu</button>
            </form>
        </div>
    </div>

    <!-- MODAL THÊM/SỬA THƯƠNG HIỆU -->
    <div class="modal-overlay" id="brandModal">
        <div class="modal-content form-box" style="margin: 0; width: 400px;">
            <div class="modal-header"><h3 id="brandModalTitle">Thêm Hãng</h3><i class="fa-solid fa-xmark close-btn" onclick="document.getElementById('brandModal').style.display='none'"></i></div>
            <form action="${pageContext.request.contextPath}/admin/saveBrand" method="POST">
                <input type="hidden" name="actionType" id="brandActionType" value="add">
                <input type="hidden" name="brandId" id="brandId" value="">
                
                <div class="form-group">
                    <label>Hãng này thuộc Danh mục nào?</label>
                    <select name="categoryId" id="brandCatId" required>
                        <c:forEach items="${categoryList}" var="c">
                            <option value="${c.categoryId}">${c.categoryName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group"><label>Tên Hãng</label><input type="text" name="brandName" id="brandName" required></div>
                <button type="submit" class="btn-submit">Lưu</button>
            </form>
        </div>
    </div>

    <script>
        function openCatModal(type, id = '', name = '') {
            document.getElementById('catModalTitle').innerText = type === 'add' ? 'Thêm Danh Mục' : 'Sửa Danh Mục';
            document.getElementById('catActionType').value = type;
            document.getElementById('catId').value = id;
            document.getElementById('catName').value = name;
            document.getElementById('catModal').style.display = 'flex';
        }
        function openBrandModal(type, id = '', name = '', catId = '') {
            document.getElementById('brandModalTitle').innerText = type === 'add' ? 'Thêm Hãng Mới' : 'Sửa Hãng';
            document.getElementById('brandActionType').value = type;
            document.getElementById('brandId').value = id;
            document.getElementById('brandName').value = name;
            if(catId) document.getElementById('brandCatId').value = catId;
            document.getElementById('brandModal').style.display = 'flex';
        }
    </script>
</body>
</html>