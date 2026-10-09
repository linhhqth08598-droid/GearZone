<%@page contentType="text/html" pageEncoding="UTF-8"%>
<aside class="sidebar">
    <div class="sidebar-widget">
        <h3 class="widget-title"><i class="fa-solid fa-filter"></i> Lọc Giá</h3>
        <ul class="category-list">
            <li><a href="${pageContext.request.contextPath}/products?minPrice=0&maxPrice=1000000">Dưới 1 triệu</a></li>
            <li><a href="${pageContext.request.contextPath}/products?minPrice=1000000&maxPrice=3000000">1 triệu - 3 triệu</a></li>
            <li><a href="${pageContext.request.contextPath}/products?minPrice=3000000&maxPrice=5000000">3 triệu - 5 triệu</a></li>
            <li><a href="${pageContext.request.contextPath}/products?minPrice=5000000&maxPrice=999999999">Trên 5 triệu</a></li>
        </ul>
        
        <!-- Form nhập khoảng giá tự chọn -->
        <div style="margin-top: 15px; border-top: 1px dashed #3e475f; padding-top: 15px;">
            <p style="color: #cbd5e1; font-size: 14px; margin-bottom: 8px;">Hoặc tự nhập khoảng giá:</p>
            <form action="${pageContext.request.contextPath}/products" method="GET" style="display: flex; flex-direction: column; gap: 10px;">
                <input type="number" name="minPrice" placeholder="Từ (VNĐ)" required style="padding: 6px; background: #0f1117; border: 1px solid #334155; color: #fff; border-radius: 4px; outline: none;">
                <input type="number" name="maxPrice" placeholder="Đến (VNĐ)" required style="padding: 6px; background: #0f1117; border: 1px solid #334155; color: #fff; border-radius: 4px; outline: none;">
                <button type="submit" style="background: #ff4757; color: #fff; border: none; padding: 6px; border-radius: 4px; cursor: pointer; font-weight: bold;">Lọc Sản Phẩm</button>
            </form>
        </div>
    </div>
</aside>