package khanhnb08894.controller;

import fourSome.model.Users;
import fourSome.model.Brands;
import fourSome.model.Categories;
import khanhnb08894.service.CatalogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminCatalogServlet", urlPatterns = {"/admin/catalog"})
public class AdminCatalogServlet extends HttpServlet {

    private CatalogService catalogService = new CatalogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null) {
            type = "brand"; // Mặc định vào trang Brand
        }

        // Sửa lại đúng tên thư mục (bỏ chữ 'th')
        if (type.equals("brand")) {
            request.setAttribute("brands", catalogService.getAllBrands());
            request.getRequestDispatcher("/views/khanhnb08894/admin/catalog_brand.jsp").forward(request, response);
        } else if (type.equals("category")) {
            request.setAttribute("categories", catalogService.getAllCategories());
            request.getRequestDispatcher("/views/khanhnb08894/admin/catalog_category.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Thêm dòng này cho chắc

        // === THÊM 3 DÒNG NÀY VÀO ĐẦU HÀM ===
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");
        // ====================================

        if ("saveBrand".equals(action)) {
            try {
                int brandId = Integer.parseInt(request.getParameter("brandId"));
                String brandName = request.getParameter("brandName");
                String country = request.getParameter("country");
                String logoUrl = request.getParameter("logoUrl");
                boolean isEdit = brandId > 0;

                Brands brand = new Brands();
                brand.setBrandId(brandId);
                brand.setBrandName(brandName);
                brand.setCountry(country);
                brand.setLogoUrl(logoUrl);
                brand.setStatus("ACTIVE");

                String error = catalogService.saveBrand(brand, isEdit, adminUser);
                if (error == null) {
                    session.setAttribute("message", (isEdit ? "Cập nhật" : "Thêm mới") + " thương hiệu thành công!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Dữ liệu không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/catalog?type=brand");

        } else if ("deleteBrand".equals(action)) {
            try {
                int brandId = Integer.parseInt(request.getParameter("brandId"));
                String error = catalogService.deleteBrand(brandId, adminUser);
                if (error == null) {
                    session.setAttribute("message", "Đã ẩn thương hiệu!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Mã không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/catalog?type=brand");

        } else if ("saveCategory".equals(action)) {
            try {
                int catId = Integer.parseInt(request.getParameter("categoryId"));
                String code = request.getParameter("categoryCode");
                String name = request.getParameter("categoryName");
                String desc = request.getParameter("description");
                boolean isRequired = request.getParameter("isRequired") != null;
                boolean isEdit = catId > 0;

                Categories cat = new Categories();
                cat.setCategoryId(catId);
                cat.setCategoryCode(code);
                cat.setCategoryName(name);
                cat.setDescription(desc);
                cat.setIsRequiredInBuild(isRequired);
                cat.setStatus("ACTIVE");

                String error = catalogService.saveCategory(cat, isEdit, adminUser);
                if (error == null) {
                    session.setAttribute("message", (isEdit ? "Cập nhật" : "Thêm mới") + " danh mục thành công!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Dữ liệu không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/catalog?type=category");

        } else if ("deleteCategory".equals(action)) {
            try {
                int catId = Integer.parseInt(request.getParameter("categoryId"));
                String error = catalogService.deleteCategory(catId, adminUser);
                if (error == null) {
                    session.setAttribute("message", "Đã ẩn danh mục!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (Exception e) {
                session.setAttribute("error", "Mã không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/catalog?type=category");
        }
    }
}
