package Service;

import DAO.UserDAO;
import Models.User;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public String register(String username, String password, String confirm, String email, String fullName, String phone) {
        if (!password.equals(confirm)) {
            return "Mật khẩu xác nhận không khớp!";
        }
        if (userDAO.checkExist(username, email)) {
            return "Tên đăng nhập hoặc Email đã tồn tại!";
        }
        User newUser = new User(4, username, password, email, fullName, phone); // Chưa hash pass cho đơn giản
        if (userDAO.insertUser(newUser)) {
            return "SUCCESS";
        }
        return "Lỗi hệ thống khi đăng ký!";
    }

    public User login(String username, String password) {
        return userDAO.checkLogin(username, password);
    }
}