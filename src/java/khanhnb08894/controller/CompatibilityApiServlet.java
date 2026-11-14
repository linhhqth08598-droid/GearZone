package khanhnb08894.controller;

import fourSome.model.CompatibilityResult;
import khanhnb08894.service.CompatibilityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CompatibilityApiServlet", urlPatterns = {"/api/compatibility"})
public class CompatibilityApiServlet extends HttpServlet {

    private CompatibilityService compatibilityService = new CompatibilityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Integer> parts = new HashMap<>();
        String[] partTypes = {"cpu", "main", "ram", "vga", "psu"};
        
        for (String type : partTypes) {
            String param = request.getParameter(type);
            if (param != null && !param.isEmpty()) {
                parts.put(type, Integer.parseInt(param));
            }
        }

        CompatibilityResult result = compatibilityService.checkBuild(parts);

        // Tạo JSON thủ công để trả về (để tránh phụ thuộc thư viện ngoài)
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"compatible\": ").append(result.isCompatible()).append(",");
        json.append("\"errors\": [");
        for (int i = 0; i < result.getErrors().size(); i++) {
            json.append("\"").append(result.getErrors().get(i)).append("\"");
            if (i < result.getErrors().size() - 1) json.append(",");
        }
        json.append("],");
        json.append("\"warnings\": [");
        for (int i = 0; i < result.getWarnings().size(); i++) {
            json.append("\"").append(result.getWarnings().get(i)).append("\"");
            if (i < result.getWarnings().size() - 1) json.append(",");
        }
        json.append("]");
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
}