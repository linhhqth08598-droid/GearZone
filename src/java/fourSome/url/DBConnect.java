package fourSome.url;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            // Đọc cấu hình từ file db.properties nằm ngay dưới Source Packages
            try (InputStream is = DBConnect.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is == null) {
                    throw new RuntimeException("Không tìm thấy file db.properties!");
                }
                props.load(is);
            }

            String hostname = props.getProperty("db.hostname", "127.0.0.1").trim();
            String port = props.getProperty("db.port", "1433").trim();
            String dbName = props.getProperty("db.dbname", "GearZone").trim();
            String username = props.getProperty("db.username", "sa").trim();
            String password = props.getProperty("db.password", "123").trim();

            String connectionUrl = "jdbc:sqlserver://" + hostname + ":" + port + ";"
                    + "databaseName=" + dbName + ";encrypt=false;trustServerCertificate=true;";

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(connectionUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Cấu hình bể kết nối giúp truy vấn tức thì
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(15000);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(1200000);

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static void main(String[] args) {
        // Test thử kết nối và đo thời gian lấy connection lần 1 và lần 2
        long t1 = System.currentTimeMillis();
        try (Connection c1 = getConnection()) {
            System.out.println("Lần 1 (Khởi tạo Pool): " + c1 + " -> Mất " + (System.currentTimeMillis() - t1) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();
        try (Connection c2 = getConnection()) {
            System.out.println("Lần 2 (Lấy từ Pool):   " + c2 + " -> Mất " + (System.currentTimeMillis() - t2) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Đã đóng HikariCP Connection Pool an toàn.");
        }
    }

}
