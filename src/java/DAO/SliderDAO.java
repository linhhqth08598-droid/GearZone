package DAO;

import Models.Slider;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SliderDAO {
    public List<Slider> getAllSliders() {
        List<Slider> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Sliders ORDER BY SliderID DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Slider s = new Slider();
                s.setSliderId(rs.getInt("SliderID"));
                s.setTitle(rs.getString("Title"));
                s.setImageBase64(rs.getString("ImageBase64"));
                list.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void insertSlider(String title, String base64) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO Sliders (Title, ImageBase64) VALUES (?, ?)")) {
            ps.setString(1, title);
            ps.setString(2, base64);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteSlider(int sliderId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Sliders WHERE SliderID = ?")) {
            ps.setInt(1, sliderId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}