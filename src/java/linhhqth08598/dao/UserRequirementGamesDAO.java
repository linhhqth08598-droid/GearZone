package linhhqth08598.dao;

import model.UserRequirementGames;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRequirementGamesDAO {

    public List<UserRequirementGames> getAll() {
        List<UserRequirementGames> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_requirement_games]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserRequirementGames obj = new UserRequirementGames();
                obj.setRequirementId(rs.getInt("requirement_id"));
                obj.setGameId(rs.getInt("game_id"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
