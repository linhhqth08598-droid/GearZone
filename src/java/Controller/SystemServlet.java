package Controller;

import DAO.BrandDAO;
import DAO.CategoryDAO;
import Models.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "SystemServlet", urlPatterns = {"/admin/system", "/admin/saveBrand", "/admin/deleteBrand", "/admin/saveCategory", "/admin/deleteCategory"})
public class SystemServlet extends HttpServlet {
    
    private BrandDAO brandDAO = new BrandDAO();
    private CategoryDAO catDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Kiểm tra quyền Admin (Chỉ RoleID <= 3 mới được vào)
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userAcc");
        if(user == null || user.getRoleId() > 3) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Đẩy dữ liệu ra giao diện quản lý
        request.setAttribute("categoryList", catDAO.getAllCategories());
        request.setAttribute("brandList", brandDAO.getAllBrands());
        request.setAttribute("activeMenu", "system");
        
        request.getRequestDispatcher("/views/manage-system.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        try {
            // XỬ LÝ QUẢN LÝ HÃNG (BRAND)
            if ("/admin/saveBrand".equals(action)) {
                String type = request.getParameter("actionType");
                String name = request.getParameter("brandName");
                int catId = Integer.parseInt(request.getParameter("categoryId"));
                
                if ("add".equals(type)) {
                    brandDAO.insertBrand(name, catId);
                } else if ("edit".equals(type)) {
                    brandDAO.updateBrand(Integer.parseInt(request.getParameter("brandId")), name, catId);
                }
            } else if ("/admin/deleteBrand".equals(action)) {
                brandDAO.deleteBrand(Integer.parseInt(request.getParameter("brandId")));
            } 
            // XỬ LÝ QUẢN LÝ DANH MỤC (CATEGORY)
            else if ("/admin/saveCategory".equals(action)) {
                String type = request.getParameter("actionType");
                String name = request.getParameter("categoryName");
                
                if ("add".equals(type)) {
                    catDAO.insertCategory(name);
                } else if ("edit".equals(type)) {
                    catDAO.updateCategory(Integer.parseInt(request.getParameter("categoryId")), name);
                }
            } else if ("/admin/deleteCategory".equals(action)) {
                catDAO.deleteCategory(Integer.parseInt(request.getParameter("categoryId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Làm xong thì load lại trang quản lý
        response.sendRedirect(request.getContextPath() + "/admin/system");
    }
}