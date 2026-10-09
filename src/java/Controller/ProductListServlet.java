package Controller;

import Models.Product;
import Service.ProductService;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {
    
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Tương tự HomeServlet, lấy toàn bộ sản phẩm
        // Sau này bạn có thể bổ sung logic lấy theo CategoryID ở đây
        List<Product> listP = productService.getAllProducts();
        
        request.setAttribute("productList", listP);
        request.setAttribute("activeMenu", "products");
        
        // Chuyển hướng sang giao diện trang sản phẩm
        request.getRequestDispatcher("/views/products.jsp").forward(request, response);
    }
}