package fourSome.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PriceAlertJob job = new PriceAlertJob();
        job.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Tắt job khi server dừng (nếu cần)
    }
}