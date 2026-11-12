package DAO;

import Models.SupportTicketMessages;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportTicketMessagesDAO {

    public List<SupportTicketMessages> getAll() {
        List<SupportTicketMessages> list = new ArrayList<>();
        String sql = "SELECT * FROM [support_ticket_messages]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportTicketMessages stm = new SupportTicketMessages();
                    stm.setMessageId(rs.getInt("message_id"));
                    stm.setTicketId(rs.getInt("ticket_id"));
                    stm.setSenderId(rs.getInt("sender_id"));
                    stm.setMessage(rs.getString("message"));
                    stm.setAttachmentUrl(rs.getString("attachment_url"));
                    stm.setCreatedAt(rs.getDate("created_at"));
                    stm.setStatus(rs.getString("status"));
                    list.add(stm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
