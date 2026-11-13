package linhhqth08598.service;

import fourSome.model.ShippingAddresses;
import fourSome.model.Users;
import linhhqth08598.dao.ShippingAddressesDAO;
import linhhqth08598.dao.UsersDAO;
import java.util.List;

public class UserService {
    private UsersDAO usersDAO = new UsersDAO();
    private ShippingAddressesDAO addressDAO = new ShippingAddressesDAO();

    public String updateProfile(int userId, String fullName, String phone) {
        Users currentUser = usersDAO.findById(userId);
        if (currentUser == null) return "Người dùng không tồn tại!";
        
        // Kiểm tra xem số điện thoại nhập vào có bị trùng với người KHI C khác không
        Users checkPhone = usersDAO.findByPhone(phone);
        if (checkPhone != null && checkPhone.getUserId() != userId) {
            return "Số điện thoại này đã được sử dụng bởi tài khoản khác!";
        }

        currentUser.setFullName(fullName);
        currentUser.setPhoneNumber(phone);
        if (usersDAO.updateProfile(currentUser)) {
            return null; // Thành công
        }
        return "Lỗi cập nhật hồ sơ!";
    }

    public String updateAvatar(int userId, String avatarUrl) {
        if (usersDAO.updateAvatar(userId, avatarUrl)) return null;
        return "Lỗi cập nhật ảnh đại diện!";
    }

    // Các hàm bọc của Sổ địa chỉ
    public List<ShippingAddresses> getUserAddresses(int userId) { return addressDAO.findByUserId(userId); }
    public boolean addAddress(ShippingAddresses address) { return addressDAO.insert(address); }
    public boolean updateAddress(ShippingAddresses address) { return addressDAO.update(address); }
    public boolean deleteAddress(int addressId, int userId) { return addressDAO.delete(addressId, userId); }
    public boolean setDefaultAddress(int addressId, int userId) { return addressDAO.setDefault(addressId, userId); }
    
    // Lấy toàn bộ danh sách User cho Admin
    public List<Users> getAllUsers() {
        return usersDAO.getAll(); // Đã có sẵn trong file DAO gốc của bạn
    }

    // Đảo trạng thái tài khoản (Khóa <-> Mở khóa)
    public String toggleUserStatus(int userId) {
        Users targetUser = usersDAO.findById(userId);
        if (targetUser == null) return "Tài khoản không tồn tại!";
        
        String newStatus = targetUser.getStatus().equals("ACTIVE") ? "LOCKED" : "ACTIVE";
        
        if (usersDAO.updateStatus(userId, newStatus)) {
            return null; // Thành công
        }
        return "Lỗi cập nhật trạng thái hệ thống!";
    }
}