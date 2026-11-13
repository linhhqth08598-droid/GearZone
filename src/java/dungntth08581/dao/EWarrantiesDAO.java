package dungntth08581.dao;

import fourSome.model.EWarranties;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EWarrantiesDAO {

    public List<EWarranties> getAll() {
        List<EWarranties> list = new ArrayList<>();
        String sql = "SELECT * FROM [e_warranties]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EWarranties obj = new EWarranties();
                obj.setWarrantyId(rs.getInt("warranty_id"));
                obj.setWarrantyCode(rs.getString("warranty_code"));
                obj.setSerialNumber(rs.getString("serial_number"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setStartDate(rs.getDate("start_date"));
                obj.setEndDate(rs.getDate("end_date"));
                obj.setStatus(rs.getString("status"));
                obj.setDocumentUrl(rs.getString("document_url"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
