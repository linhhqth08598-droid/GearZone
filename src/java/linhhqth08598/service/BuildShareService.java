package linhhqth08598.service;

import fourSome.model.PcBuildHistoryItem;
import fourSome.util.Validator;
import linhhqth08598.dao.PcBuildShareDAO;

/**
 * UC 16 (Lưu cấu hình PC) và UC 17 (Chia sẻ cấu hình qua link).
 * Các hàm trả về null = thành công, ngược lại là thông báo lỗi tiếng Việt.
 * minhtbth08199 có thể gọi saveBuild() từ trang Build PC khi người dùng bấm "Lưu cấu hình".
 */
public class BuildShareService {

    private final PcBuildShareDAO dao = new PcBuildShareDAO();

    public static boolean isValidToken(String token) {
        return token != null && token.matches("^[A-Za-z0-9]{16,100}$");
    }

    public String saveBuild(int buildId, int userId, String name) {
        String err = validateName(name);
        if (err != null) return err;
        PcBuildHistoryItem b = dao.findOwned(buildId, userId);
        if (b == null) return "Không tìm thấy cấu hình!";
        if (b.getDetails() == null || b.getDetails().isEmpty()) return "Cấu hình chưa có linh kiện nào, không thể lưu!";
        return dao.saveBuild(buildId, userId, name.trim()) ? null : "Không lưu được cấu hình!";
    }

    public String rename(int buildId, int userId, String name) {
        String err = validateName(name);
        if (err != null) return err;
        return dao.rename(buildId, userId, name.trim()) ? null : "Không đổi được tên cấu hình!";
    }

    public String delete(int buildId, int userId) {
        return dao.softDelete(buildId, userId) ? null : "Không xóa được cấu hình!";
    }

    /** Trả về token mới hoặc null. */
    public String regenerateToken(int buildId, int userId) {
        return dao.regenerateToken(buildId, userId);
    }

    /** Sao chép cấu hình của chính mình. Trả về build_id mới (0 nếu lỗi). */
    public int duplicateOwned(int buildId, int userId) {
        PcBuildHistoryItem b = dao.findOwned(buildId, userId);
        if (b == null) return 0;
        return dao.duplicate(buildId, userId, truncate(b.getBuildName() + " (bản sao)"));
    }

    public PcBuildHistoryItem getByToken(String token) {
        return isValidToken(token) ? dao.findByToken(token) : null;
    }

    public String getOwnerName(int buildId) {
        return dao.getOwnerName(buildId);
    }

    /** Lưu cấu hình từ link chia sẻ về tài khoản của người đang đăng nhập. */
    public int copyFromShared(String token, int userId) {
        PcBuildHistoryItem b = getByToken(token);
        if (b == null) return 0;
        return dao.duplicate(b.getBuildId(), userId, truncate(b.getBuildName() + " (từ link chia sẻ)"));
    }

    private String validateName(String name) {
        if (Validator.isBlank(name)) return "Vui lòng nhập tên cấu hình!";
        if (name.trim().length() > 150) return "Tên cấu hình tối đa 150 ký tự!";
        return null;
    }

    private String truncate(String s) {
        return s.length() > 150 ? s.substring(0, 150) : s;
    }
}
