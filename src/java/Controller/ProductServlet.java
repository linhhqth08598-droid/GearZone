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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        // 1. XỬ LÝ LƯU (THÊM / SỬA) SẢN PHẨM
        if (action.equals("/admin/saveProduct")) {
            String actionType = request.getParameter("actionType"); // "add" hoặc "edit"
            
            // Xử lý an toàn cho ID và Price
            int productId = 0;
            if(request.getParameter("productId") != null && !request.getParameter("productId").isEmpty()){
                productId = Integer.parseInt(request.getParameter("productId"));
            }
            double basePrice = Double.parseDouble(request.getParameter("basePrice"));
            
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String imageBase64 = request.getParameter("imageBase64"); 

            // Gọi Service để lưu
            productService.saveProduct(actionType, productId, productName, basePrice, description, imageBase64);

            // Xong việc -> Quay về Home để load lại list sản phẩm
            response.sendRedirect(request.getContextPath() + "/home");
        } 
        
        // 2. XỬ LÝ XÓA SẢN PHẨM
        else if (action.equals("/admin/deleteProduct")) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            
            productService.deleteProduct(productId);
            
            // Quay lại trang chủ
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}