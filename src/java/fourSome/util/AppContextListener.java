package fourSome.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class AppContextListener implements ServletContextListener {

    private PriceAlertJob job;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EmailUtil.warmUp();
        job = new PriceAlertJob();
        job.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (job != null) {
            job.stop();
        }

        // Tắt thread pool của EmailUtil
        EmailUtil.shutdown();

        // Đóng bể kết nối HikariCP trước khi hủy đăng ký JDBC driver
        fourSome.url.DBConnect.closePool();

        // Hủy đăng ký JDBC driver của webapp
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
