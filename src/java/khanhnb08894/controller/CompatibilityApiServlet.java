package khanhnb08894.controller;

import fourSome.model.CompatibilityResult;
import fourSome.model.Users;
import khanhnb08894.service.CompatibilityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CompatibilityApiServlet", urlPatterns = {"/api/compatibility"})
public class CompatibilityApiServlet extends HttpServlet {

    private final CompatibilityService compatibilityService = new CompatibilityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Integer> parts = new HashMap<>();
        String[] partTypes = {"cpu", "main", "ram", "ramQty", "vga", "ssd", "hdd", "psu", "case", "cooler", "monitor"};

        for (String type : partTypes) {
            String param = request.getParameter(type);
            if (param != null && !param.trim().isEmpty()) {
                try {
                    parts.put(type, Integer.parseInt(param.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }

        Integer userId = null;
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") instanceof Users u) {
            userId = u.getUserId();
        }
        Integer buildId = null;
        try {
            String b = request.getParameter("buildId");
            if (b != null && !b.isEmpty()) buildId = Integer.parseInt(b);
        } catch (NumberFormatException ignored) {}

        CompatibilityResult result = compatibilityService.checkBuild(parts, userId, buildId);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"compatible\": ").append(result.isCompatible()).append(",");
        json.append("\"errors\": [");
        for (int i = 0; i < result.getErrors().size(); i++) {
            json.append("\"").append(escapeJson(result.getErrors().get(i))).append("\"");
            if (i < result.getErrors().size() - 1) json.append(",");
        }
        json.append("],");
        json.append("\"warnings\": [");
        for (int i = 0; i < result.getWarnings().size(); i++) {
            json.append("\"").append(escapeJson(result.getWarnings().get(i))).append("\"");
            if (i < result.getWarnings().size() - 1) json.append(",");
        }
        json.append("]}");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}