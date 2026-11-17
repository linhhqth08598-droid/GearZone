package khanhnb08894.controller;

import fourSome.model.Suppliers;
import khanhnb08894.dao.SuppliersDAO;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminSupplierServlet",
        urlPatterns = {"/admin/supplier",
    "/admin/suppliers"})
public class AdminSupplierServlet extends HttpServlet {

    private SuppliersDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        supplierDAO = new SuppliersDAO();
    }

    // Hiển thị danh sách nhà cung cấp
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");

        try {

            List<Suppliers> suppliers =
                    supplierDAO.getAll(keyword, status);

            request.setAttribute("suppliers", suppliers);
            request.setAttribute("paramKeyword",
                    keyword == null ? "" : keyword);
            request.setAttribute("paramStatus",
                    status == null ? "" : status);

            request.getRequestDispatcher(
                    "/views/khanhnb08894/admin/suppliers.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            log("Lỗi tải danh sách nhà cung cấp", e);
            throw new ServletException(
                    "Không thể tải danh sách nhà cung cấp.", e);
        }
    }

    // Thêm và cập nhật nhà cung cấp
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        try {

            if ("save".equals(action)) {

                Suppliers s = readSupplier(request);

                if (isBlank(s.getSupplierName())
                        || isBlank(s.getPhone())) {

                    session.setAttribute("error",
                            "Vui lòng nhập tên và số điện thoại!");

                } else {

                    s.setStatus("ACTIVE");

                    if (supplierDAO.insertSupplier(s)) {
                        session.setAttribute("message",
                                "Thêm nhà cung cấp thành công!");
                    } else {
                        session.setAttribute("error",
                                "Không thể thêm nhà cung cấp!");
                    }
                }

            } else if ("update".equals(action)) {

                String id = request.getParameter("supplierId");

                if (id == null || !id.matches("\\d+")) {

                    session.setAttribute("error",
                            "Mã nhà cung cấp không hợp lệ!");

                } else {

                    Suppliers s = readSupplier(request);

                    s.setSupplierId(Integer.parseInt(id));

                    String status = request.getParameter("status");

                    s.setStatus("INACTIVE".equals(status)
                            ? "INACTIVE" : "ACTIVE");

                    if (isBlank(s.getSupplierName())
                            || isBlank(s.getPhone())) {

                        session.setAttribute("error",
                                "Vui lòng nhập tên và số điện thoại!");

                    } else if (supplierDAO.updateSupplier(s)) {

                        session.setAttribute("message",
                                "Cập nhật nhà cung cấp thành công!");

                    } else {

                        session.setAttribute("error",
                                "Không tìm thấy nhà cung cấp!");
                    }
                }

            } else if ("delete".equals(action)) {

                String id = request.getParameter("supplierId");

                if (id == null || !id.matches("\\d+")) {

                    session.setAttribute("error",
                            "Mã nhà cung cấp không hợp lệ!");

                } else {

                    int supplierId = Integer.parseInt(id);
                    int refs = supplierDAO.countReferences(supplierId);

                    if (refs > 0) {
                        session.setAttribute("error",
                                "Không thể xóa: nhà cung cấp này đã có " + refs
                                + " phiếu nhập/trả hàng. Hãy chuyển sang \"Ngừng hợp tác\" bằng nút Sửa.");
                    } else if (supplierDAO.deleteSupplier(supplierId)) {
                        session.setAttribute("message",
                                "Đã xóa nhà cung cấp!");
                    } else {
                        session.setAttribute("error",
                                "Không tìm thấy nhà cung cấp!");
                    }
                }

            } else {

                session.setAttribute("error",
                        "Thao tác không hợp lệ!");
            }

        } catch (Exception e) {

            log("Lỗi xử lý nhà cung cấp", e);

            session.setAttribute("error",
                    "Có lỗi xảy ra khi xử lý dữ liệu!");
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/suppliers");
    }

    // Đọc dữ liệu từ form
    private Suppliers readSupplier(HttpServletRequest request) {

        Suppliers s = new Suppliers();

        s.setSupplierName(trim(
                request.getParameter("supplierName")));

        s.setContactName(trim(
                request.getParameter("contactName")));

        s.setPhone(trim(
                request.getParameter("phone")));

        s.setEmail(trim(
                request.getParameter("email")));

        s.setAddress(trim(
                request.getParameter("address")));

        return s;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}