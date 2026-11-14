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
import java.io.IOException;

@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
public class AdminProductServlet extends HttpServlet {
    private PartsDAO partsDAO = new PartsDAO();
    private CategoriesDAO categoriesDAO = new CategoriesDAO();
    private BrandsDAO brandsDAO = new BrandsDAO();

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
                    AuditLogService log = new AuditLogService();
                    log.logAction(adminUser.getUserId(), "INSERT_PART", "parts", 0, "Thêm linh kiện: " + part.getPartName());
                } else {
                    session.setAttribute("error", "Lỗi: Mã sản phẩm có thể bị trùng!");
                }
            } else if ("deleteProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.softDelete(id)) {
                    session.setAttribute("message", "Đã ẩn linh kiện!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu không hợp lệ! Lỗi: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/product");
    }
}