<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <%-- ============ LAYOUT ADMIN ============ --%>
    <c:when test="${requestScope.adminLayout}">
    </main> <!-- đóng main.admin-content (mở trong admin_sidebar.jsp) -->
    <footer class="admin-footer">
        <p class="mb-1" style="color: #475569; font-size: 14px; font-weight: 500;">&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>
        <p class="mb-0" style="color: #94a3b8; font-size: 13px;">Dự án JAV102 - Nhóm 2</p>
    </footer>
</div> <!-- đóng div.admin-main -->
</div> <!-- đóng div.admin-wrapper (mở trong admin_header.jsp) -->
</c:when>

<%-- ============ LAYOUT NGƯỜI DÙNG ============ --%>
<%-- ============ LAYOUT USER CÓ SIDEBAR (giống admin) ============ --%>
<c:when test="${requestScope.userSidebarLayout}">
</div> <!-- đóng div.user-content (mở trong sidebar.jsp) -->
<footer class="user-footer">
    <p class="mb-1" style="color: #475569; font-size: 14px; font-weight: 500;">&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>
    <p class="mb-0" style="color: #94a3b8; font-size: 13px;">Dự án JAV102 - Nhóm 2</p>
</footer>
</div> <!-- đóng div.user-main -->
</div> <!-- đóng div.user-wrapper -->
</main> <!-- đóng main mở trong header.jsp -->
</c:when>

<%-- ============ LAYOUT NGƯỜI DÙNG (không sidebar) ============ --%>
<c:otherwise>
</main> <!-- đóng main mở trong header.jsp -->
<footer style="background-color: #ffffff; border-top: 1px solid #e2e8f0; padding: 15px 0; margin-top: auto; width: 100%;">
    <div class="container text-center">
        <p class="mb-1" style="color: #475569; font-size: 14px; font-weight: 500;">&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>
        <p class="mb-0" style="color: #94a3b8; font-size: 13px;">Dự án JAV102 - Nhóm 2</p>
    </div>
</footer>
</c:otherwise>
</c:choose>

<!-- Scripts: Bootstrap chỉ nạp 1 lần duy nhất ở đây -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/views/components/script/script.js?v=4"></script>
<script>
    (function () {
        const contextPath = '${pageContext.request.contextPath}';
        const syncChannel = ('BroadcastChannel' in window) ? new BroadcastChannel('gearzone_sync') : null;
        const pageKey = 'gz_keep_page_' + window.location.pathname;
        const scrollKey = 'gz_scroll_pos_' + window.location.pathname;
        let initOrderId = null;
        let initPartId = null;

        // 1. LƯU VỊ TRÍ CUỘN TRANG & SỐ TRANG (?page=2) TRƯỚC KHI BẤM NÚT SUBMIT
        document.addEventListener('submit', function () {
            sessionStorage.setItem(scrollKey, window.scrollY);
            if (window.location.search.includes('page=')) {
                sessionStorage.setItem(pageKey, window.location.search);
            }
        });

        // Khôi phục đúng số trang (?page=2) nếu vừa bị Servlet redirect về trang 1
        const savedPage = sessionStorage.getItem(pageKey);
        if (savedPage && !window.location.search.includes('page=')) {
            sessionStorage.removeItem(pageKey);
            window.location.replace(window.location.pathname + savedPage);
            return;
        }

        // Khôi phục đúng vị trí cuộn trang sau khi tải lại
        const savedScroll = sessionStorage.getItem(scrollKey);
        if (savedScroll !== null) {
            sessionStorage.removeItem(scrollKey);
            window.scrollTo({ top: parseInt(savedScroll, 10), behavior: 'instant' });
        }

        // 2. CẬP NHẬT SỐ CHUÔNG THÔNG BÁO & GIỎ HÀNG
        function updateBadges(unreadCount, cartCount) {
            document.querySelectorAll('a[href*="/user/notifications"] .badge').forEach(el => {
                el.textContent = unreadCount;
                el.style.display = unreadCount > 0 ? 'inline-block' : 'none';
            });
            document.querySelectorAll('a[href*="/cart"] .badge').forEach(el => {
                el.textContent = cartCount;
                el.style.display = cartCount > 0 ? 'inline-block' : 'none';
            });
        }

        // 3. HIỆN THÔNG BÁO NỔI KHI CÓ ĐƠN HÀNG MỚI HOẶC SẢN PHẨM MỚI
        function showRealtimeBanner(text) {
            if (document.getElementById('gz-realtime-toast')) return;
            const div = document.createElement('div');
            div.id = 'gz-realtime-toast';
            div.style.cssText = 'position:fixed;bottom:20px;right:20px;z-index:9999;background:#0f172a;color:#fff;padding:12px 18px;border-radius:10px;box-shadow:0 10px 25px rgba(0,0,0,0.25);display:flex;align-items:center;gap:12px;font-size:14px;';
            div.innerHTML = '<span>' + text + '</span>'
                + '<button onclick="location.reload()" class="btn btn-sm btn-primary fw-bold">Tải lại</button>'
                + '<button onclick="this.parentElement.remove()" class="btn-close btn-close-white btn-sm"></button>';
            document.body.appendChild(div);
        }

        // Đồng bộ tức thì giữa các tab cùng trình duyệt (0ms)
        if (syncChannel) {
            syncChannel.onmessage = (event) => {
                if (event.data && event.data.type === 'BADGE_UPDATE') {
                    updateBadges(event.data.unreadCount, event.data.cartCount);
                }
            };
        }

        // Gọi ngầm lên server mỗi 5 giây
        async function pollServerState() {
            if (document.hidden) return;
            try {
                const res = await fetch(contextPath + '/api/sync-state');
                if (!res.ok) return;
                const data = await res.json();

                updateBadges(data.unreadCount, data.cartCount);

                if (syncChannel) {
                    syncChannel.postMessage({
                        type: 'BADGE_UPDATE',
                        unreadCount: data.unreadCount,
                        cartCount: data.cartCount
                    });
                }

                if (initOrderId === null) {
                    initOrderId = data.latestOrderId;
                } else if (data.latestOrderId > initOrderId && window.location.pathname.includes('/admin/order')) {
                    initOrderId = data.latestOrderId;
                    showRealtimeBanner('🔔 Có đơn hàng mới vừa được đặt!');
                }

                if (initPartId === null) {
                    initPartId = data.latestPartId;
                } else if (data.latestPartId > initPartId && (window.location.pathname.includes('/home') || window.location.pathname.includes('/product'))) {
                    initPartId = data.latestPartId;
                    showRealtimeBanner('🆕 Cửa hàng vừa cập nhật sản phẩm mới!');
                }
            } catch (e) {}
        }

        pollServerState();
        setInterval(pollServerState, 5000);
    })();
</script>
</body>
</html>