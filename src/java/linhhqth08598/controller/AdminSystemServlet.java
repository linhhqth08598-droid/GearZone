package linhhqth08598.controller;

import fourSome.model.Users;
import linhhqth08598.dao.PsuPowerRulesDAO;
import linhhqth08598.dao.UsageProfilesDAO;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * UC 341–343: Cấu hình hệ thống.
 *  - type=psu     : ngưỡng công suất nguồn (psu_power_rules)
 *  - type=profile : tiêu chí hiệu năng gaming / đề xuất theo nhu cầu (usage_profiles)
 */
@WebServlet(name = "AdminSystemServlet", urlPatterns = {"/admin/system"})
public class AdminSystemServlet extends HttpServlet {

    private final PsuPowerRulesDAO psuDAO = new PsuPowerRulesDAO();
    private final UsageProfilesDAO profileDAO = new UsageProfilesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = normalize(request.getParameter("type"));
        if ("psu".equals(type)) {
            request.setAttribute("psuRules", psuDAO.getAll());
        } else {
            request.setAttribute("profiles", profileDAO.getAll());
        }
        request.setAttribute("currentType", type);
        request.getRequestDispatcher("/views/linhhqth08598/admin/system.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String type = normalize(request.getParameter("type"));
        String action = request.getParameter("action");
        AuditLogService audit = new AuditLogService();

        try {
            if ("psu".equals(type)) {
                if ("save".equals(action)) {
                    int id = parseInt(request.getParameter("id"), 0);
                    int watt = Integer.parseInt(request.getParameter("minSystemWattage").trim());
                    double headroom = Double.parseDouble(request.getParameter("headroomPercent").trim());
                    String desc = trim(request.getParameter("description"));
                    if (watt <= 0 || headroom < 0 || headroom > 100) {
                        session.setAttribute("error", "Công suất phải lớn hơn 0 và hệ số dự phòng từ 0 đến 100%.");
                    } else {
                        boolean ok = id > 0 ? psuDAO.update(id, watt, headroom, desc) : psuDAO.insert(watt, headroom, desc);
                        if (ok) {
                            session.setAttribute("message", id > 0 ? "Đã cập nhật ngưỡng công suất." : "Đã thêm ngưỡng công suất.");
                            audit.logAction(admin.getUserId(), id > 0 ? "UPDATE_PSU_RULE" : "INSERT_PSU_RULE", "psu_power_rules", id,
                                    "Ngưỡng nguồn >= " + watt + "W, dự phòng " + headroom + "%");
                        } else {
                            session.setAttribute("error", "Không lưu được ngưỡng công suất.");
                        }
                    }
                } else if ("toggle".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String next = "ACTIVE".equals(request.getParameter("status")) ? "INACTIVE" : "ACTIVE";
                    if (psuDAO.setStatus(id, next)) {
                        session.setAttribute("message", "ACTIVE".equals(next) ? "Đã bật lại ngưỡng." : "Đã tắt ngưỡng.");
                        audit.logAction(admin.getUserId(), "TOGGLE_PSU_RULE", "psu_power_rules", id, "Đổi trạng thái sang " + next);
                    }
                }
            } else {
                if ("save".equals(action)) {
                    int id = parseInt(request.getParameter("id"), 0);
                    String name = trim(request.getParameter("profileName"));
                    String games = trim(request.getParameter("targetGameList"));
                    String res = trim(request.getParameter("targetResolution"));
                    String fps = trim(request.getParameter("targetFps"));
                    if (name == null || name.isEmpty()) {
                        session.setAttribute("error", "Tên tiêu chí không được để trống.");
                    } else {
                        boolean ok = id > 0 ? profileDAO.update(id, name, games, res, fps) : profileDAO.insert(name, games, res, fps);
                        if (ok) {
                            session.setAttribute("message", id > 0 ? "Đã cập nhật tiêu chí." : "Đã thêm tiêu chí.");
                            audit.logAction(admin.getUserId(), id > 0 ? "UPDATE_USAGE_PROFILE" : "INSERT_USAGE_PROFILE", "usage_profiles", id, "Tiêu chí: " + name);
                        } else {
                            session.setAttribute("error", "Không lưu được tiêu chí.");
                        }
                    }
                } else if ("toggle".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String next = "ACTIVE".equals(request.getParameter("status")) ? "INACTIVE" : "ACTIVE";
                    if (profileDAO.setStatus(id, next)) {
                        session.setAttribute("message", "ACTIVE".equals(next) ? "Đã bật lại tiêu chí." : "Đã tắt tiêu chí.");
                        audit.logAction(admin.getUserId(), "TOGGLE_USAGE_PROFILE", "usage_profiles", id, "Đổi trạng thái sang " + next);
                    }
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu số không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/system?type=" + type);
    }

    private String normalize(String type) { return "psu".equals(type) ? "psu" : "profile"; }

    private String trim(String s) { return s == null ? null : s.trim(); }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}
