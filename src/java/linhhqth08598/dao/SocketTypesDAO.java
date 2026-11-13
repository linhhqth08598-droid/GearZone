package dungntth08581.dao;

import model.SocketTypes;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocketTypesDAO {

    public List<SocketTypes> getAll() {
        List<SocketTypes> list = new ArrayList<>();
        String sql = "SELECT * FROM [socket_types]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SocketTypes obj = new SocketTypes();
                obj.setSocketId(rs.getInt("socket_id"));
                obj.setSocketName(rs.getString("socket_name"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
