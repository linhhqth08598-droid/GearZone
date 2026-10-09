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

        if (action.equals("/admin/saveProduct")) {
            String actionType = request.getParameter("actionType"); 
            int productId = (request.getParameter("productId") != null && !request.getParameter("productId").isEmpty()) ? Integer.parseInt(request.getParameter("productId")) : 0;
            int categoryId = (request.getParameter("categoryId") != null && !request.getParameter("categoryId").isEmpty()) ? Integer.parseInt(request.getParameter("categoryId")) : 1;
            int brandId = (request.getParameter("brandId") != null && !request.getParameter("brandId").isEmpty()) ? Integer.parseInt(request.getParameter("brandId")) : 1;
            double basePrice = Double.parseDouble(request.getParameter("basePrice"));
            int stockQuantity = (request.getParameter("stockQuantity") != null && !request.getParameter("stockQuantity").isEmpty()) ? Integer.parseInt(request.getParameter("stockQuantity")) : 0;
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String imageBase64 = request.getParameter("imageBase64"); 

            productService.saveProduct(actionType, productId, categoryId, brandId, productName, basePrice, stockQuantity, description, imageBase64);
            response.sendRedirect(redirectUrl);
        } else if (action.equals("/admin/deleteProduct")) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            productService.deleteProduct(productId);
            response.sendRedirect(redirectUrl);
        }
    }
}