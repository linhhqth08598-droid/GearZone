package DAO;

import Models.PurchaseOrderDetails;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailsDAO {

    public List<PurchaseOrderDetails> getAll() {
        List<PurchaseOrderDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM [purchase_order_details]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderDetails pod = new PurchaseOrderDetails();
                    pod.setPoDetailId(rs.getInt("po_detail_id"));
                    pod.setPoId(rs.getInt("po_id"));
                    pod.setPartId(rs.getInt("part_id"));
                    pod.setImportQuantity(rs.getInt("import_quantity"));
                    pod.setImportUnitPrice(rs.getDouble("import_unit_price"));
                    pod.setReceivedQuantity(rs.getInt("received_quantity"));
                    pod.setMissingQuantity(rs.getInt("missing_quantity"));
                    pod.setWrongItemQuantity(rs.getInt("wrong_item_quantity"));
                    pod.setDamagedQuantity(rs.getInt("damaged_quantity"));
                    pod.setStatus(rs.getString("status"));
                    list.add(pod);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
