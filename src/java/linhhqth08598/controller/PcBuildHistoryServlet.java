package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.PcBuildHistoryItem;
import linhhqth08598.dao.UserWishlistsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PcBuildHistoryServlet", urlPatterns = {"/user/pc-build-history"})
public class PcBuildHistoryServlet extends HttpServlet {

    private UserWishlistsDAO dao = new UserWishlistsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<PcBuildHistoryItem> history = dao.getPcBuildHistory(user.getUserId());
        request.setAttribute("buildHistoryList", history);

        request.getRequestDispatcher("/views/linhhqth08598/user/pc_build_history.jsp").forward(request, response);
    }
}