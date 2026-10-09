package Controller;

import DAO.BrandDAO;
import DAO.CategoryDAO;
import DAO.ReviewDAO; // 1. Import ReviewDAO vào đây
import Models.Product;
import Service.ProductService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product-detail"})
public class ProductDetailServlet extends HttpServlet {
    
    private ProductService productService = new ProductService();
    private CategoryDAO catDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    private ReviewDAO reviewDAO = new ReviewDAO(); // 2. Khởi tạo đối tượng ReviewDAO

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            int productId = Integer.parseInt(idStr);
            Product product = productService.getProductById(productId);
            
            if (product != null) {
                request.setAttribute("product", product);
                
                // 3. Lấy danh sách đánh giá của sản phẩm này truyền ra View
                request.setAttribute("productReviews", reviewDAO.getReviewsByProduct(productId));
                
                // Đổ dữ liệu cho Menu Dropdown trên Header
                request.setAttribute("categoryList", catDAO.getAllCategories());
                request.setAttribute("brandList", brandDAO.getAllBrands());
                
                request.getRequestDispatcher("/views/product-detail.jsp").forward(request, response);
                return;
            }
        }
        
        // Nếu không có ID hoặc không tìm thấy sản phẩm, quay về trang chủ
        response.sendRedirect(request.getContextPath() + "/home");
    }
}