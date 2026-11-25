package linhhqth08598.dao;

import fourSome.model.SiteContactInfo;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SiteContactInfoDAO {

    private static volatile boolean tableEnsured = false;

    private void ensureTable(Connection con) {
        if (tableEnsured) return;
        String createSql = "IF OBJECT_ID(N'[dbo].[site_contact_info]', N'U') IS NULL "
                + "BEGIN "
                + "CREATE TABLE [dbo].[site_contact_info] ("
                + "[contact_info_id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY, "
                + "[company_name] [nvarchar](200) NOT NULL, "
                + "[intro_text] [nvarchar](500) NULL, "
                + "[address_1] [nvarchar](255) NULL, "
                + "[address_2] [nvarchar](255) NULL, "
                + "[addresses_json] [nvarchar](max) NULL, "
                + "[sales_hours] [nvarchar](150) NULL, "
                + "[tech_hours] [nvarchar](150) NULL, "
                + "[hotline] [varchar](50) NOT NULL, "
                + "[facebook_url] [nvarchar](500) NULL, "
                + "[youtube_url] [nvarchar](500) NULL, "
                + "[chat_url] [nvarchar](500) NULL, "
                + "[updated_at] [datetime2](7) NOT NULL DEFAULT (SYSDATETIME()), "
                + "[updated_by] [int] NULL"
                + "); "
                + "END "
                + "ELSE IF COL_LENGTH(N'dbo.site_contact_info', N'addresses_json') IS NULL "
                + "BEGIN "
                + "ALTER TABLE [dbo].[site_contact_info] ADD [addresses_json] [nvarchar](max) NULL; "
                + "END";
        try (Statement st = con.createStatement()) {
            st.executeUpdate(createSql);
            tableEnsured = true;
        } catch (Exception ignored) {
        }
    }

    public SiteContactInfo getActiveInfo() {
        String sql = "SELECT TOP 1 * FROM dbo.site_contact_info ORDER BY contact_info_id ASC";
        try (Connection con = DBConnect.getConnection()) {
            ensureTable(con);
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SiteContactInfo info = new SiteContactInfo();
                    info.setContactInfoId(rs.getInt("contact_info_id"));
                    info.setCompanyName(rs.getString("company_name"));
                    info.setIntroText(rs.getString("intro_text"));
                    String addr1 = rs.getString("address_1");
                    String addr2 = rs.getString("address_2");
                    info.setAddress1(addr1);
                    info.setAddress2(addr2);

                    String jsonCol = null;
                    try {
                        jsonCol = rs.getString("addresses_json");
                    } catch (Exception ignored) {
                    }
                    List<String> list = parseAddresses(jsonCol, addr1, addr2);
                    info.setAddresses(list);
                    info.setAddress1(!list.isEmpty() ? list.get(0) : null);
                    info.setAddress2(list.size() > 1 ? list.get(1) : null);

                    info.setSalesHours(rs.getString("sales_hours"));
                    info.setTechHours(rs.getString("tech_hours"));
                    info.setHotline(rs.getString("hotline"));
                    info.setFacebookUrl(rs.getString("facebook_url"));
                    info.setYoutubeUrl(rs.getString("youtube_url"));
                    info.setChatUrl(rs.getString("chat_url"));
                    info.setUpdatedAt(rs.getTimestamp("updated_at"));
                    int updBy = rs.getInt("updated_by");
                    info.setUpdatedBy(rs.wasNull() ? null : updBy);
                    return info;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultInfo();
    }

    public boolean saveInfo(SiteContactInfo info) {
        List<String> cleanAddresses = normalizeAddresses(info.getAddresses());
        if (cleanAddresses.isEmpty()) {
            cleanAddresses = normalizeFromLegacy(info.getAddress1(), info.getAddress2());
        }
        info.setAddresses(cleanAddresses);
        info.setAddress1(!cleanAddresses.isEmpty() ? cleanAddresses.get(0) : null);
        info.setAddress2(cleanAddresses.size() > 1 ? cleanAddresses.get(1) : null);

        String checkSql = "SELECT TOP 1 contact_info_id FROM dbo.site_contact_info ORDER BY contact_info_id ASC";
        String updateSql = "UPDATE dbo.site_contact_info SET company_name = ?, intro_text = ?, address_1 = ?, address_2 = ?, "
                       + "addresses_json = ?, sales_hours = ?, tech_hours = ?, hotline = ?, facebook_url = ?, youtube_url = ?, chat_url = ?, "
                       + "updated_at = SYSDATETIME(), updated_by = ? WHERE contact_info_id = ?";
        String insertSql = "INSERT INTO dbo.site_contact_info (company_name, intro_text, address_1, address_2, "
                       + "addresses_json, sales_hours, tech_hours, hotline, facebook_url, youtube_url, chat_url, updated_at, updated_by) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), ?)";

        try (Connection con = DBConnect.getConnection()) {
            ensureTable(con);
            Integer existingId = null;
            try (PreparedStatement ps = con.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) existingId = rs.getInt("contact_info_id");
            }
            if (existingId != null) {
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    bindFields(ps, info);
                    ps.setInt(13, existingId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    bindFields(ps, info);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void bindFields(PreparedStatement ps, SiteContactInfo info) throws Exception {
        ps.setString(1, info.getCompanyName());
        ps.setString(2, info.getIntroText());
        ps.setString(3, info.getAddress1());
        ps.setString(4, info.getAddress2());
        ps.setString(5, serializeAddresses(info.getAddresses()));
        ps.setString(6, info.getSalesHours());
        ps.setString(7, info.getTechHours());
        ps.setString(8, info.getHotline());
        ps.setString(9, info.getFacebookUrl());
        ps.setString(10, info.getYoutubeUrl());
        ps.setString(11, info.getChatUrl());
        if (info.getUpdatedBy() == null || info.getUpdatedBy() <= 0) {
            ps.setNull(12, Types.INTEGER);
        } else {
            ps.setInt(12, info.getUpdatedBy());
        }
    }

    private static List<String> normalizeAddresses(List<String> rawList) {
        List<String> out = new ArrayList<>();
        if (rawList == null) return out;
        for (String s : rawList) {
            if (s != null) {
                String t = s.trim();
                if (!t.isEmpty()) out.add(t);
            }
        }
        return out;
    }

    private static List<String> normalizeFromLegacy(String addr1, String addr2) {
        List<String> out = new ArrayList<>();
        if (addr1 != null && !addr1.trim().isEmpty()) out.add(addr1.trim());
        if (addr2 != null && !addr2.trim().isEmpty()) out.add(addr2.trim());
        return out;
    }

    private static String serializeAddresses(List<String> list) {
        List<String> clean = normalizeAddresses(list);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < clean.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(clean.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static List<String> parseAddresses(String jsonCol, String addr1, String addr2) {
        if (jsonCol != null && !jsonCol.trim().isEmpty()) {
            List<String> parsed = new ArrayList<>();
            String s = jsonCol.trim();
            if (s.startsWith("[") && s.endsWith("]")) {
                boolean inQuotes = false;
                boolean escaped = false;
                StringBuilder cur = new StringBuilder();
                for (int i = 1; i < s.length() - 1; i++) {
                    char ch = s.charAt(i);
                    if (escaped) {
                        switch (ch) {
                            case 'n': cur.append('\n'); break;
                            case 'r': cur.append('\r'); break;
                            case 't': cur.append('\t'); break;
                            default: cur.append(ch); break;
                        }
                        escaped = false;
                    } else if (ch == '\\' && inQuotes) {
                        escaped = true;
                    } else if (ch == '"') {
                        if (inQuotes) {
                            String item = cur.toString().trim();
                            if (!item.isEmpty()) parsed.add(item);
                            cur.setLength(0);
                        }
                        inQuotes = !inQuotes;
                    } else if (inQuotes) {
                        cur.append(ch);
                    }
                }
            }
            if (!parsed.isEmpty()) return parsed;
        }
        return normalizeFromLegacy(addr1, addr2);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static SiteContactInfo defaultInfo() {
        SiteContactInfo info = new SiteContactInfo();
        info.setCompanyName("Công ty TNHH GearZone Việt Nam");
        info.setIntroText("Cần tư vấn nhanh? Gọi hotline hoặc gửi yêu cầu ở trên, chúng tôi sẽ phản hồi sớm nhất.");
        info.setAddress1("Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội");
        info.setAddress2("Số 2 Trần Phú, Hà Đông, Hà Nội");
        info.setAddresses(normalizeFromLegacy(info.getAddress1(), info.getAddress2()));
        info.setSalesHours("8h30 – 21h30");
        info.setTechHours("8h30 – 12h, 13h30 – 17h30");
        info.setHotline("0123 456 789");
        info.setFacebookUrl("#");
        info.setYoutubeUrl("#");
        info.setChatUrl("#");
        return info;
    }
}