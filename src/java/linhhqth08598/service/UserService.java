package linhhqth08598.service;

import fourSome.model.Roles;
import fourSome.model.ShippingAddresses;
import fourSome.model.Users;
import fourSome.util.PasswordHasher;
import fourSome.util.Validator;
import linhhqth08598.dao.RolesDAO;
import linhhqth08598.dao.ShippingAddressesDAO;
import linhhqth08598.dao.UserRolesDAO;
import linhhqth08598.dao.UsersDAO;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UsersDAO usersDAO = new UsersDAO();
    private final ShippingAddressesDAO addressDAO = new ShippingAddressesDAO();
    private final UserRolesDAO userRolesDAO = new UserRolesDAO();
    private final RolesDAO rolesDAO = new RolesDAO();

    // =====================================================================
    //  PHẦN NGƯỜI DÙNG: hồ sơ, ảnh đại diện, sổ địa chỉ
    // =====================================================================

    /** Cập nhật họ tên + SĐT. Trả về null nếu thành công, ngược lại là thông báo lỗi. */
    public String updateProfile(int userId, String fullName, String phone) {
        Users currentUser = usersDAO.findById(userId);
        if (currentUser == null) return "Người dùng không tồn tại!";

        String error = validateNameAndPhone(fullName, phone);
        if (error != null) return error;

        String cleanPhone = Validator.isBlank(phone) ? null : phone.trim();
        if (cleanPhone != null) {
            Users checkPhone = usersDAO.findByPhone(cleanPhone);
            if (checkPhone != null && checkPhone.getUserId() != userId) {
                return "Số điện thoại này đã được sử dụng bởi tài khoản khác!";
            }
        }
        currentUser.setFullName(fullName.trim());
        currentUser.setPhoneNumber(cleanPhone);
        return usersDAO.updateProfile(currentUser) ? null : "Lỗi cập nhật hồ sơ!";
    }

    public String updateAvatar(int userId, String avatarUrl) {
        return usersDAO.updateAvatar(userId, avatarUrl) ? null : "Lỗi cập nhật ảnh đại diện!";
    }

    public List<ShippingAddresses> getUserAddresses(int userId) {
        return addressDAO.findByUserId(userId);
    }

    /** Thêm / sửa địa chỉ (addressId > 0 là sửa). Trả về null nếu thành công. */
    public String saveAddress(ShippingAddresses a) {
        if (!Validator.lengthBetween(a.getRecipientName(), 2, 100)) return "Tên người nhận phải từ 2 đến 100 ký tự!";
        if (!Validator.isPhoneVN(a.getPhone())) return "Số điện thoại người nhận không hợp lệ (VD: 0912345678)!";
        if (!Validator.lengthBetween(a.getProvince(), 1, 100)) return "Vui lòng nhập Tỉnh / Thành phố!";
        if (!Validator.lengthBetween(a.getWard(), 1, 100)) return "Vui lòng nhập Phường / Xã!";
        if (!Validator.lengthBetween(a.getDetailedAddress(), 3, 255)) return "Địa chỉ chi tiết phải từ 3 đến 255 ký tự!";

        a.setRecipientName(a.getRecipientName().trim());
        a.setPhone(a.getPhone().trim());
        a.setProvince(a.getProvince().trim());
        a.setWard(a.getWard().trim());
        a.setDetailedAddress(fourSome.util.FormatUtil.cleanAddress(a.getDetailedAddress()));   // gộp dấu phẩy thừa (",,")

        boolean isNew = a.getAddressId() == null || a.getAddressId() <= 0;
        // Địa chỉ đầu tiên của user luôn là mặc định
        if (isNew && addressDAO.findByUserId(a.getUserId()).isEmpty()) a.setIsDefault(true);

        boolean ok = isNew ? addressDAO.insert(a) : addressDAO.update(a);
        if (ok) return null;
        String why = ShippingAddressesDAO.getLastError();
        return "Không lưu được địa chỉ" + (why == null ? "" : " (" + why + ")") + ", vui lòng thử lại!";
    }

    public boolean addAddress(ShippingAddresses address) { return addressDAO.insert(address); }
    public boolean updateAddress(ShippingAddresses address) { return addressDAO.update(address); }
    public boolean deleteAddress(int addressId, int userId) { return addressDAO.delete(addressId, userId); }
    public boolean setDefaultAddress(int addressId, int userId) { return addressDAO.setDefault(addressId, userId); }

    // =====================================================================
    //  PHẦN ADMIN: quản lý người dùng / nhân viên
    // =====================================================================

    public List<Users> getAllUsers() {
        return usersDAO.getAll();
    }

    public List<Users> searchUsers(String keyword, String status, String roleCode, int offset, int limit) {
        return usersDAO.search(keyword, status, roleCode, offset, limit);
    }

    public int countUsers(String keyword, String status, String roleCode) {
        return usersDAO.count(keyword, status, roleCode);
    }

    public Users getUserById(int userId) {
        return usersDAO.findById(userId);
    }

    public Map<Integer, List<Roles>> getRolesOfUsers(List<Integer> userIds) {
        return userRolesDAO.getRolesOfUsers(userIds);
    }

    public List<String> getRoleCodes(int userId) {
        return userRolesDAO.getRoleCodesByUserId(userId);
    }

    public List<Roles> getAllRoles() {
        return rolesDAO.getAll();
    }

    /** UC 326 - Tạo tài khoản nhân viên. Trả về null nếu thành công. */
    public String createUser(String email, String fullName, String phone, String password, List<Integer> roleIds) {
        if (!Validator.isEmail(email)) return "Email không hợp lệ!";
        String error = validateNameAndPhone(fullName, phone);
        if (error != null) return error;
        if (!Validator.isStrongPassword(password)) {
            return "Mật khẩu tối thiểu 8 ký tự, gồm chữ hoa, chữ thường và số!";
        }
        if (roleIds == null || roleIds.isEmpty()) return "Vui lòng chọn ít nhất 1 vai trò!";
        error = validateRoleIds(roleIds);
        if (error != null) return error;

        String cleanEmail = email.trim().toLowerCase();
        if (usersDAO.findByEmail(cleanEmail) != null) return "Email này đã được sử dụng!";
        String cleanPhone = Validator.isBlank(phone) ? null : phone.trim();
        if (cleanPhone != null && usersDAO.findByPhone(cleanPhone) != null) {
            return "Số điện thoại này đã được sử dụng!";
        }

        Users u = new Users();
        u.setEmail(cleanEmail);
        u.setFullName(fullName.trim());
        u.setPhoneNumber(cleanPhone);
        u.setPasswordHash(PasswordHasher.hash(password));
        u.setIsEmailVerified(true);   // tài khoản do admin tạo coi như đã xác thực email
        u.setIsPhoneVerified(false);
        u.setStatus("ACTIVE");

        int newId = usersDAO.insertReturnId(u);
        if (newId <= 0) return "Không tạo được tài khoản, vui lòng thử lại!";
        if (!userRolesDAO.replaceRoles(newId, roleIds)) return "Đã tạo tài khoản nhưng gán vai trò thất bại, hãy sửa lại vai trò.";
        return null;
    }

    /**
     * UC 327 - Cập nhật nhân viên. roleIds = null nghĩa là KHÔNG đổi vai trò.
     * Trả về null nếu thành công.
     */
    public String adminUpdateUser(int userId, String fullName, String phone, List<Integer> roleIds) {
        Users target = usersDAO.findById(userId);
        if (target == null) return "Tài khoản không tồn tại!";

        String error = validateNameAndPhone(fullName, phone);
        if (error != null) return error;

        String cleanPhone = Validator.isBlank(phone) ? null : phone.trim();
        if (cleanPhone != null) {
            Users other = usersDAO.findByPhone(cleanPhone);
            if (other != null && other.getUserId() != userId) return "Số điện thoại này đã được sử dụng bởi tài khoản khác!";
        }
        if (roleIds != null) {
            if (roleIds.isEmpty()) return "Mỗi tài khoản phải có ít nhất 1 vai trò!";
            error = validateRoleIds(roleIds);
            if (error != null) return error;
        }

        if (!usersDAO.adminUpdate(userId, fullName.trim(), cleanPhone)) return "Lỗi cập nhật thông tin tài khoản!";
        if (roleIds != null && !userRolesDAO.replaceRoles(userId, roleIds)) return "Đã lưu thông tin nhưng cập nhật vai trò thất bại!";
        return null;
    }

    /** Đặt lại mật khẩu: sinh mật khẩu tạm ngẫu nhiên. Trả về mảng {errorOrNull, tempPassword}. */
    public String[] resetPassword(int userId) {
        Users target = usersDAO.findById(userId);
        if (target == null) return new String[]{"Tài khoản không tồn tại!", null};
        String temp = generateTempPassword();
        if (!usersDAO.updatePassword(userId, PasswordHasher.hash(temp))) {
            return new String[]{"Không đặt lại được mật khẩu!", null};
        }
        return new String[]{null, temp};
    }

    // Đảo trạng thái tài khoản (Khóa <-> Mở khóa)
    public String toggleUserStatus(int userId) {
        Users targetUser = usersDAO.findById(userId);
        if (targetUser == null) return "Tài khoản không tồn tại!";

        String newStatus = "ACTIVE".equals(targetUser.getStatus()) ? "LOCKED" : "ACTIVE";
        if (usersDAO.updateStatus(userId, newStatus)) {
            return null;
        }
        return "Lỗi cập nhật trạng thái hệ thống!";
    }

    // ---------------------------------------------------------------------
    private String validateNameAndPhone(String fullName, String phone) {
        if (!Validator.lengthBetween(fullName, 2, 100)) return "Họ tên phải từ 2 đến 100 ký tự!";
        if (!Validator.isBlank(phone) && !Validator.isPhoneVN(phone)) {
            return "Số điện thoại không hợp lệ (VD: 0912345678)!";
        }
        return null;
    }

    private String validateRoleIds(List<Integer> roleIds) {
        List<Integer> valid = new ArrayList<>();
        for (Roles r : rolesDAO.getAll()) valid.add(r.getRoleId());
        for (Integer id : roleIds) {
            if (!valid.contains(id)) return "Vai trò được chọn không hợp lệ!";
        }
        return null;
    }

    private String generateTempPassword() {
        final String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        final String lower = "abcdefghijkmnopqrstuvwxyz";
        final String digit = "23456789";
        final String all = upper + lower + digit;
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(rnd.nextInt(upper.length())));
        sb.append(lower.charAt(rnd.nextInt(lower.length())));
        sb.append(digit.charAt(rnd.nextInt(digit.length())));
        for (int i = 0; i < 7; i++) sb.append(all.charAt(rnd.nextInt(all.length())));
        // xáo trộn để 3 ký tự bắt buộc không luôn đứng đầu
        char[] c = sb.toString().toCharArray();
        for (int i = c.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            char t = c[i]; c[i] = c[j]; c[j] = t;
        }
        return new String(c);
    }
}