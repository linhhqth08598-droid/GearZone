package DAO;

import Models.SupplierReturns;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierReturnsDAO {

    public List<SupplierReturns> getAll() {
        List<SupplierReturns> list = new ArrayList<>();
        String sql = "SELECT * FROM [supplier_returns]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupplierReturns sr = new SupplierReturns();
                    sr.setSupplierReturnId(rs.getInt("supplier_return_id"));
                    sr.setReturnCode(rs.getString("return_code"));
                    sr.setSupplierId(rs.getInt("supplier_id"));
                    sr.setPoId(rs.getInt("po_id"));
                    sr.setPartId(rs.getInt("part_id"));
                    sr.setQuantity(rs.getInt("quantity"));
                    sr.setDefectType(rs.getString("defect_type"));
                    sr.setReason(rs.getString("reason"));
                    sr.setStatus(rs.getString("status"));
                    sr.setCreatedBy(rs.getInt("created_by"));
                    sr.setCreatedAt(rs.getDate("created_at"));
                    list.add(sr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
