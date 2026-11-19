package fourSome.controller;

import fourSome.model.Users;
import fourSome.url.DBConnect;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/sync-state")
public class SyncStateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        HttpSession session = req.getSession(false);
        int userId = 0;
        int cartCount = 0;

        if (session != null) {
            Users user = (Users) session.getAttribute("loggedUser");
            if (user != null) userId = user.getUserId();
            Object c = session.getAttribute("cartCount");
            if (c instanceof Integer) cartCount = (Integer) c;
        }

        int unreadCount = 0;
        int latestOrderId = 0;
        int latestPartId = 0;

        String sql = "SELECT "
                   + "(SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0) AS unread_cnt, "
                   + "(SELECT ISNULL(MAX(order_id), 0) FROM orders) AS max_order, "
                   + "(SELECT ISNULL(MAX(part_id), 0) FROM parts) AS max_part";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    unreadCount = rs.getInt("unread_cnt");
                    latestOrderId = rs.getInt("max_order");
                    latestPartId = rs.getInt("max_part");
                }
            }
            if (session != null && userId > 0) {
                session.setAttribute("unreadCount", unreadCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.getWriter().write(String.format(
            "{\"unreadCount\":%d,\"cartCount\":%d,\"latestOrderId\":%d,\"latestPartId\":%d}",
            unreadCount, cartCount, latestOrderId, latestPartId
        ));
    }
}
