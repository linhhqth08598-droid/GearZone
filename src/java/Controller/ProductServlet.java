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
        
        // Lấy đường dẫn gốc của trang mà người dùng vừa đứng trước khi submit
        String referer = request.getHeader("Referer");
        // Nếu không có Referer, mặc định cho về /home
        String redirectUrl = (referer != null) ? referer : request.getContextPath() + "/home";

        // 1. XỬ LÝ LƯU (THÊM / SỬA) SẢN PHẨM
        if (action.equals("/admin/saveProduct")) {
            String actionType = request.getParameter("actionType"); 
            
            int productId = 0;
            if(request.getParameter("productId") != null && !request.getParameter("productId").isEmpty()){
                productId = Integer.parseInt(request.getParameter("productId"));
            }
            double basePrice = Double.parseDouble(request.getParameter("basePrice"));
            
            // Lấy thêm số lượng kho từ form
            int stockQuantity = 0;
            if(request.getParameter("stockQuantity") != null && !request.getParameter("stockQuantity").isEmpty()){
                stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));
            }
            
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String imageBase64 = request.getParameter("imageBase64"); 

            // Truyền đủ biến stockQuantity vào Service
            productService.saveProduct(actionType, productId, productName, basePrice, stockQuantity, description, imageBase64);

            // Quay về lại đúng trang trước đó (Home hoặc Products)
            response.sendRedirect(redirectUrl);
        } 
        
        // 2. XỬ LÝ XÓA SẢN PHẨM
        else if (action.equals("/admin/deleteProduct")) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            productService.deleteProduct(productId);
            
            // Quay về lại đúng trang trước đó
            response.sendRedirect(redirectUrl);
        }
    }
}