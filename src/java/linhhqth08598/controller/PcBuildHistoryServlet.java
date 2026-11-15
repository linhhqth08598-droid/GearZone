package linhhqth08598.controller;

import fourSome.model.PcBuildHistoryItem;
import fourSome.model.Users;
import linhhqth08598.dao.UserWishlistsDAO;
import linhhqth08598.service.BuildShareService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/** UC 15 Lịch sử cấu hình PC + UC 16 Lưu cấu hình + UC 17 Chia sẻ qua link. */
@WebServlet(name = "PcBuildHistoryServlet", urlPatterns = {"/user/pc-build-history"})
public class PcBuildHistoryServlet extends HttpServlet {

    private final UserWishlistsDAO dao = new UserWishlistsDAO();
    private final BuildShareService shareService = new BuildShareService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        List<PcBuildHistoryItem> history = dao.getPcBuildHistory(user.getUserId());
        request.setAttribute("buildHistoryList", history);
        request.getRequestDispatcher("/views/linhhqth08598/user/pc_build_history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("buildId");
        if (action == null || idParam == null || !idParam.matches("\\d{1,9}")) {
            session.setAttribute("error", "Yêu cầu không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/user/pc-build-history");
            return;
        }
        int buildId = Integer.parseInt(idParam);
        int userId = user.getUserId();
        String openShare = ""; // sau khi tạo link mới thì mở sẵn hộp chia sẻ

        switch (action) {
            case "save": {            // UC 16
                String err = shareService.saveBuild(buildId, userId, request.getParameter("buildName"));
                flash(session, err, "Đã lưu cấu hình thành công!");
                break;
            }
            case "rename": {
                String err = shareService.rename(buildId, userId, request.getParameter("buildName"));
                flash(session, err, "Đã đổi tên cấu hình!");
                break;
            }
            case "delete": {
                String err = shareService.delete(buildId, userId);
                flash(session, err, "Đã xóa cấu hình!");
                break;
            }
            case "duplicate": {
                int newId = shareService.duplicateOwned(buildId, userId);
                flash(session, newId > 0 ? null : "Không sao chép được cấu hình!", "Đã sao chép cấu hình thành công!");
                break;
            }
            case "regenerate": {      // UC 17: đổi link, link cũ hết hiệu lực
                String token = shareService.regenerateToken(buildId, userId);
                flash(session, token != null ? null : "Không tạo được link mới!", "Đã tạo link chia sẻ mới, link cũ không còn hiệu lực.");
                if (token != null) openShare = "?share=" + buildId;
                break;
            }
            default:
                session.setAttribute("error", "Thao tác không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/user/pc-build-history" + openShare);
    }

    private void flash(HttpSession session, String error, String okMessage) {
        if (error == null) session.setAttribute("message", okMessage);
        else session.setAttribute("error", error);
    }
}
