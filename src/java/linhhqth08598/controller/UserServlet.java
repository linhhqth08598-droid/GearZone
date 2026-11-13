package linhhqth08598.controller;

import fourSome.model.ShippingAddresses;
import fourSome.model.Users;
import fourSome.util.FileUploadUtil;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // Chặn cứng tối đa 10MB
public class UserServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "profile";
        }

        switch (action) {
            case "profile":
                request.getRequestDispatcher("/views/linhhqth08598/user/profile.jsp").forward(request, response);
                break;
            case "address":
                request.setAttribute("addresses", userService.getUserAddresses(loggedUser.getUserId()));
                request.getRequestDispatcher("/views/linhhqth08598/user/address.jsp").forward(request, response);
                break;
            case "setDefaultAddress":
                int defId = Integer.parseInt(request.getParameter("id"));
                userService.setDefaultAddress(defId, loggedUser.getUserId());
                response.sendRedirect(request.getContextPath() + "/user?action=address");
                break;
            case "deleteAddress":
                int delId = Integer.parseInt(request.getParameter("id"));
                userService.deleteAddress(delId, loggedUser.getUserId());
                response.sendRedirect(request.getContextPath() + "/user?action=address");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user?action=profile");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        switch (action) {
            case "updateProfile":
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String error = userService.updateProfile(loggedUser.getUserId(), fullName, phone);

                if (error == null) {
                    loggedUser.setFullName(fullName);
                    loggedUser.setPhoneNumber(phone);
                    session.setAttribute("loggedUser", loggedUser);
                    session.setAttribute("message", "Cập nhật thông tin thành công.");
                } else {
                    session.setAttribute("error", error);
                }
                response.sendRedirect(request.getContextPath() + "/user?action=profile");
                break;

            case "updateAvatar":
                Part filePart = request.getPart("avatar");
                if (filePart != null && filePart.getSize() > 0) {
                    // Đọc dữ liệu từ file upload
                    java.io.InputStream fileContent = filePart.getInputStream();
                    byte[] imageBytes = fileContent.readAllBytes();

                    // Mã hóa sang chuỗi Base64
                    String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

                    // Tạo chuỗi data URI để trình duyệt có thể hiển thị trực tiếp
                    // Giả sử ảnh luôn là jpeg/png, bạn có thể nối chuỗi như sau:
                    String avatarBase64Url = "data:image/jpeg;base64," + base64Image;

                    if (userService.updateAvatar(loggedUser.getUserId(), avatarBase64Url) == null) {
                        loggedUser.setAvatarUrl(avatarBase64Url);
                        session.setAttribute("loggedUser", loggedUser);
                        session.setAttribute("message", "Đổi ảnh đại diện thành công.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/user?action=profile");
                break;

            case "saveAddress":
                ShippingAddresses addr = new ShippingAddresses();
                String idParam = request.getParameter("addressId");
                addr.setUserId(loggedUser.getUserId());
                addr.setRecipientName(request.getParameter("recipientName"));
                addr.setPhone(request.getParameter("phone"));
                addr.setProvince(request.getParameter("province"));
                addr.setDistrict(request.getParameter("district"));
                addr.setWard(request.getParameter("ward"));
                addr.setDetailedAddress(request.getParameter("detailedAddress"));
                addr.setIsDefault(request.getParameter("isDefault") != null);

                if (idParam != null && !idParam.trim().isEmpty()) {
                    addr.setAddressId(Integer.parseInt(idParam));
                    userService.updateAddress(addr);
                    session.setAttribute("message", "Cập nhật địa chỉ thành công.");
                } else {
                    userService.addAddress(addr);
                    session.setAttribute("message", "Thêm địa chỉ thành công.");
                }
                response.sendRedirect(request.getContextPath() + "/user?action=address");
                break;
        }
    }
}
