<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<!-- Nội dung chính -->
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-headset me-2"></i>Hỗ trợ Khách hàng</h1>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Mã Ticket</th>
                        <th>Khách hàng</th>
                        <th>Tiêu đề</th>
                        <th>Loại</th>
                        <th>Trạng thái</th>
                        <th>Ngày gửi</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="t" items="${ticketsList}">
                        <tr>
                            <td><strong>#${t.ticketCode}</strong></td>
                            <td>
                                <div class="fw-bold">${t.userId}</div>
                                <!-- Nếu có join full_name thì hiển thị -->
                            </td>
                            <td class="text-wrap" style="max-width: 200px;"><c:out value='${t.subject}'/></td>
                            <td><span class="badge bg-info text-dark">${t.ticketType}</span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${t.status == 'OPEN'}"><span class="badge bg-warning text-dark">Mới (Open)</span></c:when>
                                    <c:when test="${t.status == 'IN_PROGRESS'}"><span class="badge bg-primary">Đang xử lý</span></c:when>
                                    <c:when test="${t.status == 'RESOLVED'}"><span class="badge bg-success">Đã giải quyết</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã đóng</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${t.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td class="text-center">
                                <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#ticketModal-${t.ticketId}">
                                    <i class="fas fa-eye"></i> Xem
                                </button>
                            </td>
                        </tr>

                        <!-- Modal chi tiết Ticket (Đặt ngay trong vòng lặp để lấy đúng ID) -->
                        
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <jsp:include page="/views/components/pagination.jsp"><jsp:param name="url" value="/admin/support"/></jsp:include>
    </div>
</div>

<!-- Cấu hình thông tin liên hệ hiển thị cho khách (lưu vào site_contact_info) -->
<div class="card shadow-sm border-0 rounded-4 mt-4">
    <div class="card-body p-4">
        <h5 class="fw-bold mb-3"><i class="fas fa-address-book text-primary me-2"></i>Cấu hình thông tin liên hệ</h5>
        <form action="${pageContext.request.contextPath}/admin/support" method="post">
            <input type="hidden" name="action" value="updateContactInfo">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Tên công ty / cửa hàng</label>
                    <input type="text" name="companyName" class="form-control" maxlength="200" required
                           value="<c:out value='${siteContactInfo.companyName}'/>">
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Hotline</label>
                    <input type="text" name="hotline" class="form-control" maxlength="50" required
                           value="<c:out value='${siteContactInfo.hotline}'/>">
                </div>
                <div class="col-md-12">
                    <label class="form-label fw-semibold">Lời giới thiệu ngắn</label>
                    <input type="text" name="introText" class="form-control" maxlength="500"
                           value="<c:out value='${siteContactInfo.introText}'/>">
                </div>
                <div class="col-md-12">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <label class="form-label fw-semibold mb-0">Danh sách địa chỉ cơ sở</label>
                        <button type="button" class="btn btn-sm btn-outline-success" id="btnAddBranch">
                            <i class="fas fa-plus me-1"></i>Thêm cơ sở
                        </button>
                    </div>
                    <div id="branchListContainer" class="d-flex flex-column gap-2">
                        <c:choose>
                            <c:when test="${not empty siteContactInfo.addresses}">
                                <c:forEach var="addr" items="${siteContactInfo.addresses}" varStatus="st">
                                    <div class="input-group branch-row">
                                        <span class="input-group-text branch-label">Cơ sở ${st.index + 1}</span>
                                        <input type="text" name="addresses" class="form-control" maxlength="255"
                                               placeholder="Nhập địa chỉ cơ sở..." value="<c:out value='${addr}'/>">
                                        <button type="button" class="btn btn-outline-danger btn-remove-branch" title="Xóa cơ sở">
                                            <i class="fas fa-trash-alt"></i> Xóa
                                        </button>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty siteContactInfo.address1}">
                                    <div class="input-group branch-row">
                                        <span class="input-group-text branch-label">Cơ sở 1</span>
                                        <input type="text" name="addresses" class="form-control" maxlength="255"
                                               placeholder="Nhập địa chỉ cơ sở..." value="<c:out value='${siteContactInfo.address1}'/>">
                                        <button type="button" class="btn btn-outline-danger btn-remove-branch" title="Xóa cơ sở">
                                            <i class="fas fa-trash-alt"></i> Xóa
                                        </button>
                                    </div>
                                </c:if>
                                <c:if test="${not empty siteContactInfo.address2}">
                                    <div class="input-group branch-row">
                                        <span class="input-group-text branch-label">Cơ sở 2</span>
                                        <input type="text" name="addresses" class="form-control" maxlength="255"
                                               placeholder="Nhập địa chỉ cơ sở..." value="<c:out value='${siteContactInfo.address2}'/>">
                                        <button type="button" class="btn btn-outline-danger btn-remove-branch" title="Xóa cơ sở">
                                            <i class="fas fa-trash-alt"></i> Xóa
                                        </button>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="form-text">Có thể thêm nhiều cơ sở hoặc xóa bớt cơ sở tùy ý.</div>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Giờ tư vấn &amp; bán hàng</label>
                    <input type="text" name="salesHours" class="form-control" maxlength="150"
                           value="<c:out value='${siteContactInfo.salesHours}'/>">
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Giờ hỗ trợ kỹ thuật</label>
                    <input type="text" name="techHours" class="form-control" maxlength="150"
                           value="<c:out value='${siteContactInfo.techHours}'/>">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold">Link Facebook</label>
                    <input type="text" name="facebookUrl" class="form-control" maxlength="500"
                           value="<c:out value='${siteContactInfo.facebookUrl}'/>">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold">Link YouTube</label>
                    <input type="text" name="youtubeUrl" class="form-control" maxlength="500"
                           value="<c:out value='${siteContactInfo.youtubeUrl}'/>">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold">Link Chat</label>
                    <input type="text" name="chatUrl" class="form-control" maxlength="500"
                           value="<c:out value='${siteContactInfo.chatUrl}'/>">
                </div>
            </div>
            <div class="mt-3 text-end">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="fas fa-save me-1"></i>Lưu thông tin liên hệ
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    (function () {
        var container = document.getElementById('branchListContainer');
        var addBtn = document.getElementById('btnAddBranch');
        if (!container || !addBtn) return;

        function refreshLabels() {
            var rows = container.querySelectorAll('.branch-row');
            rows.forEach(function (row, idx) {
                var lbl = row.querySelector('.branch-label');
                if (lbl) lbl.textContent = 'Cơ sở ' + (idx + 1);
            });
        }

        function bindRemove(btn) {
            btn.addEventListener('click', function () {
                var row = btn.closest('.branch-row');
                if (row) {
                    row.remove();
                    refreshLabels();
                }
            });
        }

        container.querySelectorAll('.btn-remove-branch').forEach(bindRemove);

        addBtn.addEventListener('click', function () {
            var row = document.createElement('div');
            row.className = 'input-group branch-row';

            var span = document.createElement('span');
            span.className = 'input-group-text branch-label';
            span.textContent = 'Cơ sở ' + (container.querySelectorAll('.branch-row').length + 1);

            var input = document.createElement('input');
            input.type = 'text';
            input.name = 'addresses';
            input.className = 'form-control';
            input.maxLength = 255;
            input.placeholder = 'Nhập địa chỉ cơ sở mới...';

            var delBtn = document.createElement('button');
            delBtn.type = 'button';
            delBtn.className = 'btn btn-outline-danger btn-remove-branch';
            delBtn.title = 'Xóa cơ sở';
            delBtn.innerHTML = '<i class="fas fa-trash-alt"></i> Xóa';
            bindRemove(delBtn);

            row.appendChild(span);
            row.appendChild(input);
            row.appendChild(delBtn);
            container.appendChild(row);
            input.focus();
        });
    })();
