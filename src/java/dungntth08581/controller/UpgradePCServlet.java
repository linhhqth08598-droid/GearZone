package dungntth08581.controller;

import dungntth08581.service.UpgradePCService;
import fourSome.model.PcBuilds;
import fourSome.model.UserCurrentPcs;
import fourSome.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UpgradePCServlet", urlPatterns = {"/upgrade-pc"})
public class UpgradePCServlet extends HttpServlet {

    private final UpgradePCService upgradeService = new UpgradePCService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/upgrade-pc");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        request.setAttribute("cpuList", upgradeService.getPartsByCategory(1));
        request.setAttribute("mainList", upgradeService.getPartsByCategory(2));
        request.setAttribute("ramList", upgradeService.getPartsByCategory(3));
        request.setAttribute("vgaList", upgradeService.getPartsByCategory(4));
        request.setAttribute("ssdList", upgradeService.getPartsByCategory(5));
        request.setAttribute("psuList", upgradeService.getPartsByCategory(7));
        request.setAttribute("caseList", upgradeService.getPartsByCategory(8));
        request.setAttribute("coolerList", upgradeService.getPartsByCategory(9));
        request.setAttribute("buildHistoryList", upgradeService.getUserBuildHistory(user.getUserId()));

        UserCurrentPcs currentPc = upgradeService.getActiveCurrentPc(user.getUserId());
        request.setAttribute("currentPc", currentPc);
        if (currentPc != null) {
            request.setAttribute("analysis", upgradeService.analyzePc(currentPc));
        }
        request.getRequestDispatcher("/views/minhtbth08199/buildpc/upgrade.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/upgrade-pc");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "saveCurrentPc";

        try {
            if ("fromHistory".equals(action)) {
                Integer histBuildId = parsePositiveInt(request.getParameter("historyBuildId"));
                if (histBuildId == null) {
                    session.setAttribute("error", "Vui lòng chọn một cấu hình trong lịch sử build.");
                    response.sendRedirect(request.getContextPath() + "/upgrade-pc");
                    return;
                }
                double histBudget = 0.0;
                String rawHistBudget = request.getParameter("historyUpgradeBudget");
                if (rawHistBudget != null && !rawHistBudget.trim().isEmpty()) {
                    histBudget = Double.parseDouble(rawHistBudget.trim().replaceAll("[\\s.,_₫đĐ]", ""));
                }
                if (histBudget < 0 || histBudget > 500_000_000) {
                    session.setAttribute("error", "Ngân sách nâng cấp phải từ 0 đến 500.000.000 đồng.");
                    response.sendRedirect(request.getContextPath() + "/upgrade-pc");
                    return;
                }
                int savedId = upgradeService.applyBuildFromHistory(user.getUserId(), histBuildId, histBudget);
                if (savedId > 0) {
                    session.setAttribute("message", "Đã lấy cấu hình từ lịch sử build #" + histBuildId + " làm PC hiện tại (#" + savedId + ") và phân tích 3 phương án nâng cấp.");
                } else {
                    session.setAttribute("error", "Không thể lấy cấu hình từ lịch sử build, vui lòng thử lại.");
                }
                response.sendRedirect(request.getContextPath() + "/upgrade-pc");
                return;
            }

            if ("applyPlan".equals(action)) {
                String planType = request.getParameter("planType");
                boolean addToCart = "true".equals(request.getParameter("addToCart")) || request.getParameter("addToCart") != null;
                PcBuilds created = upgradeService.applyPlanAndCreateBuild(user.getUserId(), planType, addToCart);
                session.setAttribute("message", "Đã tạo cấu hình nâng cấp '" + created.getBuildName() + "'"
                        + (addToCart ? " và thêm linh kiện nâng cấp vào giỏ hàng." : "."));
                response.sendRedirect(request.getContextPath() + "/build-pc?buildId=" + created.getBuildId());
                return;
            }

            UserCurrentPcs pc = new UserCurrentPcs();
            pc.setUserId(user.getUserId());
            pc.setCurrentCpuId(parsePositiveInt(request.getParameter("currentCpuId")));
            pc.setCurrentMainboardId(parsePositiveInt(request.getParameter("currentMainboardId")));
            pc.setCurrentRamId(parsePositiveInt(request.getParameter("currentRamId")));
            pc.setCurrentVgaId(parsePositiveInt(request.getParameter("currentVgaId")));
            pc.setCurrentPsuId(parsePositiveInt(request.getParameter("currentPsuId")));
            pc.setCurrentCaseId(parsePositiveInt(request.getParameter("currentCaseId")));
            pc.setCurrentStorageId(parsePositiveInt(request.getParameter("currentStorageId")));
            pc.setCurrentCoolerId(parsePositiveInt(request.getParameter("currentCoolerId")));

            Integer ramQty = parsePositiveInt(request.getParameter("currentRamQuantity"));
            pc.setCurrentRamQuantity(ramQty != null ? Math.min(ramQty, 32) : 1);

            String rawBudget = request.getParameter("upgradeBudget");
            double budget = 0.0;
            if (rawBudget != null && !rawBudget.trim().isEmpty()) {
                budget = Double.parseDouble(rawBudget.trim().replaceAll("[\\s.,_₫đĐ]", ""));
            }
            if (budget < 0 || budget > 500_000_000) {
                session.setAttribute("error", "Ngân sách nâng cấp phải từ 0 đến 500.000.000 đồng.");
                response.sendRedirect(request.getContextPath() + "/upgrade-pc");
                return;
            }
            pc.setUpgradeBudget(budget);

            int savedId = upgradeService.saveCurrentPc(pc);
            if (savedId > 0) {
                session.setAttribute("message", "Đã lưu cấu hình PC hiện tại (#" + savedId + ") và phân tích 3 phương án nâng cấp.");
            } else {
                session.setAttribute("error", "Không thể lưu cấu hình PC hiện tại, vui lòng thử lại.");
            }
        } catch (NumberFormatException ex) {
            session.setAttribute("error", "Dữ liệu số hoặc ngân sách nâng cấp không hợp lệ.");
        } catch (IllegalArgumentException ex) {
            session.setAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý nâng cấp PC.");
        }
        response.sendRedirect(request.getContextPath() + "/upgrade-pc");
    }

    private Integer parsePositiveInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            int val = Integer.parseInt(raw.trim());
            return val > 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
