package minhtbth08199.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import fourSome.model.Parts;
import fourSome.model.PcBuilds;
import minhtbth08199.dao.BuildPartsDAO;
import minhtbth08199.model.BuildPartItem;
import minhtbth08199.service.BuildPCService;
import khanhnb08894.service.CompatibilityService;
import fourSome.model.CompatibilityResult;

@WebServlet(name = "BuildApiServlet", urlPatterns = {"/api/build/*"})
public class BuildApiServlet extends HttpServlet {
    private final BuildPartsDAO catalog = new BuildPartsDAO();
    private final BuildPCService builds = new BuildPCService();
    private final CompatibilityService compatibility = new CompatibilityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            if ("/parts".equals(request.getPathInfo())) {
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                List<Parts> parts = catalog.findAvailable(categoryId, request.getParameter("q"), 20);
                StringBuilder json = new StringBuilder("{\"items\":[");
                for (Parts part : parts) {
                    if (json.charAt(json.length() - 1) != '[') json.append(',');
                    double price = part.getSalePrice() != null ? part.getSalePrice() : part.getBasePrice();
                    json.append("{\"id\":").append(part.getPartId())
                        .append(",\"name\":\"").append(escape(part.getPartName()))
                        .append("\",\"price\":").append(price).append('}');
                }
                out.print(json.append("]}").toString());
                return;
            }
            if ("/state".equals(request.getPathInfo())) {
                int buildId = intParameter(request, "buildId");
                requireAccess(request, buildId);
                out.print(stateJson(buildId));
                return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Không tìm thấy API\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            int buildId = intParameter(request, "buildId");
            requireAccess(request, buildId);
            String path = request.getPathInfo();
            if ("/part".equals(path)) {
                builds.setPart(buildId, intParameter(request, "partId"), intParameter(request, "quantity"));
                out.print(stateJson(buildId)); return;
            }
            if ("/remove".equals(path)) {
                builds.removePart(buildId, intParameter(request, "partId"));
                out.print(stateJson(buildId)); return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Không tìm thấy API\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private int intParameter(HttpServletRequest request, String name) {
        try { return Integer.parseInt(request.getParameter(name)); }
        catch (Exception e) { throw new IllegalArgumentException("Tham số " + name + " không hợp lệ"); }
    }

    private String stateJson(int buildId) {
        if (builds.get(buildId) == null) throw new IllegalArgumentException("Cấu hình không tồn tại");
        List<BuildPartItem> selectedParts = builds.getSelectedParts(buildId);
        StringBuilder json = new StringBuilder("{\"totalPrice\":").append(builds.get(buildId).getTotalPrice()).append(",\"items\":[");
        Map<String, Integer> ids = new HashMap<>();
        for (BuildPartItem item : selectedParts) {
            if (json.charAt(json.length() - 1) != '[') json.append(',');
            json.append("{\"id\":").append(item.getPartId()).append(",\"categoryId\":").append(item.getCategoryId())
                .append(",\"name\":\"").append(escape(item.getPartName())).append("\",\"quantity\":").append(item.getQuantity())
                .append(",\"price\":").append(item.getUnitPrice()).append('}');
            String key = switch (item.getCategoryId()) {
                case 1 -> "cpu"; case 2 -> "main"; case 3 -> "ram"; case 4 -> "vga";
                case 7 -> "psu"; case 8 -> "case"; case 9 -> "cooler"; default -> null;
            };
            if (key != null) ids.put(key, item.getPartId());
        }
        CompatibilityResult result = compatibility.checkBuild(ids);
        for (BuildPartItem item : selectedParts) {
            int category = item.getCategoryId();
            if ((category == 1 || category == 2 || category == 3 || category == 4 || category == 7 || category == 8 || category == 9)
                    && !catalog.hasCompatibilitySpecs(category, item.getPartId())) {
                result.addWarning("Thiếu dữ liệu kỹ thuật của " + item.getPartName() + "; chưa thể xác nhận đầy đủ tính tương thích.");
            }
        }
        json.append("],\"compatibility\":{\"compatible\":").append(result.isCompatible())
            .append(",\"errors\":").append(stringArray(result.getErrors()))
            .append(",\"warnings\":").append(stringArray(result.getWarnings())).append("}}");
        return json.toString();
    }

    private String stringArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (String value : values) {
            if (json.length() > 1) json.append(',');
            json.append('"').append(escape(value)).append('"');
        }
        return json.append(']').toString();
    }

    private void requireAccess(HttpServletRequest request, int buildId) {
        PcBuilds build = builds.get(buildId);
        if (build == null || !BuildPCServlet.canAccess(request, build)) {
            throw new IllegalArgumentException("Bạn không có quyền sửa cấu hình này");
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) escaped.append(String.format("\\u%04x", (int) ch));
                    else escaped.append(ch);
                }
            }
        }
        return escaped.toString();
    }

}
