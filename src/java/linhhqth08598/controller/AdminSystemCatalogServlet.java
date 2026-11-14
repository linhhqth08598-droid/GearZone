// File: AdminSystemCatalogServlet.java
package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.SystemCatalog;
import linhhqth08598.dao.SystemCatalogDAO;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminSystemCatalogServlet", urlPatterns = {"/admin/system-catalog"})
public class AdminSystemCatalogServlet extends HttpServlet {
    private SystemCatalogDAO dao = new SystemCatalogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null) type = "chipset";

        String table = "", idCol = "", nameCol = "";
        if (type.equals("chipset")) { table = "chipset_types"; idCol = "chipset_id"; nameCol = "chipset_name"; }
        else if (type.equals("ram")) { table = "ram_standards"; idCol = "standard_id"; nameCol = "standard_name"; }
        else if (type.equals("storage")) { table = "storage_interfaces"; idCol = "interface_id"; nameCol = "interface_name"; }

        List<SystemCatalog> list = dao.getAll(table, idCol, nameCol);
        request.setAttribute("listItems", list);
        request.setAttribute("currentType", type);
        request.getRequestDispatcher("/views/linhhqth08598/admin/system_catalog.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        String table = "", idCol = "", nameCol = "";
        if (type.equals("chipset")) { table = "chipset_types"; idCol = "chipset_id"; nameCol = "chipset_name"; }
        else if (type.equals("ram")) { table = "ram_standards"; idCol = "standard_id"; nameCol = "standard_name"; }
        else if (type.equals("storage")) { table = "storage_interfaces"; idCol = "interface_id"; nameCol = "interface_name"; }

        try {
            if ("save".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                
                boolean success;
                if (id > 0) {
                    success = dao.update(table, idCol, nameCol, id, name, "ACTIVE");
                    session.setAttribute("message", "Cập nhật thành công!");
                } else {
                    success = dao.insert(table, nameCol, name);
                    session.setAttribute("message", "Thêm mới thành công!");
                }
                if (!success) session.setAttribute("error", "Lỗi: Tên có thể đã bị trùng!");

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.softDelete(table, idCol, id);
                session.setAttribute("message", "Đã ẩn thành công!");
            }
        } catch (Exception e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/system-catalog?type=" + type);
    }
}