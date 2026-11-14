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
<script src="${pageContext.request.contextPath}/views/components/script/script.js"></script>
</body>
</html>
