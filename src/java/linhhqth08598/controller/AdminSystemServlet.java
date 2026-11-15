package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.BudgetTierRules;
import linhhqth08598.dao.BudgetTierRulesDAO;
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
import java.math.BigDecimal;

/**
 * UC 341–343: Cấu hình hệ thống.
 *  - type=psu     : ngưỡng công suất nguồn (psu_power_rules)
 *  - type=profile : tiêu chí hiệu năng gaming / đề xuất theo nhu cầu (usage_profiles)
 *  - type=budget  : tiêu chí đề xuất theo ngân sách (budget_tier_rules) - UC 343
 */
@WebServlet(name = "AdminSystemServlet", urlPatterns = {"/admin/system"})
public class AdminSystemServlet extends HttpServlet {

    private final PsuPowerRulesDAO psuDAO = new PsuPowerRulesDAO();
    private final UsageProfilesDAO profileDAO = new UsageProfilesDAO();
    private final BudgetTierRulesDAO budgetDAO = new BudgetTierRulesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = normalize(request.getParameter("type"));
        if ("psu".equals(type)) {
            request.setAttribute("psuRules", psuDAO.getAll());
        } else if ("budget".equals(type)) {
            request.setAttribute("budgetTiers", budgetDAO.getAll());
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
            } else if ("budget".equals(type)) {
                handleBudget(request, session, admin, audit, action);
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

    /** UC 343: thêm/sửa/bật-tắt khung ngân sách. */
    private void handleBudget(HttpServletRequest request, HttpSession session, Users admin, AuditLogService audit, String action) {
        if ("save".equals(action)) {
            int id = parseInt(request.getParameter("id"), 0);
            String name = trim(request.getParameter("tierName"));
            BigDecimal min = new BigDecimal(request.getParameter("minBudget").trim());
            String maxStr = trim(request.getParameter("maxBudget"));
            BigDecimal max = (maxStr == null || maxStr.isEmpty()) ? null : new BigDecimal(maxStr);
            int cpu = Integer.parseInt(request.getParameter("cpuPercent").trim());
            int gpu = Integer.parseInt(request.getParameter("gpuPercent").trim());
            int ram = Integer.parseInt(request.getParameter("ramPercent").trim());
            int sto = Integer.parseInt(request.getParameter("storagePercent").trim());
            int other = Integer.parseInt(request.getParameter("otherPercent").trim());

            String err = null;
            if (name == null || name.isEmpty()) err = "Tên khung ngân sách không được để trống.";
            else if (min.signum() < 0) err = "Ngân sách tối thiểu không được âm.";
            else if (max != null && max.compareTo(min) <= 0) err = "Ngân sách tối đa phải lớn hơn tối thiểu (để trống nếu không giới hạn).";
            else if (cpu < 0 || gpu < 0 || ram < 0 || sto < 0 || other < 0) err = "Tỉ lệ phân bổ không được âm.";
            else if (cpu + gpu + ram + sto + other != 100) err = "Tổng tỉ lệ phân bổ phải đúng 100% (hiện tại là " + (cpu + gpu + ram + sto + other) + "%).";
            else if (budgetDAO.overlaps(id, min, max)) err = "Khung ngân sách bị chồng lên một khung đang hoạt động khác.";

            if (err != null) {
                session.setAttribute("error", err);
                return;
            }
            BudgetTierRules t = new BudgetTierRules();
            t.setTierId(id);
            t.setTierName(name);
            t.setMinBudget(min);
            t.setMaxBudget(max);
            t.setCpuPercent(cpu);
            t.setGpuPercent(gpu);
            t.setRamPercent(ram);
            t.setStoragePercent(sto);
            t.setOtherPercent(other);
            t.setDescription(trim(request.getParameter("description")));
            boolean ok = id > 0 ? budgetDAO.update(t) : budgetDAO.insert(t);
            if (ok) {
                session.setAttribute("message", id > 0 ? "Đã cập nhật khung ngân sách." : "Đã thêm khung ngân sách.");
                audit.logAction(admin.getUserId(), id > 0 ? "UPDATE_BUDGET_TIER" : "INSERT_BUDGET_TIER", "budget_tier_rules", id, "Khung ngân sách: " + name);
            } else {
                session.setAttribute("error", "Không lưu được khung ngân sách.");
            }
        } else if ("toggle".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String next = "ACTIVE".equals(request.getParameter("status")) ? "INACTIVE" : "ACTIVE";
            if ("ACTIVE".equals(next)) {
                // bật lại thì phải kiểm tra chồng khung
                for (BudgetTierRules t : budgetDAO.getAll()) {
                    if (t.getTierId() == id && budgetDAO.overlaps(id, t.getMinBudget(), t.getMaxBudget())) {
                        session.setAttribute("error", "Không bật lại được: khung này chồng lên một khung đang hoạt động.");
                        return;
                    }
                }
            }
            if (budgetDAO.setStatus(id, next)) {
                session.setAttribute("message", "ACTIVE".equals(next) ? "Đã bật lại khung ngân sách." : "Đã tắt khung ngân sách.");
                audit.logAction(admin.getUserId(), "TOGGLE_BUDGET_TIER", "budget_tier_rules", id, "Đổi trạng thái sang " + next);
            }
        }
    }

    private String normalize(String type) {
        if ("psu".equals(type)) return "psu";
        if ("budget".equals(type)) return "budget";
        return "profile";
    }

    private String trim(String s) { return s == null ? null : s.trim(); }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}
