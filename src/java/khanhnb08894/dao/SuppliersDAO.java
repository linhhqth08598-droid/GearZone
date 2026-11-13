package khanhnb08894.dao;

import model.Suppliers;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuppliersDAO {

    public List<Suppliers> getAll() {
        List<Suppliers> list = new ArrayList<>();
        String sql = "SELECT * FROM [suppliers]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Suppliers obj = new Suppliers();
                obj.setSupplierId(rs.getInt("supplier_id"));
                obj.setSupplierName(rs.getString("supplier_name"));
                obj.setContactName(rs.getString("contact_name"));
                obj.setPhone(rs.getString("phone"));
                obj.setEmail(rs.getString("email"));
                obj.setAddress(rs.getString("address"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
