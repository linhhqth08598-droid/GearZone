package DAO;

import Models.OrderItems;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsDAO {

    public List<OrderItems> getAll() {
        List<OrderItems> list = new ArrayList<>();
        String sql = "SELECT * FROM [order_items]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItems oi = new OrderItems();
                    oi.setOrderItemId(rs.getInt("order_item_id"));
                    oi.setOrderId(rs.getInt("order_id"));
                    oi.setPartId(rs.getInt("part_id"));
                    oi.setSerialNumber(rs.getString("serial_number"));
                    oi.setQuantity(rs.getInt("quantity"));
                    oi.setUnitPrice(rs.getDouble("unit_price"));
                    oi.setCostPrice(rs.getDouble("cost_price"));
                    oi.setStatus(rs.getString("status"));
                    list.add(oi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
