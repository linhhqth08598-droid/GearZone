package DAO;

import Models.UserRequirementGames;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRequirementGamesDAO {

    public List<UserRequirementGames> getAll() {
        List<UserRequirementGames> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_requirement_games]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserRequirementGames urg = new UserRequirementGames();
                    urg.setRequirementId(rs.getInt("requirement_id"));
                    urg.setGameId(rs.getInt("game_id"));
                    urg.setStatus(rs.getString("status"));
                    list.add(urg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
