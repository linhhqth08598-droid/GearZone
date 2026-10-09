package Controller;

import DAO.BrandDAO;
import DAO.CategoryDAO;
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
    private CategoryDAO catDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Product> listP = productService.getAllProducts();
        
        // TRUYỀN DỮ LIỆU ĐỘNG SANG JSP
        request.setAttribute("categoryList", catDAO.getAllCategories());
        request.setAttribute("brandList", brandDAO.getAllBrands());
        request.setAttribute("productList", listP);
        request.setAttribute("activeMenu", "home");
        
        request.getRequestDispatcher("/views/trangChu.jsp").forward(request, response);
    }
}