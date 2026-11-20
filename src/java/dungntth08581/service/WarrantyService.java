package dungntth08581.service;

import dungntth08581.dao.EWarrantiesDAO;
import dungntth08581.dao.WarrantyClaimLogsDAO;
import dungntth08581.dao.WarrantyClaimsDAO;
import fourSome.model.EWarranties;
import fourSome.model.WarrantyClaimLogs;
import fourSome.model.WarrantyClaims;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WarrantyService {

    private final EWarrantiesDAO eWarrantiesDAO = new EWarrantiesDAO();
    private final WarrantyClaimsDAO claimsDAO = new WarrantyClaimsDAO();
    private final WarrantyClaimLogsDAO logsDAO = new WarrantyClaimLogsDAO();

    // =========================================================================
    // UC 271: Thiết lập thời hạn bảo hành theo loại linh kiện / linh kiện
    // =========================================================================
    public String setWarrantyMonthsForCategory(int staffId, int categoryId, int warrantyMonths) {
        if (warrantyMonths < 0 || warrantyMonths > 120) {
            return "Thời hạn bảo hành phải từ 0 đến 120 tháng.";
        }
        try {
            int updated = eWarrantiesDAO.updateWarrantyMonthsByCategory(categoryId, warrantyMonths);
            logStaffAudit(staffId, "UPDATE_WARRANTY_POLICY", "parts", categoryId,
                    "Cập nhật hạn bảo hành " + warrantyMonths + " tháng cho danh mục #" + categoryId + " (" + updated + " linh kiện)");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi cập nhật hạn bảo hành.";
        }
    }

    public String setWarrantyMonthsForPart(int staffId, int partId, int warrantyMonths) {
        if (warrantyMonths < 0 || warrantyMonths > 120) {
            return "Thời hạn bảo hành phải từ 0 đến 120 tháng.";
        }
        try {
            boolean ok = eWarrantiesDAO.updateWarrantyMonthsByPart(partId, warrantyMonths);
            if (!ok) {
                return "Không tìm thấy linh kiện #" + partId;
            }
            logStaffAudit(staffId, "UPDATE_WARRANTY_POLICY", "parts", partId,
                    "Cập nhật hạn bảo hành " + warrantyMonths + " tháng cho linh kiện #" + partId);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi hệ thống khi cập nhật hạn bảo hành.";
        }
    }

    // =========================================================================
    // UC 272: Phát hành phiếu bảo hành điện tử cho đơn hàng đã hoàn tất
    // =========================================================================
    public List<EWarranties> issueWarrantiesForOrder(int orderId, Integer staffId) throws Exception {
        List<EWarranties> existing = eWarrantiesDAO.findByOrderId(orderId);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<EWarranties> issued = new ArrayList<>();
        String itemSql = "SELECT oi.order_item_id, oi.part_id, oi.serial_number, oi.quantity, "
                + "p.warranty_months, o.user_id, o.order_code "
                + "FROM order_items oi "
                + "JOIN parts p ON oi.part_id = p.part_id "
                + "JOIN orders o ON oi.order_id = o.order_id "
                + "WHERE oi.order_id = ?";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                ps.setInt(1, orderId);
                int userId = -1;
                String orderCode = "";
                try (ResultSet rs = ps.executeQuery()) {
                    Date now = new Date();
                    String dateTag = new SimpleDateFormat("yyyyMMdd").format(now);
                    int seq = 1;
                    while (rs.next()) {
                        userId = rs.getInt("user_id");
                        orderCode = rs.getString("order_code");
                        int partId = rs.getInt("part_id");
                        int qty = Math.max(1, rs.getInt("quantity"));
                        int months = rs.getInt("warranty_months");
                        String baseSerial = rs.getString("serial_number");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        cal.add(Calendar.MONTH, months > 0 ? months : 12);
                        Date endDate = cal.getTime();

                        java.util.List<String> assigned = khanhnb08894.dao.PartSerialsDAO.splitSerials(baseSerial);
                        for (int i = 0; i < qty; i++) {
                            EWarranties w = new EWarranties();
                            String code = "EW-" + dateTag + "-" + orderId + "-" + (seq++);
                            // Ưu tiên đúng serial đã gán cho đơn; chỉ khi thiếu mới sinh mã dự phòng
                            String serial = i < assigned.size()
                                    ? assigned.get(i)
                                    : "SN-" + orderId + "-" + partId + "-" + (i + 1);
                            w.setWarrantyCode(code);
                            w.setSerialNumber(serial);
                            w.setPartId(partId);
                            w.setOrderId(orderId);
                            w.setStartDate(now);
                            w.setEndDate(endDate);
                            w.setStatus("ACTIVE");
                            w.setDocumentUrl("/warranty-lookup?keyword=" + code);

                            int newId = eWarrantiesDAO.insert(conn, w);
                            w.setWarrantyId(newId);
                            issued.add(w);
                        }
                    }
                }
                if (issued.isEmpty()) {
                    conn.rollback();
                    throw new IllegalStateException("Đơn hàng #" + orderId + " không có linh kiện để phát hành bảo hành.");
                }
                conn.commit();

                if (userId > 0) {
                    sendNotification(userId, "WARRANTY", "Phát hành phiếu bảo hành điện tử",
                            "Đơn hàng " + orderCode + " đã được kích hoạt " + issued.size() + " phiếu bảo hành điện tử.",
                            "ORDER", orderId);
                }
                if (staffId != null) {
                    logStaffAudit(staffId, "ISSUE_EWARRANTY", "e_warranties", orderId,
                            "Phát hành " + issued.size() + " phiếu bảo hành cho đơn hàng #" + orderId);
                }
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        }
        return issued;
    }

    // =========================================================================
    // UC 273 & 274: Tra cứu bảo hành công khai theo mã SP / số Serial / mã BH
    // =========================================================================
    public List<HashMap<String, Object>> lookupPublicWarranty(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<HashMap<String, Object>> results = eWarrantiesDAO.searchPublic(keyword.trim());
        Date now = new Date();
        for (HashMap<String, Object> row : results) {
            Date end = (Date) row.get("endDate");
            boolean isValid = "ACTIVE".equalsIgnoreCase((String) row.get("status"))
                    && end != null && !end.before(now);
            row.put("isValid", isValid);
        }
        return results;
    }

    public HashMap<String, Object> lookupPartPolicy(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return eWarrantiesDAO.findPartWarrantyPolicy(keyword.trim());
    }

    public List<HashMap<String, Object>> getUserWarranties(int userId) {
        return eWarrantiesDAO.findByUserId(userId);
    }

    // =========================================================================
    // UC 275: Khách hàng gửi yêu cầu bảo hành
    // =========================================================================
    public String createClaim(int userId, int warrantyId, String faultDescription) {
        if (faultDescription == null || faultDescription.trim().length() < 10) {
            return "Vui lòng mô tả tình trạng lỗi ít nhất 10 ký tự.";
        }
        EWarranties w = eWarrantiesDAO.findById(warrantyId);
        if (w == null) {
            return "Không tìm thấy phiếu bảo hành.";
        }

        if (!isWarrantyOwnedByUser(warrantyId, userId)) {
            return "Bạn không có quyền gửi yêu cầu cho phiếu bảo hành này.";
        }
        if (!"ACTIVE".equalsIgnoreCase(w.getStatus()) || w.getEndDate().before(new Date())) {
            return "Phiếu bảo hành đã hết hạn hoặc không còn hiệu lực.";
        }
        if (claimsDAO.hasActiveClaim(warrantyId)) {
            return "Phiếu bảo hành này đang có một yêu cầu khác chưa hoàn tất.";
        }

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Date now = new Date();
                WarrantyClaims c = new WarrantyClaims();
                c.setWarrantyId(warrantyId);
                c.setUserId(userId);
                c.setFaultDescription(faultDescription.trim());
                c.setStatus("PENDING");
                c.setCreatedAt(now);

                int claimId = claimsDAO.insert(conn, c);
                addClaimLog(conn, claimId, "PENDING", "Khách hàng gửi yêu cầu bảo hành.", userId);
                conn.commit();

                sendNotification(userId, "WARRANTY", "Đã tiếp nhận yêu cầu bảo hành #" + claimId,
                        "Yêu cầu bảo hành cho phiếu " + w.getWarrantyCode() + " đã được gửi thành công.",
                        "WARRANTY_CLAIM", claimId);
                return null;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return "Lỗi khi tạo yêu cầu bảo hành: " + ex.getMessage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối cơ sở dữ liệu.";
        }
    }

    // =========================================================================
    // UC 276–280: Tiếp nhận, ghi tình trạng, kiểm tra điều kiện, duyệt / từ chối
    // =========================================================================
    public String receiveAndInspectClaim(int claimId, int staffId, String receivedCondition,
            boolean eligible, String rejectionReason) {
        WarrantyClaims c = claimsDAO.findById(claimId);
        if (c == null) {
            return "Không tìm thấy yêu cầu bảo hành #" + claimId;
        }
        if (!"PENDING".equalsIgnoreCase(c.getStatus()) && !"RECEIVED".equalsIgnoreCase(c.getStatus())) {
            return "Trạng thái hiện tại (" + c.getStatus() + ") không thể tiếp nhận/duyệt lại.";
        }
        if (receivedCondition == null || receivedCondition.trim().isEmpty()) {
            return "Vui lòng ghi nhận tình trạng linh kiện khi tiếp nhận.";
        }
        if (!eligible && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            return "Bắt buộc nhập lý do khi từ chối bảo hành.";
        }

        String newStatus = eligible ? "APPROVED" : "REJECTED";
        c.setReceivedCondition(receivedCondition.trim());
        c.setHandledBy(staffId);
        c.setStatus(newStatus);
        if (!eligible) {
            c.setRejectionReason(rejectionReason.trim());
            c.setCompletedAt(new Date());
        } else {
            c.setRejectionReason(null);
        }

        String logNote = eligible
                ? "Tiếp nhận (" + receivedCondition.trim() + ") – Đủ điều kiện bảo hành."
                : "Từ chối bảo hành. Tình trạng: " + receivedCondition.trim() + ". Lý do: " + rejectionReason.trim();

        return saveClaimTransition(c, staffId, logNote,
                eligible ? "Yêu cầu bảo hành #" + claimId + " đã được duyệt" : "Yêu cầu bảo hành #" + claimId + " bị từ chối",
                logNote);
    }

    // =========================================================================
    // UC 281–282: Chuyển trung tâm bảo hành & Cập nhật tiến độ
    // =========================================================================
    public String sendToServiceCenter(int claimId, int staffId, String serviceCenterName, String note) {
        WarrantyClaims c = claimsDAO.findById(claimId);
        if (c == null) {
            return "Không tìm thấy yêu cầu bảo hành #" + claimId;
        }
        if (!"APPROVED".equalsIgnoreCase(c.getStatus())) {
            return "Chỉ chuyển trung tâm bảo hành khi yêu cầu đã được duyệt (APPROVED).";
        }
        if (serviceCenterName == null || serviceCenterName.trim().isEmpty()) {
            return "Vui lòng nhập tên trung tâm bảo hành / hãng.";
        }

        c.setServiceCenterName(serviceCenterName.trim());
        c.setHandledBy(staffId);
        c.setStatus("AT_SERVICE_CENTER");

        String logNote = "Chuyển đến trung tâm bảo hành: " + serviceCenterName.trim()
                + (note != null && !note.trim().isEmpty() ? " – " + note.trim() : "");
        return saveClaimTransition(c, staffId, logNote,
                "Cập nhật bảo hành #" + claimId, logNote);
    }

    public String updateProgress(int claimId, int staffId, String newStatus, String note) {
        WarrantyClaims c = claimsDAO.findById(claimId);
        if (c == null) {
            return "Không tìm thấy yêu cầu bảo hành #" + claimId;
        }
        if ("COMPLETED".equalsIgnoreCase(c.getStatus()) || "REJECTED".equalsIgnoreCase(c.getStatus())) {
            return "Yêu cầu bảo hành đã đóng, không thể cập nhật tiến độ.";
        }
        if (note == null || note.trim().isEmpty()) {
            return "Vui lòng nhập ghi chú tiến độ.";
        }

        c.setStatus(newStatus);
        c.setHandledBy(staffId);
        return saveClaimTransition(c, staffId, note.trim(),
                "Cập nhật tiến độ bảo hành #" + claimId, note.trim());
    }

    // =========================================================================
    // UC 283–284: Ghi kết quả sửa chữa hoặc đổi mới linh kiện
    // =========================================================================
    public String recordResolution(int claimId, int staffId, boolean isReplacement,
            String replacementSerial, String resolutionNote) {
        WarrantyClaims c = claimsDAO.findById(claimId);
        if (c == null) {
            return "Không tìm thấy yêu cầu bảo hành #" + claimId;
        }
        if ("COMPLETED".equalsIgnoreCase(c.getStatus()) || "REJECTED".equalsIgnoreCase(c.getStatus())) {
            return "Yêu cầu bảo hành đã đóng.";
        }
        if (resolutionNote == null || resolutionNote.trim().isEmpty()) {
            return "Vui lòng nhập ghi chú kết quả sửa chữa / đổi mới.";
        }
        if (isReplacement && (replacementSerial == null || replacementSerial.trim().isEmpty())) {
            return "Vui lòng nhập số Serial linh kiện đổi mới.";
        }

        c.setHandledBy(staffId);
        c.setResolutionNote(resolutionNote.trim());
        c.setStatus(isReplacement ? "REPLACED" : "REPAIRED");
        if (isReplacement) {
            c.setReplacementSerialNumber(replacementSerial.trim());
        }

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                claimsDAO.update(conn, c);
                if (isReplacement) {
                    eWarrantiesDAO.updateSerialNumber(conn, c.getWarrantyId(), replacementSerial.trim());
                }
                String logNote = isReplacement
                        ? "Đổi mới linh kiện (Serial mới: " + replacementSerial.trim() + "). " + resolutionNote.trim()
                        : "Đã sửa chữa xong. " + resolutionNote.trim();
                addClaimLog(conn, claimId, c.getStatus(), logNote, staffId);
                conn.commit();

                sendNotification(c.getUserId(), "WARRANTY", "Có kết quả bảo hành #" + claimId,
                        logNote, "WARRANTY_CLAIM", claimId);
                logStaffAudit(staffId, "WARRANTY_RESOLUTION", "warranty_claims", claimId, logNote);
                return null;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return "Lỗi khi tạo yêu cầu bảo hành: " + ex.getMessage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối cơ sở dữ liệu.";
        }
    }

    // =========================================================================
    // UC 286: Xác nhận hoàn tất & trả linh kiện cho khách
    // =========================================================================
    public String completeClaim(int claimId, int staffId, String finalNote) {
        WarrantyClaims c = claimsDAO.findById(claimId);
        if (c == null) {
            return "Không tìm thấy yêu cầu bảo hành #" + claimId;
        }
        if (!"REPAIRED".equalsIgnoreCase(c.getStatus()) && !"REPLACED".equalsIgnoreCase(c.getStatus())) {
            return "Cần ghi nhận kết quả sửa chữa hoặc đổi mới trước khi hoàn tất.";
        }

        c.setHandledBy(staffId);
        c.setStatus("COMPLETED");
        c.setCompletedAt(new Date());

        String note = (finalNote != null && !finalNote.trim().isEmpty())
                ? finalNote.trim()
                : "Đã bàn giao linh kiện bảo hành cho khách hàng.";
        return saveClaimTransition(c, staffId, note,
                "Hoàn tất bảo hành #" + claimId, note);
    }

    // =========================================================================
    // UC 287: Xem lịch sử bảo hành & chi tiết log
    // =========================================================================
    public List<HashMap<String, Object>> getUserClaimHistory(int userId) {
        return claimsDAO.findWithDetails(userId, null);
    }

    public List<HashMap<String, Object>> getAdminClaims(String statusFilter) {
        return claimsDAO.findWithDetails(null, statusFilter);
    }

    public List<HashMap<String, Object>> getClaimLogs(int claimId) {
        return logsDAO.findByClaimIdWithUser(claimId);
    }

    // =========================================================================
    // Helpers nội bộ
    // =========================================================================
    private String saveClaimTransition(WarrantyClaims c, int staffId, String logNote,
            String notifTitle, String notifContent) {
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                claimsDAO.update(conn, c);
                addClaimLog(conn, c.getClaimId(), c.getStatus(), logNote, staffId);
                conn.commit();

                sendNotification(c.getUserId(), "WARRANTY", notifTitle, notifContent,
                        "WARRANTY_CLAIM", c.getClaimId());
                logStaffAudit(staffId, "UPDATE_WARRANTY_" + c.getStatus(), "warranty_claims",
                        c.getClaimId(), logNote);
                return null;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return "Lỗi khi tạo yêu cầu bảo hành: " + ex.getMessage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối cơ sở dữ liệu.";
        }
    }

    private void addClaimLog(Connection conn, int claimId, String status, String note, Integer userId) throws SQLException {
        WarrantyClaimLogs log = new WarrantyClaimLogs();
        log.setClaimId(claimId);
        log.setStatus(status);
        log.setNote(note);
        log.setUpdatedBy(userId);
        log.setCreatedAt(new Date());
        logsDAO.insert(conn, log);
    }

    private boolean isWarrantyOwnedByUser(int warrantyId, int userId) {
        String sql = "SELECT COUNT(*) FROM e_warranties w "
                + "JOIN orders o ON w.order_id = o.order_id "
                + "WHERE w.warranty_id = ? AND o.user_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warrantyId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendNotification(int userId, String type, String title, String content,
            String refType, int refId) {
        String sql = "INSERT INTO notifications "
                + "(user_id, notification_type, title, content, channel, reference_type, reference_id, status, is_read, created_at, sent_at) "
                + "VALUES (?, ?, ?, ?, 'IN_APP', ?, ?, 'SENT', 0, ?, ?)";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, title);
            ps.setString(4, content);
            ps.setString(5, refType);
            ps.setInt(6, refId);
            ps.setTimestamp(7, now);
            ps.setTimestamp(8, now);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logStaffAudit(int staffId, String action, String targetTable, int targetId, String desc) {
        String sql = "INSERT INTO staff_audit_logs "
                + "(staff_id, action, target_table, target_id, description, created_at, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setString(2, action);
            ps.setString(3, targetTable);
            ps.setInt(4, targetId);
            ps.setString(5, desc);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
