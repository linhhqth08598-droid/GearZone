package DAO;

import Models.Suppliers;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuppliersDAO {

    public List<Suppliers> getAll() {
        List<Suppliers> list = new ArrayList<>();
        String sql = "SELECT * FROM [suppliers]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
