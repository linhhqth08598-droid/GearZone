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

@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {

    private ProductService productService = new ProductService();
    private CategoryDAO catDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        String brandIdStr = request.getParameter("brandId");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        List<Product> listP = null;
        try {
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                listP = productService.getProductsByCategory(Integer.parseInt(categoryIdStr));
            } else if (brandIdStr != null && !brandIdStr.isEmpty()) {
                listP = productService.getProductsByBrand(Integer.parseInt(brandIdStr));
            } else if (minPriceStr != null && maxPriceStr != null) {
                listP = productService.getProductsByPriceRange(Double.parseDouble(minPriceStr), Double.parseDouble(maxPriceStr));
            } else {
                listP = productService.getAllProducts();
            }
        } catch (Exception e) {
            listP = productService.getAllProducts();
        }

        // Nạp dữ liệu Loại và Hãng 100% từ Database
        request.setAttribute("categoryList", catDAO.getAllCategories());
        request.setAttribute("brandList", brandDAO.getAllBrands());
        request.setAttribute("productList", listP);
        request.setAttribute("activeMenu", "products");

        request.getRequestDispatcher("/views/products.jsp").forward(request, response);
    }
}
