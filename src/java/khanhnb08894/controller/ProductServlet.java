package khanhnb08894.controller;

import fourSome.model.Parts;
import fourSome.model.Users;
import fourSome.url.DBConnect;
import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import khanhnb08894.dao.BrandsDAO;
import khanhnb08894.dao.CategoriesDAO;
import khanhnb08894.dao.PartMediaDAO;
import khanhnb08894.dao.PartsDAO;
import khanhnb08894.service.ProductService;
import khanhnb08894.service.SpecService;
import linhhqth08598.service.ReviewService;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {

    private final PartsDAO partsDAO = new PartsDAO();
    private final ProductService productService = new ProductService();
    private final CategoriesDAO categoriesDAO = new CategoriesDAO();
    private final BrandsDAO brandsDAO = new BrandsDAO();
    private final SpecService specService = new SpecService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");

        if ("detail".equals(action)) {
            try {
                int partId = Integer.parseInt(request.getParameter("id"));
                Parts part = productService.findById(partId);

                if (part != null) {
                    request.setAttribute("part", part);
                    request.setAttribute("primaryImage", new PartMediaDAO().getPrimaryUrl(partId));
                    request.setAttribute("generalSpecs", productService.getGeneralSpec(partId));
                    request.setAttribute("techSpecs", productService.getTechSpecs(partId));

                    String ogDesc = part.getDescription();
                    if (ogDesc == null || ogDesc.trim().isEmpty()) {
                        ogDesc = "Mua " + part.getPartName() + " chính hãng tại GearZone, bảo hành " + part.getWarrantyMonths() + " tháng.";
                    } else if (ogDesc.length() > 180) {
                        ogDesc = ogDesc.substring(0, 177) + "...";
                    }
                    request.setAttribute("ogTitle", part.getPartName() + " - GearZone");
                    request.setAttribute("ogDescription", ogDesc);
                    String ogImg = (String) request.getAttribute("primaryImage");
                    if (ogImg != null && !ogImg.isEmpty()) {
                        request.setAttribute("ogImage", ogImg);
                    }

                    ReviewService reviewService = new ReviewService();
                    request.setAttribute("reviews", reviewService.getReviewsByPart(partId));

                    Users loggedUser = (Users) request.getSession().getAttribute("loggedUser");
                    request.setAttribute("canReview", loggedUser != null && reviewService.canReview(loggedUser.getUserId(), partId));
                    boolean isWishlisted = false;
                    boolean isPriceAlerted = false;

                    if (loggedUser != null) {
                        try (Connection conn = DBConnect.getConnection()) {
                            String sqlW = "SELECT 1 FROM user_wishlists WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
                            try (PreparedStatement ps = conn.prepareStatement(sqlW)) {
                                ps.setInt(1, loggedUser.getUserId());
                                ps.setInt(2, partId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        isWishlisted = true;
                                    }
                                }
                            }
                            String sqlP = "SELECT 1 FROM price_drop_notifications WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
                            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                                ps.setInt(1, loggedUser.getUserId());
                                ps.setInt(2, partId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        isPriceAlerted = true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    request.setAttribute("isWishlisted", isWishlisted);
                    request.setAttribute("isPriceAlerted", isPriceAlerted);
                    request.getRequestDispatcher("/views/khanhnb08894/product/detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            Integer categoryId = parsePosInt(request.getParameter("categoryId"));
            Integer brandId = parsePosInt(request.getParameter("brandId"));
            Double minPrice = parsePosDouble(request.getParameter("minPrice"));
            Double maxPrice = parsePosDouble(request.getParameter("maxPrice"));
            String kw = request.getParameter("q");
            if (kw == null || kw.trim().isEmpty()) {
                kw = request.getParameter("keyword");
            }
            boolean inStockOnly = "1".equals(request.getParameter("inStock")) || "true".equalsIgnoreCase(request.getParameter("inStock"));
            boolean saleOnly = "1".equals(request.getParameter("saleOnly")) || "true".equalsIgnoreCase(request.getParameter("saleOnly"));
            Double minRating = parsePosDouble(request.getParameter("minRating"));
            String socket = trimOrNull(request.getParameter("socket"));
            String chipset = trimOrNull(request.getParameter("chipset"));
            String ramDdr = trimOrNull(request.getParameter("ramDdr"));
            Integer ramCapacityGb = parsePosInt(request.getParameter("ramGb"));
            Integer minVramGb = parsePosInt(request.getParameter("vramGb"));
            String sortBy = trimOrNull(request.getParameter("sort"));

            List<Parts> filtered = productService.filterAndSortParts(
                    categoryId, brandId, minPrice, maxPrice, kw,
                    inStockOnly, saleOnly, minRating, socket, chipset,
                    ramDdr, ramCapacityGb, minVramGb, sortBy);

            PaginationUtil pagination = new PaginationUtil(request.getParameter("page"), Constants.DEFAULT_PAGE_SIZE, filtered.size());
            List<Parts> pageParts = pagination.slice(filtered);

            request.setAttribute("categories", categoriesDAO.getAll());
            request.setAttribute("brands", brandsDAO.getAll());
            request.setAttribute("parts", pageParts);
            request.setAttribute("totalFiltered", filtered.size());
            request.setAttribute("currentPage", pagination.getCurrentPage());
            request.setAttribute("totalPages", pagination.getTotalPages());
            request.setAttribute("paginationBaseUrl", buildPaginationBaseUrl(
                    request.getContextPath() + "/product",
                    categoryId, brandId, minPrice, maxPrice, kw,
                    inStockOnly, saleOnly, minRating, socket, chipset,
                    ramDdr, ramCapacityGb, minVramGb, sortBy));
            request.setAttribute("specSummaries", specService.getSummaries());
            request.getRequestDispatcher("/views/khanhnb08894/product/list.jsp").forward(request, response);
        }
    }

    private String buildPaginationBaseUrl(
            String base, Integer categoryId, Integer brandId, Double minPrice, Double maxPrice,
            String kw, boolean inStockOnly, boolean saleOnly, Double minRating,
            String socket, String chipset, String ramDdr, Integer ramCapacityGb, Integer minVramGb, String sortBy) {
        StringBuilder sb = new StringBuilder(base).append("?");
        appendParam(sb, "categoryId", categoryId);
        appendParam(sb, "brandId", brandId);
        appendParam(sb, "minPrice", minPrice);
        appendParam(sb, "maxPrice", maxPrice);
        appendParam(sb, "q", trimOrNull(kw));
        if (inStockOnly) {
            appendParam(sb, "inStock", "1");
        }
        if (saleOnly) {
            appendParam(sb, "saleOnly", "1");
        }
        appendParam(sb, "minRating", minRating);
        appendParam(sb, "socket", socket);
        appendParam(sb, "chipset", chipset);
        appendParam(sb, "ramDdr", ramDdr);
        appendParam(sb, "ramGb", ramCapacityGb);
        appendParam(sb, "vramGb", minVramGb);
        appendParam(sb, "sort", sortBy);
        if (sb.charAt(sb.length() - 1) == '?') {
            sb.append("page=");
        } else {
            sb.append("&page=");
        }
        return sb.toString();
    }

    private void appendParam(StringBuilder sb, String key, Object val) {
        if (val == null) {
            return;
        }
        String s = String.valueOf(val).trim();
        if (s.isEmpty()) {
            return;
        }
        if (sb.charAt(sb.length() - 1) != '?') {
            sb.append("&");
        }
        sb.append(key).append("=").append(URLEncoder.encode(s, StandardCharsets.UTF_8));
    }

    private Integer parsePosInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            int v = Integer.parseInt(raw.trim());
            return v > 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parsePosDouble(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            double v = Double.parseDouble(raw.trim().replace(",", ""));
            return v > 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trimOrNull(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        return t.isEmpty() ? null : t;
    }
}
