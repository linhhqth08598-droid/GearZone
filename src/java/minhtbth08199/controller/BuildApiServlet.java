package minhtbth08199.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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

@WebServlet(name = "BuildApiServlet", urlPatterns = {"/api/build/*"})
public class BuildApiServlet extends HttpServlet {
    private final BuildPartsDAO catalog = new BuildPartsDAO();
    private final BuildPCService builds = new BuildPCService();

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
        StringBuilder json = new StringBuilder("{\"totalPrice\":").append(builds.get(buildId).getTotalPrice()).append(",\"items\":[");
        for (BuildPartItem item : builds.getSelectedParts(buildId)) {
            if (json.charAt(json.length() - 1) != '[') json.append(',');
            json.append("{\"id\":").append(item.getPartId()).append(",\"categoryId\":").append(item.getCategoryId())
                .append(",\"name\":\"").append(escape(item.getPartName())).append("\",\"quantity\":").append(item.getQuantity())
                .append(",\"price\":").append(item.getUnitPrice()).append('}');
        }
        return json.append("]}").toString();
    }

    private void requireAccess(HttpServletRequest request, int buildId) {
        PcBuilds build = builds.get(buildId);
        if (build == null || !BuildPCServlet.canAccess(request, build)) {
            throw new IllegalArgumentException("Bạn không có quyền sửa cấu hình này");
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
