package dungntth08581.controller;

import dungntth08581.service.WarrantyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "PublicWarrantyServlet", urlPatterns = {"/warranty-lookup"})
public class PublicWarrantyServlet extends HttpServlet {

    private final WarrantyService warrantyService = new WarrantyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<HashMap<String, Object>> results = warrantyService.lookupPublicWarranty(keyword.trim());
            req.setAttribute("keyword", keyword.trim());
            req.setAttribute("results", results);
            if (results.isEmpty()) {
                req.setAttribute("partPolicy", warrantyService.lookupPartPolicy(keyword.trim()));
            }
            req.setAttribute("searched", true);
        }
        req.getRequestDispatcher("/views/dungntth08581/support/warranty.jsp").forward(req, resp);
    }
}
