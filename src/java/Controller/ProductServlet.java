package Controller;

import Service.ProductService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductServlet", urlPatterns = {"/admin/saveProduct", "/admin/deleteProduct"})
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();
        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : request.getContextPath() + "/home";

        try {
            if (action.equals("/admin/saveProduct")) {
                String actionType = request.getParameter("actionType").trim(); 
                
                String idStr = request.getParameter("productId");
                int productId = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;
                
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                int brandId = Integer.parseInt(request.getParameter("brandId"));
                
                String priceStr = request.getParameter("basePrice").replace(",", ".");
                double basePrice = Double.parseDouble(priceStr);
                
                String stockStr = request.getParameter("stockQuantity");
                int stockQuantity = (stockStr != null && !stockStr.isEmpty()) ? Integer.parseInt(stockStr) : 0;
                
                String productName = request.getParameter("productName");
                String description = request.getParameter("description");
                String imageBase64 = request.getParameter("imageBase64"); 

                productService.saveProduct(actionType, productId, categoryId, brandId, productName, basePrice, stockQuantity, description, imageBase64);
                
                request.getSession().setAttribute("successMsg", "Lưu sản phẩm thành công!");
                
            } else if (action.equals("/admin/deleteProduct")) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                productService.deleteProduct(productId);
                request.getSession().setAttribute("successMsg", "Xóa sản phẩm thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Gửi thẳng thông báo lỗi nguyên thủy ra màn hình để báo cho bạn biết
            request.getSession().setAttribute("errorMsg", "THẤT BẠI: " + e.getMessage());
        }
        
        response.sendRedirect(redirectUrl);
    }
}