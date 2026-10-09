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

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy danh sách sản phẩm thật từ Database
        List<Product> listP = productService.getAllProducts();
        
        // Gán danh sách vào request để jsp dùng vòng lặp <c:forEach>
        request.setAttribute("productList", listP);
        
        // Chuyển hướng hiển thị ra giao diện
        request.getRequestDispatcher("/views/trangChu.jsp").forward(request, response);
    }
}