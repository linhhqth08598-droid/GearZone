package khanhnb08894.dao;

import model.PurchaseOrderDetails;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailsDAO {

    public List<PurchaseOrderDetails> getAll() {
        List<PurchaseOrderDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM [purchase_order_details]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseOrderDetails obj = new PurchaseOrderDetails();
                obj.setPoDetailId(rs.getInt("po_detail_id"));
                obj.setPoId(rs.getInt("po_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setImportQuantity(rs.getInt("import_quantity"));
                obj.setImportUnitPrice(rs.getDouble("import_unit_price"));
                obj.setReceivedQuantity(rs.getInt("received_quantity"));
                obj.setMissingQuantity(rs.getInt("missing_quantity"));
                obj.setWrongItemQuantity(rs.getInt("wrong_item_quantity"));
                obj.setDamagedQuantity(rs.getInt("damaged_quantity"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
