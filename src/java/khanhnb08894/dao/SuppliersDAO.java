package khanhnb08894.dao;

import fourSome.model.Suppliers;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuppliersDAO {

    // Lấy danh sách nhà cung cấp và tìm kiếm
    public List<Suppliers> getAll(String keyword, String status)
            throws SQLException {

        List<Suppliers> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT supplier_id, supplier_name, contact_name, "
            + "phone, email, address, status "
            + "FROM suppliers WHERE 1=1"
        );

        boolean hasKeyword =
                keyword != null && !keyword.trim().isEmpty();

        boolean hasStatus =
                status != null && !status.trim().isEmpty();

        if (hasKeyword) {
            sql.append(" AND (supplier_name LIKE ? "
                    + "OR contact_name LIKE ? "
                    + "OR phone LIKE ? OR email LIKE ?)");
        }

        if (hasStatus) {
            sql.append(" AND status = ?");
        }

        sql.append(" ORDER BY supplier_id DESC");

        try (Connection con = DBConnect.getConnection()) {

            if (con == null) {
                throw new SQLException("Không kết nối được database!");
            }

            try (PreparedStatement ps =
                    con.prepareStatement(sql.toString())) {

                int index = 1;

                if (hasKeyword) {
                    String value = "%" + keyword.trim() + "%";

                    ps.setString(index++, value);
                    ps.setString(index++, value);
                    ps.setString(index++, value);
                    ps.setString(index++, value);
                }

                if (hasStatus) {
                    ps.setString(index++, status.trim());
                }

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Suppliers s = new Suppliers();

                        s.setSupplierId(rs.getInt("supplier_id"));
                        s.setSupplierName(rs.getString("supplier_name"));
                        s.setContactName(rs.getString("contact_name"));
                        s.setPhone(rs.getString("phone"));
                        s.setEmail(rs.getString("email"));
                        s.setAddress(rs.getString("address"));
                        s.setStatus(rs.getString("status"));

                        list.add(s);
                    }
                }
            }
        }

        return list;
    }

    // Giữ phương thức cũ để không ảnh hưởng code đang sử dụng
    public List<Suppliers> getAll() {
        try {
            return getAll(null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Thêm nhà cung cấp
    public boolean insertSupplier(Suppliers s)
            throws SQLException {

        String sql = "INSERT INTO suppliers "
                + "(supplier_name, contact_name, phone, "
                + "email, address, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnect.getConnection()) {

            if (con == null) {
                throw new SQLException("Không kết nối được database!");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, s.getSupplierName().trim());
                ps.setString(2, emptyToNull(s.getContactName()));
                ps.setString(3, s.getPhone().trim());
                ps.setString(4, emptyToNull(s.getEmail()));
                ps.setString(5, emptyToNull(s.getAddress()));
                ps.setString(6, "ACTIVE");

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Cập nhật nhà cung cấp
    public boolean updateSupplier(Suppliers s)
            throws SQLException {

        String sql = "UPDATE suppliers SET "
                + "supplier_name = ?, "
                + "contact_name = ?, "
                + "phone = ?, "
                + "email = ?, "
                + "address = ?, "
                + "status = ? "
                + "WHERE supplier_id = ?";

        try (Connection con = DBConnect.getConnection()) {

            if (con == null) {
                throw new SQLException("Không kết nối được database!");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, s.getSupplierName().trim());
                ps.setString(2, emptyToNull(s.getContactName()));
                ps.setString(3, s.getPhone().trim());
                ps.setString(4, emptyToNull(s.getEmail()));
                ps.setString(5, emptyToNull(s.getAddress()));
                ps.setString(6, s.getStatus());
                ps.setInt(7, s.getSupplierId());

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Đếm số phiếu nhập / phiếu trả hàng đang tham chiếu tới nhà cung cấp
    public int countReferences(int supplierId) throws SQLException {

        String sql = "SELECT (SELECT COUNT(*) FROM purchase_orders WHERE supplier_id = ?) "
                + "+ (SELECT COUNT(*) FROM supplier_returns WHERE supplier_id = ?)";

        try (Connection con = DBConnect.getConnection()) {

            if (con == null) {
                throw new SQLException("Không kết nối được database!");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, supplierId);
                ps.setInt(2, supplierId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        }
    }

    // Xóa nhà cung cấp (chỉ gọi khi countReferences = 0)
    public boolean deleteSupplier(int supplierId) throws SQLException {

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection con = DBConnect.getConnection()) {

            if (con == null) {
                throw new SQLException("Không kết nối được database!");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, supplierId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    private String emptyToNull(String value) {
        return value == null || value.trim().isEmpty()
                ? null : value.trim();
    }
}