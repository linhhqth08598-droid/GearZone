package dungntth08581.controller;

import dungntth08581.service.AssemblyService;
import fourSome.model.Users;
import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import linhhqth08598.service.AuditLogService;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Quản lý lắp ráp (admin/kỹ thuật): danh sách, lọc trạng thái, xác nhận/đổi lịch/từ chối (UC 255), D3: báo cáo kiểm tra, ảnh, hoàn tất, bàn giao (UC 259–270). Quyền do AdminAuthFilter (khu TECH). */
@WebServlet(name = "AdminAssemblyServlet", urlPatterns = {"/admin/assembly"})
@MultipartConfig(maxFileSize = 10L * 1024 * 1024, maxRequestSize = 110L * 1024 * 1024)   // tối đa 10 ảnh x 10 MB mỗi lần gửi
public class AdminAssemblyServlet extends HttpServlet {

    private final AssemblyService service = new AssemblyService();
    private final AuditLogService audit = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {   // trang chi tiết: phân công, ghi linh kiện, cập nhật tiến độ (D2)
            AssemblyService.Row row = null;
            try { row = service.getRow(Integer.parseInt(idParam)); } catch (NumberFormatException ignored) { }
            if (row == null) { request.getSession().setAttribute("error", "Không tìm thấy đơn lắp ráp!"); response.sendRedirect(request.getContextPath() + "/admin/assembly"); return; }
            request.setAttribute("a", row);
            request.setAttribute("technicians", service.technicians());
            request.setAttribute("source", service.sourceWithSerials(row.assemblyId));
            request.setAttribute("used", service.usedParts(row.assemblyId));
            request.setAttribute("isService", AssemblyService.isServiceType(row.type));
            request.setAttribute("report", service.getReport(row.assemblyId));
            request.setAttribute("photos", service.photos(row.assemblyId));
            request.getRequestDispatcher("/views/dungntth08581/admin/assembly_detail.jsp").forward(request, response);
            return;
        }
        String status = request.getParameter("status");
        List<AssemblyService.Row> all = service.listForAdmin(status);
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, all.size());
        request.setAttribute("rows", page.slice(all));
        request.setAttribute("pagination", page);
        request.setAttribute("status", status == null ? "" : status);
        request.getRequestDispatcher("/views/dungntth08581/admin/assembly.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String err; int id = 0;
        String ct = request.getContentType();
        if (request.getParameter("id") == null && ct != null && ct.toLowerCase().startsWith("multipart/")) {
            System.out.println("[ASSEMBLY] multipart bị từ chối (quá dung lượng?) contentLength=" + request.getContentLengthLong());
            session.setAttribute("error", "Không nhận được ảnh: tổng dung lượng quá lớn (mỗi ảnh ≤ 10 MB). Hãy chọn ít ảnh hơn mỗi lần.");
            String ref = request.getHeader("Referer");
            response.sendRedirect(ref != null && ref.contains("/admin/assembly?id=") ? ref : request.getContextPath() + "/admin/assembly");
            return;
        }
        try {
            id = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            if ("addPhoto".equals(action)) {   // log chẩn đoán upload ảnh (xem ở cửa sổ Output/Tomcat log)
                StringBuilder sb = new StringBuilder("[ASSEMBLY addPhoto] id=" + id + " contentType=" + request.getContentType() + " parts:");
                try { for (jakarta.servlet.http.Part pt : request.getParts()) sb.append(" ").append(pt.getName()).append("=").append(pt.getSize()).append("B"); }
                catch (Exception ex) { sb.append(" LỖI getParts: ").append(ex); }
                System.out.println(sb);
            }
            if ("assign".equals(action)) err = service.assign(id, Integer.parseInt(request.getParameter("technicianId")));
            else if ("addPart".equals(action)) err = service.addUsedPart(id, Integer.parseInt(request.getParameter("partId")), Integer.parseInt(request.getParameter("qty")), request.getParameter("serial"));
            else if ("removePart".equals(action)) err = service.removeUsedPart(id, Integer.parseInt(request.getParameter("usedId")));
            else if ("advance".equals(action)) err = service.advance(id, request.getParameter("next"));
            else if ("saveReport".equals(action)) err = service.saveReport(id, "1".equals(request.getParameter("boot")),
                    request.getParameter("cpuIdle"), request.getParameter("cpuLoad"), request.getParameter("gpuIdle"), request.getParameter("gpuLoad"),
                    "1".equals(request.getParameter("stable")), request.getParameter("stableMin"),
                    request.getParameter("cinebench"), request.getParameter("dmark"), request.getParameter("note"), request.getParameter("finalResult"));
            else if ("addPhoto".equals(action)) {
                err = null;
                int added = 0;
                for (jakarta.servlet.http.Part part : request.getParts()) {
                    if (!"photos".equals(part.getName()) || part.getSize() == 0) continue;
                    err = service.addPhoto(id, part);
                    if (err != null) break;
                    added++;
                }
                if (err == null && added == 0) err = "Vui lòng chọn ảnh!";
                if (err != null && added > 0) err = "Đã tải " + added + " ảnh, ảnh tiếp theo lỗi: " + err;
            }
            else if ("removePhoto".equals(action)) err = service.removePhoto(id, Integer.parseInt(request.getParameter("photoId")));
            else if ("complete".equals(action)) err = service.complete(id);
            else if ("handOver".equals(action)) err = service.handOver(id);
            else err = service.adminAction(id, action, request.getParameter("when"), request.getParameter("reason"));
            if (err == null && admin != null) audit.logAction(admin.getUserId(), "ASSEMBLY_" + action.toUpperCase(), "assembly_orders", id, "Lắp ráp #" + id + ": " + action);
        } catch (NumberFormatException e) { err = "Dữ liệu không hợp lệ!"; }
        if (err == null) session.setAttribute("message", "Đã cập nhật đơn lắp ráp #" + id + "."); else session.setAttribute("error", err);
        if (request.getParameter("detail") != null) { response.sendRedirect(request.getContextPath() + "/admin/assembly?id=" + id); return; }
        String back = request.getContextPath() + "/admin/assembly";
        String st = request.getParameter("status");
        if (st != null && !st.isEmpty()) back += "?status=" + java.net.URLEncoder.encode(st, java.nio.charset.StandardCharsets.UTF_8);
        response.sendRedirect(back);
    }
}