package minhtbth08199.service;

import fourSome.model.Parts;
import fourSome.model.PcBuildDetails;
import fourSome.model.PcBuilds;
import fourSome.url.DBConnect;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.List;
import java.util.Base64;
import minhtbth08199.dao.BuildPartsDAO;
import minhtbth08199.dao.PcBuildDetailsDAO;
import minhtbth08199.dao.PcBuildsDAO;

public class BuildPCService {
    private final PcBuildsDAO builds = new PcBuildsDAO();
    private final PcBuildDetailsDAO details = new PcBuildDetailsDAO();
    private final BuildPartsDAO parts = new BuildPartsDAO();

    public PcBuilds create(Integer userId, String name) {
        String safeName = name == null || name.trim().isEmpty() ? "Cấu hình mới" : name.trim();
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try { int id = builds.create(con, userId, safeName, token()); builds.updateTotal(con, id); con.commit(); return builds.findById(id); }
            catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { throw new IllegalStateException("Không thể tạo cấu hình", e); }
    }

    /** Giá luôn đọc lại từ DB, không nhận giá từ trình duyệt. */
    public PcBuilds setPart(int buildId, int partId, int quantity) {
        if (quantity < 1 || quantity > 32) throw new IllegalArgumentException("Số lượng linh kiện không hợp lệ");
        if (builds.findById(buildId) == null) throw new IllegalArgumentException("Cấu hình không tồn tại");
        Parts part = parts.findAvailableById(partId);
        if (part == null) throw new IllegalArgumentException("Linh kiện không tồn tại, đã ngừng bán hoặc hết hàng");
        double price = part.getSalePrice() != null ? part.getSalePrice() : part.getBasePrice();
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try { details.removeOtherPartsInCategory(con, buildId, part.getCategoryId(), partId); details.upsert(con, buildId, partId, quantity, price); builds.updateTotal(con, buildId); con.commit(); return builds.findById(buildId); }
            catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { throw new IllegalStateException("Không thể cập nhật cấu hình", e); }
    }

    public PcBuilds removePart(int buildId, int partId) {
        try (Connection con = DBConnect.getConnection()) { con.setAutoCommit(false); try { details.remove(con, buildId, partId); builds.updateTotal(con, buildId); con.commit(); return builds.findById(buildId); } catch (Exception e) { con.rollback(); throw e; } }
        catch (Exception e) { throw new IllegalStateException("Không thể xóa linh kiện", e); }
    }
    public PcBuilds get(int buildId) { return builds.findById(buildId); }
    public PcBuilds getShared(String token) { return token == null ? null : builds.findByShareToken(token); }
    public List<PcBuildDetails> getDetails(int buildId) { return details.findByBuildId(buildId); }
    public List<minhtbth08199.model.BuildPartItem> getSelectedParts(int buildId) { return parts.findSelectedByBuildId(buildId); }
    public boolean delete(int buildId, Integer userId) { return builds.softDelete(buildId, userId); }
    private String token() { byte[] bytes = new byte[24]; new SecureRandom().nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }

}
