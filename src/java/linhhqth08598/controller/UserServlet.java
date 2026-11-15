package linhhqth08598.controller;

import fourSome.model.ShippingAddresses;
import fourSome.model.Users;
import fourSome.util.Constants;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * UC 8  - Cập nhật thông tin cá nhân
 * UC 9  - Cập nhật địa chỉ giao hàng (sổ địa chỉ)
 * UC 10 - Đổi ảnh đại diện
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 12 * 1024 * 1024)
public class UserServlet extends HttpServlet {

    private static final int AVATAR_SIZE = 256; // ảnh đại diện được cắt vuông và thu về 256x256

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users loggedUser = currentUser(request, response);
        if (loggedUser == null) return;

        String action = request.getParameter("action");
        if (action == null) action = "profile";

        switch (action) {
            case "address":
                request.setAttribute("addresses", userService.getUserAddresses(loggedUser.getUserId()));
                request.getRequestDispatcher("/views/linhhqth08598/user/address.jsp").forward(request, response);
                break;
            case "profile":
            default:
                request.getRequestDispatcher("/views/linhhqth08598/user/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Users loggedUser = currentUser(request, response);
        if (loggedUser == null) return;
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        if (action == null) action = "";

        String redirectTo = "/user?action=profile";

        switch (action) {
            case "updateProfile": {
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String error = userService.updateProfile(loggedUser.getUserId(), fullName, phone);
                if (error == null) {
                    loggedUser.setFullName(fullName.trim());
                    loggedUser.setPhoneNumber(phone == null || phone.trim().isEmpty() ? null : phone.trim());
                    session.setAttribute("loggedUser", loggedUser);
                    session.setAttribute("message", "Cập nhật thông tin thành công.");
                } else {
                    session.setAttribute("error", error);
                }
                break;
            }

            case "updateAvatar":
                handleAvatarUpload(request, session, loggedUser);
                break;

            case "removeAvatar":
                if (userService.updateAvatar(loggedUser.getUserId(), null) == null) {
                    loggedUser.setAvatarUrl(null);
                    session.setAttribute("loggedUser", loggedUser);
                    session.setAttribute("message", "Đã xóa ảnh đại diện.");
                } else {
                    session.setAttribute("error", "Không xóa được ảnh đại diện!");
                }
                break;

            case "saveAddress": {
                redirectTo = "/user?action=address";
                ShippingAddresses addr = new ShippingAddresses();
                String idParam = request.getParameter("addressId");
                addr.setUserId(loggedUser.getUserId());
                if (idParam != null && idParam.matches("\\d{1,9}")) addr.setAddressId(Integer.parseInt(idParam));
                addr.setRecipientName(request.getParameter("recipientName"));
                addr.setPhone(request.getParameter("phone"));
                addr.setProvince(request.getParameter("province"));
                addr.setDistrict(request.getParameter("district"));
                addr.setWard(request.getParameter("ward"));
                addr.setDetailedAddress(request.getParameter("detailedAddress"));
                addr.setIsDefault(request.getParameter("isDefault") != null);

                boolean isNew = addr.getAddressId() == null;
                String error = userService.saveAddress(addr);
                if (error == null) {
                    session.setAttribute("message", isNew ? "Thêm địa chỉ thành công." : "Cập nhật địa chỉ thành công.");
                } else {
                    session.setAttribute("error", error);
                }
                break;
            }

            case "setDefaultAddress": {
                redirectTo = "/user?action=address";
                Integer id = parseId(addressIdParam(request));
                if (id != null && userService.setDefaultAddress(id, loggedUser.getUserId())) {
                    session.setAttribute("message", "Đã đặt làm địa chỉ mặc định.");
                } else {
                    session.setAttribute("error", "Không đặt được địa chỉ mặc định!");
                }
                break;
            }

            case "deleteAddress": {
                redirectTo = "/user?action=address";
                Integer id = parseId(addressIdParam(request));
                if (id != null && userService.deleteAddress(id, loggedUser.getUserId())) {
                    session.setAttribute("message", "Đã xóa địa chỉ.");
                } else {
                    session.setAttribute("error", "Không xóa được địa chỉ!");
                }
                break;
            }

            default:
                break;
        }
        response.sendRedirect(request.getContextPath() + redirectTo);
    }

    // =====================================================================
    private Users currentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Users u = (Users) request.getSession().getAttribute("loggedUser");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
        return u;
    }

    /** JSP có thể gửi "addressId" hoặc "id". */
    private String addressIdParam(HttpServletRequest request) {
        String v = request.getParameter("addressId");
        return v != null ? v : request.getParameter("id");
    }

    private Integer parseId(String s) {
        return (s != null && s.matches("\\d{1,9}")) ? Integer.valueOf(s) : null;
    }

    /** Kiểm tra là ảnh thật, cắt vuông giữa, thu về 256x256, lưu dạng data URI JPEG (~20KB). */
    private void handleAvatarUpload(HttpServletRequest request, HttpSession session, Users user)
            throws IOException, ServletException {
        Part filePart = request.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) {
            session.setAttribute("error", "Vui lòng chọn một ảnh!");
            return;
        }
        if (filePart.getSize() > Constants.MAX_IMAGE_SIZE) {
            session.setAttribute("error", "Ảnh quá lớn (tối đa 10MB)!");
            return;
        }

        String name = filePart.getSubmittedFileName() == null ? "" : filePart.getSubmittedFileName().toLowerCase();
        boolean extOk = false;
        for (String ext : Constants.IMAGE_EXTENSIONS) {
            if (name.endsWith("." + ext)) { extOk = true; break; }
        }
        if (!extOk) {
            session.setAttribute("error", "Chỉ hỗ trợ ảnh JPG, PNG hoặc GIF!");
            return;
        }

        BufferedImage src;
        try (InputStream in = filePart.getInputStream()) {
            src = ImageIO.read(in); // trả null nếu không phải ảnh thật / định dạng không hỗ trợ (vd. webp)
        }
        if (src == null) {
            session.setAttribute("error", "File không phải ảnh hợp lệ hoặc định dạng chưa được hỗ trợ (dùng JPG, PNG, GIF).");
            return;
        }

        int side = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - side) / 2;
        int y = (src.getHeight() - side) / 2;
        BufferedImage square = src.getSubimage(x, y, side, side);

        BufferedImage out = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.WHITE); // nền trắng cho ảnh PNG trong suốt
        g.fillRect(0, 0, AVATAR_SIZE, AVATAR_SIZE);
        g.drawImage(square, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(out, "jpg", bos);
        String dataUri = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bos.toByteArray());

        if (userService.updateAvatar(user.getUserId(), dataUri) == null) {
            user.setAvatarUrl(dataUri);
            session.setAttribute("loggedUser", user);
            session.setAttribute("message", "Đổi ảnh đại diện thành công.");
        } else {
            session.setAttribute("error", "Không lưu được ảnh đại diện!");
        }
    }
}
