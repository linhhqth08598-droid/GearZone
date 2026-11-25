<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
        <h3 class="fw-bold mb-0"><i class="fas fa-balance-scale text-danger me-2"></i>So sánh Linh kiện theo từng loại</h3>
        <a href="${pageContext.request.contextPath}/product" class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-arrow-left me-1"></i> Danh sách linh kiện
        </a>
    </div>

    <!-- Chọn nhanh từng loại danh mục -->
    <div class="d-flex flex-wrap gap-2 mb-4">
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/compare?categoryId=${cat.categoryId}"
               class="btn btn-sm ${cat.categoryId == selectedCategoryId ? 'btn-danger' : 'btn-outline-dark'}">
                <c:out value="${cat.categoryName}"/>
            </a>
        </c:forEach>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/compare" method="GET" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-semibold">Loại linh kiện</label>
                    <select name="categoryId" id="compareCategorySelect" class="form-select" onchange="this.form.submit()">
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}" ${cat.categoryId == selectedCategoryId ? 'selected' : ''}>
                                <c:out value="${cat.categoryName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <c:forEach var="slotIdx" begin="0" end="2">
                    <div class="col-md-8 col-lg-4">
                        <label class="form-label fw-semibold">Linh kiện #${slotIdx + 1}</label>
                        <select name="id" class="form-select">
                            <option value="">-- Chọn linh kiện cùng loại --</option>
                            <c:forEach var="cand" items="${categoryParts}">
                                <c:set var="selectedId" value="${slotIdx < comparedParts.size() ? comparedParts[slotIdx].partId : 0}" />
                                <option value="${cand.partId}" ${cand.partId == selectedId ? 'selected' : ''}>
                                    <c:out value="${cand.productCode}"/> - <c:out value="${cand.partName}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </c:forEach>

                <div class="col-12 d-flex gap-2">
                    <button type="submit" class="btn btn-danger"><i class="fas fa-sliders-h me-1"></i> So sánh ngay</button>
                    <a href="${pageContext.request.contextPath}/compare?categoryId=${selectedCategoryId}" class="btn btn-outline-secondary">Xóa lựa chọn</a>
                </div>
            </form>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty comparedParts}">
            <div class="alert alert-info rounded-4">
                Hãy chọn từ 2 đến 3 linh kiện cùng loại (<strong><c:out value="${categoryNames[selectedCategoryId]}"/></strong>) để so sánh giá, hiệu năng, hiệu năng/giá và thông số kỹ thuật.
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm border-0 rounded-4 mb-4">
                <div class="card-body p-4">
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th style="width: 220px;">Tiêu chí</th>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <th class="text-center" style="min-width: 210px;">
                                            <div class="fw-bold text-dark"><c:out value="${p.partName}"/></div>
                                            <small class="text-muted"><c:out value="${p.productCode}"/></small>
                                        </th>
                                    </c:forEach>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="fw-semibold">Loại linh kiện</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center"><c:out value="${categoryNames[p.categoryId]}"/></td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Giá tham khảo</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center text-danger fw-bold">
                                            <c:set var="effPrice" value="${not empty p.salePrice and p.salePrice > 0 ? p.salePrice : p.basePrice}" />
                                            <fmt:formatNumber value="${effPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Điểm hiệu năng Gaming</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center fw-semibold"><c:out value="${p.gamingPerformanceScore}"/> điểm</td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Hiệu năng / Giá (điểm / triệu ₫)</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center text-primary fw-bold"><c:out value="${perfPerMillionMap[p.partId]}"/></td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Bảo hành</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center"><c:out value="${p.warrantyMonths}"/> tháng</td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Tóm tắt thông số</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td><small><c:out value="${specSummaries[p.partId]}"/></small></td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Chi tiết kỹ thuật</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td>
                                            <ul class="list-unstyled mb-0 small">
                                                <c:forEach var="sp" items="${techSpecsMap[p.partId]}">
                                                    <li><strong><c:out value="${sp[0]}"/>:</strong> <c:out value="${sp[1]}"/></li>
                                                </c:forEach>
                                            </ul>
                                        </td>
                                    </c:forEach>
                                </tr>
                                <tr>
                                    <td class="fw-semibold">Hành động</td>
                                    <c:forEach var="p" items="${comparedParts}">
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/product?action=detail&id=${p.partId}" class="btn btn-sm btn-outline-primary mb-1">Xem chi tiết</a>
                                            <form action="${pageContext.request.contextPath}/cart" method="POST" class="d-inline">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="partId" value="${p.partId}">
                                                <button type="submit" class="btn btn-sm btn-danger mb-1"><i class="fas fa-cart-plus"></i> Thêm giỏ</button>
                                            </form>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <c:if test="${isVgaCategory and not empty benchmarks}">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4">
                <h5 class="fw-bold mb-3"><i class="fas fa-tachometer-alt text-danger me-2"></i>Bảng FPS Benchmark VGA tham khảo</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Game</th>
                                <th>VGA</th>
                                <th>Độ phân giải</th>
                                <th>Thiết lập</th>
                                <th class="text-end">FPS trung bình</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${benchmarks}">
                                <tr>
                                    <td class="fw-semibold"><c:out value="${b['gameName']}"/></td>
                                    <td><c:out value="${b['partName']}"/></td>
                                    <td><c:out value="${b['resolution']}"/></td>
                                    <td><span class="badge bg-secondary"><c:out value="${b['graphicsPreset']}"/></span></td>
                                    <td class="text-end fw-bold text-success"><c:out value="${b['avgFps']}"/> FPS</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp" />
