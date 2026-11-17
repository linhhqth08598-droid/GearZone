package khanhnb08894.controller;

import khanhnb08894.dao.PurchaseOrdersDAO;
import khanhnb08894.dao.SuppliersDAO;
import fourSome.model.PurchaseOrders;
import fourSome.model.Suppliers;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminPurchaseServlet", urlPatterns = {"/admin/purchase", "/admin/purchases"})
public class AdminPurchaseServlet extends HttpServlet {

    private PurchaseOrdersDAO poDAO;
    private SuppliersDAO suppliersDAO;

    @Override
    public void init() throws ServletException {
        poDAO = new PurchaseOrdersDAO();
        suppliersDAO = new SuppliersDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<PurchaseOrders> purchases = poDAO.getAll();
        List<Suppliers> suppliers = suppliersDAO.getAll();
        
        request.setAttribute("purchases", purchases);
        request.setAttribute("suppliers", suppliers);
        
        request.getRequestDispatcher("/views/khanhnb08894/admin/purchases.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("save".equals(action)) {
                PurchaseOrders po = new PurchaseOrders();
                po.setPoCode(request.getParameter("poCode"));
                po.setSupplierId(Integer.parseInt(request.getParameter("supplierId")));
                
                String eta = request.getParameter("expectedDeliveryDate");
                if (eta != null && !eta.isEmpty()) {
                    java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(eta);
                    po.setExpectedDeliveryDate(parsed);
                }

                if (poDAO.insert(po)) {
                    session.setAttribute("message", "Tạo Đơn nhập hàng thành công!");
                } else {
                    session.setAttribute("error", "Lỗi khi tạo đơn nhập hàng.");
                }
                
            } else if ("updateStatus".equals(action)) {
                int poId = Integer.parseInt(request.getParameter("poId"));
                String status = request.getParameter("status");

                if (poDAO.updateStatus(poId, status)) {
                    session.setAttribute("message", "Cập nhật trạng thái thành công!");
                } else {
                    session.setAttribute("error", "Lỗi khi cập nhật trạng thái.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/purchases");
    }
}   