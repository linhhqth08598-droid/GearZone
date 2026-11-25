package khanhnb08894.controller;

import fourSome.model.Categories;
import fourSome.model.Parts;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import khanhnb08894.dao.CategoriesDAO;
import khanhnb08894.service.GameService;
import khanhnb08894.service.ProductService;

/**
 * So sánh linh kiện theo từng loại danh mục (UC 158-160).
 */
@WebServlet(name = "CompareServlet", urlPatterns = {"/compare"})
public class CompareServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final GameService gameService = new GameService();
    private final CategoriesDAO categoriesDAO = new CategoriesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        List<Categories> activeCategories = new ArrayList<>();
        Map<Integer, String> categoryNames = new LinkedHashMap<>();
        for (Categories c : categoriesDAO.getAll()) {
            categoryNames.put(c.getCategoryId(), c.getCategoryName());
            if ("ACTIVE".equalsIgnoreCase(c.getStatus())) {
                activeCategories.add(c);
            }
        }

        List<Parts> allParts = gameService.getCandidatePartsForComparison();
        Integer selectedCategoryId = null;
        String catParam = request.getParameter("categoryId");
        if (catParam != null && catParam.trim().matches("\\d+")) {
            int parsedCatId = Integer.parseInt(catParam.trim());
            if (parsedCatId > 0) {
                selectedCategoryId = parsedCatId;
            }
        }

        String[] rawIds = request.getParameterValues("id");
        List<Integer> parsedIds = new ArrayList<>();
        if (rawIds != null) {
            for (String raw : rawIds) {
                if (raw == null || raw.trim().isEmpty()) {
                    continue;
                }
                for (String token : raw.split(",")) {
                    String t = token.trim();
                    if (t.matches("\\d+")) {
                        int pid = Integer.parseInt(t);
                        if (pid > 0 && !parsedIds.contains(pid) && parsedIds.size() < 4) {
                            parsedIds.add(pid);
                        }
                    }
                }
            }
        }

        // Nếu chưa truyền categoryId nhưng có truyền id linh kiện thì suy ra loại từ linh kiện đầu tiên
        Map<Integer, Parts> partById = new LinkedHashMap<>();
        for (Parts p : allParts) {
            partById.put(p.getPartId(), p);
        }
        if (selectedCategoryId == null && !parsedIds.isEmpty()) {
            Parts first = partById.get(parsedIds.get(0));
            if (first != null && first.getCategoryId() != null) {
                selectedCategoryId = first.getCategoryId();
            }
        }
        if (selectedCategoryId == null && !activeCategories.isEmpty()) {
            selectedCategoryId = activeCategories.get(0).getCategoryId();
        }

        // Lọc danh sách linh kiện ứng viên theo đúng loại danh mục đang chọn
        List<Parts> categoryParts = new ArrayList<>();
        for (Parts p : allParts) {
            if (selectedCategoryId != null && p.getCategoryId() != null
                    && p.getCategoryId().intValue() == selectedCategoryId.intValue()) {
                categoryParts.add(p);
            }
        }

        Map<Integer, String> specSummaries = productService.getSpecSummaries();
        List<Parts> comparedParts = new ArrayList<>();
        Map<Integer, List<String[]>> techSpecsMap = new LinkedHashMap<>();
        Map<Integer, Double> perfPerMillionMap = new LinkedHashMap<>();

        for (Integer pid : parsedIds) {
            Parts p = partById.get(pid);
            if (p == null) {
                p = productService.findById(pid);
            }
            if (p != null && (selectedCategoryId == null
                    || (p.getCategoryId() != null && p.getCategoryId().intValue() == selectedCategoryId.intValue()))) {
                comparedParts.add(p);
                techSpecsMap.put(pid, productService.getTechSpecs(pid));
                double effectivePrice = (p.getSalePrice() != null && p.getSalePrice() > 0)
                        ? p.getSalePrice()
                        : (p.getBasePrice() != null ? p.getBasePrice() : 0.0);
                int score = p.getGamingPerformanceScore() != null ? p.getGamingPerformanceScore() : 0;
                double ratio = effectivePrice > 0 ? (score / (effectivePrice / 1_000_000.0)) : 0.0;
                perfPerMillionMap.put(pid, Math.round(ratio * 100.0) / 100.0);
            }
        }

        request.setAttribute("categories", activeCategories);
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("categoryNames", categoryNames);
        request.setAttribute("categoryParts", categoryParts);
        request.setAttribute("specSummaries", specSummaries);
        request.setAttribute("comparedParts", comparedParts);
        request.setAttribute("techSpecsMap", techSpecsMap);
        request.setAttribute("perfPerMillionMap", perfPerMillionMap);

        boolean isVgaCategory = (selectedCategoryId != null && selectedCategoryId == 4);
        request.setAttribute("isVgaCategory", isVgaCategory);
        request.setAttribute("benchmarks", isVgaCategory ? gameService.getBenchmarkRowsWithNames() : new ArrayList<>());

        request.getRequestDispatcher("/views/khanhnb08894/product/compare.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
