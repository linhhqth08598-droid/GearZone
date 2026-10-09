package Controller;

import DAO.SliderDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SliderServlet", urlPatterns = {"/admin/saveSlider", "/admin/deleteSlider"})
public class SliderServlet extends HttpServlet {
    private SliderDAO sliderDAO = new SliderDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        try {
            if (action.equals("/admin/saveSlider")) {
                String title = request.getParameter("sliderTitle");
                String base64 = request.getParameter("sliderBase64");
                sliderDAO.insertSlider(title, base64);
                request.getSession().setAttribute("successMsg", "Thêm ảnh Slider thành công!");
            } else if (action.equals("/admin/deleteSlider")) {
                int id = Integer.parseInt(request.getParameter("sliderId"));
                sliderDAO.deleteSlider(id);
                request.getSession().setAttribute("successMsg", "Đã xóa Slider!");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/home");
    }
}