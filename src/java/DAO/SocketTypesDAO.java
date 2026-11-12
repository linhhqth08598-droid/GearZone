package DAO;

import Models.SocketTypes;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocketTypesDAO {

    public List<SocketTypes> getAll() {
        List<SocketTypes> list = new ArrayList<>();
        String sql = "SELECT * FROM [socket_types]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SocketTypes st = new SocketTypes();
                    st.setSocketId(rs.getInt("socket_id"));
                    st.setSocketName(rs.getString("socket_name"));
                    st.setStatus(rs.getString("status"));
                    list.add(st);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
