package DAO;

import Models.EWarranties;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EWarrantiesDAO {

    public List<EWarranties> getAll() {
        List<EWarranties> list = new ArrayList<>();
        String sql = "SELECT * FROM [e_warranties]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EWarranties ew = new EWarranties();
                    ew.setWarrantyId(rs.getInt("warranty_id"));
                    ew.setWarrantyCode(rs.getString("warranty_code"));
                    ew.setSerialNumber(rs.getString("serial_number"));
                    ew.setPartId(rs.getInt("part_id"));
                    ew.setOrderId(rs.getInt("order_id"));
                    ew.setStartDate(rs.getDate("start_date"));
                    ew.setEndDate(rs.getDate("end_date"));
                    ew.setStatus(rs.getString("status"));
                    ew.setDocumentUrl(rs.getString("document_url"));
                    list.add(ew);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
