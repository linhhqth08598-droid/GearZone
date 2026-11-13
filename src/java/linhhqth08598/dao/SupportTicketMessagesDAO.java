package linhhqth08598.dao;

import fourSome.model.SupportTicketMessages;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportTicketMessagesDAO {

    public List<SupportTicketMessages> getAll() {
        List<SupportTicketMessages> list = new ArrayList<>();
        String sql = "SELECT * FROM [support_ticket_messages]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupportTicketMessages obj = new SupportTicketMessages();
                obj.setMessageId(rs.getInt("message_id"));
                obj.setTicketId(rs.getInt("ticket_id"));
                obj.setSenderId(rs.getInt("sender_id"));
                obj.setMessage(rs.getString("message"));
                obj.setAttachmentUrl(rs.getString("attachment_url"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
