package khanhnb08894.controller;

import fourSome.model.Parts;
import fourSome.model.Users;
import khanhnb08894.dao.BrandsDAO;
import khanhnb08894.dao.CategoriesDAO;
import khanhnb08894.dao.PartsDAO;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.Part;
import java.io.IOException;

@jakarta.servlet.annotation.MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
public class AdminProductServlet extends HttpServlet {
    private PartsDAO partsDAO = new PartsDAO();
    private CategoriesDAO categoriesDAO = new CategoriesDAO();
    private BrandsDAO brandsDAO = new BrandsDAO();
    private khanhnb08894.dao.PartMediaDAO mediaDAO = new khanhnb08894.dao.PartMediaDAO();

    /** Lưu ảnh chính: ưu tiên file tải lên (jpg/png/webp/gif ≤ 5MB), nếu không có thì dùng đường dẫn ảnh. */
    private String saveImage(HttpServletRequest request, int partId) throws IOException, ServletException {
        String url = null;
        Part filePart = request.getPart("imageFile");
        if (filePart != null && filePart.getSize() > 0) {
            String ct = filePart.getContentType() == null ? "" : filePart.getContentType().toLowerCase();
            if (!java.util.Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif").contains(ct)) return "Ảnh phải là jpg, png, webp hoặc gif!";
            if (filePart.getSize() > 2 * 1024 * 1024) return "Ảnh tối đa 2MB!";
            // Lưu Base64 thẳng vào part_media.media_url (giống ảnh đại diện)
            byte[] bytes = filePart.getInputStream().readAllBytes();
            url = "data:" + ct + ";base64," + java.util.Base64.getEncoder().encodeToString(bytes);
        } else {
            String u = request.getParameter("imageUrl");
            if (u != null && !u.trim().isEmpty()) {
                u = u.trim();
                if (!(u.startsWith("http://") || u.startsWith("https://"))) return "Đường dẫn ảnh phải bắt đầu bằng http:// hoặc https://";
                url = u;
            }
        }
        if (url == null) return null;
        return mediaDAO.setPrimary(partId, url) ? null : "Không lưu được ảnh (đã chạy file SQL đổi cột media_url sang nvarchar(max) chưa?)";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy dữ liệu đổ ra bảng
        request.setAttribute("parts", partsDAO.getAll());
        // Lấy danh mục và thương hiệu để đổ vào Dropdown
        request.setAttribute("categories", categoriesDAO.getAll());
        request.setAttribute("brands", brandsDAO.getAll());
        
        request.getRequestDispatcher("/views/khanhnb08894/admin/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            if ("saveProduct".equals(action)) {
                Parts part = new Parts();
                part.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
                part.setBrandId(Integer.parseInt(request.getParameter("brandId")));
                part.setProductCode(request.getParameter("productCode"));
                part.setPartName(request.getParameter("partName"));
                part.setBasePrice(Double.parseDouble(request.getParameter("basePrice")));
                
                String salePriceStr = request.getParameter("salePrice");
                if (salePriceStr != null && !salePriceStr.isEmpty()) {
                    part.setSalePrice(Double.parseDouble(salePriceStr));
                } else {
                    part.setSalePrice(null);
                }
                
                part.setDescription(request.getParameter("description"));
                part.setWarrantyMonths(Integer.parseInt(request.getParameter("warrantyMonths")));
                
                // Các trường mặc định
                part.setIsRgb(false);
                part.setGamingPerformanceScore(0);
                part.setViewCount(0);
                part.setSoldCount(0);
                part.setStatus("ACTIVE");

                boolean success = partsDAO.insert(part);
                if (success) {
                    session.setAttribute("message", "Thêm linh kiện thành công!");
                    String imgErr = saveImage(request, mediaDAO.findPartIdByCode(part.getProductCode()));
                    if (imgErr != null) session.setAttribute("error", imgErr);
                    AuditLogService log = new AuditLogService();
                    log.logAction(adminUser.getUserId(), "INSERT_PART", "parts", 0, "Thêm linh kiện: " + part.getPartName());
                } else {
                    session.setAttribute("error", "Lỗi: Mã sản phẩm có thể bị trùng!");
                }
            } else if ("updateProduct".equals(action)) {
                Parts part = new Parts();
                part.setPartId(Integer.parseInt(request.getParameter("partId")));
                part.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
                part.setBrandId(Integer.parseInt(request.getParameter("brandId")));
                part.setProductCode(request.getParameter("productCode").trim());
                part.setPartName(request.getParameter("partName").trim());
                part.setBasePrice(Double.parseDouble(request.getParameter("basePrice")));
                String sale = request.getParameter("salePrice");
                part.setSalePrice(sale != null && !sale.isEmpty() ? Double.valueOf(sale) : null);
                part.setDescription(request.getParameter("description"));
                part.setWarrantyMonths(Integer.parseInt(request.getParameter("warrantyMonths")));

                if (partsDAO.update(part)) {
                    session.setAttribute("message", "Cập nhật linh kiện thành công!");
                    String imgErr = saveImage(request, part.getPartId());
                    if (imgErr != null) session.setAttribute("error", imgErr);
                    new AuditLogService().logAction(adminUser.getUserId(), "UPDATE_PART", "parts", part.getPartId(), "Sửa linh kiện: " + part.getPartName());
                } else {
                    session.setAttribute("error", "Không cập nhật được (mã sản phẩm có thể bị trùng).");
                }
            } else if ("deleteProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.softDelete(id)) {
                    session.setAttribute("message", "Đã ẩn linh kiện!");
                    new AuditLogService().logAction(adminUser.getUserId(), "HIDE_PART", "parts", id, "Ẩn linh kiện #" + id);
                }
            } else if ("restoreProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.setStatus(id, "ACTIVE")) {
                    session.setAttribute("message", "Đã hiện lại linh kiện!");
                    new AuditLogService().logAction(adminUser.getUserId(), "SHOW_PART", "parts", id, "Hiện lại linh kiện #" + id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu không hợp lệ! Lỗi: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}