</script>


<!-- Các modal đặt ngoài bảng (đặt trong <tbody> là HTML sai cấu trúc) -->
<c:forEach var="t" items="${ticketsList}">
<div class="modal fade" id="ticketModal-${t.ticketId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/support" method="POST">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary"><i class="fas fa-ticket-alt me-2"></i>Chi tiết Ticket: #${t.ticketCode}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <input type="hidden" name="ticketId" value="${t.ticketId}">
                                            
                                            <div class="mb-4">
                                                <h6 class="fw-bold text-dark mb-2">Tiêu đề:</h6>
                                                <p class="text-secondary mb-0"><c:out value='${t.subject}'/></p>
                                            </div>
                                            
                                            <div class="mb-4">
                                                <h6 class="fw-bold text-dark mb-2">Nội dung:</h6>
                                                <div class="bg-light p-3 rounded border" style="white-space: pre-wrap;"><c:out value='${t.content}'/></div>
                                            </div>

                                            <div class="mb-2">
                                                <h6 class="fw-bold text-dark mb-2">Cập nhật trạng thái:</h6>
                                                <select name="status" class="form-select form-control">
                                                    <option value="OPEN" ${t.status == 'OPEN' ? 'selected' : ''}>Mới (Open)</option>
                                                    <option value="IN_PROGRESS" ${t.status == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                                                    <option value="RESOLVED" ${t.status == 'RESOLVED' ? 'selected' : ''}>Đã giải quyết</option>
                                                    <option value="CLOSED" ${t.status == 'CLOSED' ? 'selected' : ''}>Đóng</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                                            <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu thay đổi</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
</c:forEach>

<jsp:include page="/views/components/footer.jsp" />