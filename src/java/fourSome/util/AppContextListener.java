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
        job = new PriceAlertJob();
        job.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (job != null) {
            job.stop();
        }

        // Hủy đăng ký JDBC driver của webapp để tránh cảnh báo memory leak khi redeploy
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