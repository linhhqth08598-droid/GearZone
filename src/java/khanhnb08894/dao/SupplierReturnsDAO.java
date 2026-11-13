package khanhnb08894.dao;

import model.SupplierReturns;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierReturnsDAO {

    public List<SupplierReturns> getAll() {
        List<SupplierReturns> list = new ArrayList<>();
        String sql = "SELECT * FROM [supplier_returns]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupplierReturns obj = new SupplierReturns();
                obj.setSupplierReturnId(rs.getInt("supplier_return_id"));
                obj.setReturnCode(rs.getString("return_code"));
                obj.setSupplierId(rs.getInt("supplier_id"));
                obj.setPoId(rs.getInt("po_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setDefectType(rs.getString("defect_type"));
                obj.setReason(rs.getString("reason"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedBy(rs.getInt("created_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
