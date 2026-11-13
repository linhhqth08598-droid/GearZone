package linhhqth08598.service;

import fourSome.model.UserWishlists;
import linhhqth08598.dao.UserWishlistsDAO;
import java.util.List;

public class WishlistService {
    private UserWishlistsDAO wishlistDAO = new UserWishlistsDAO();

    public List<UserWishlists> getWishlist(int userId) {
        return wishlistDAO.getWishlistByUserId(userId);
    }

    public String addWishlist(int userId, int partId) {
        if (wishlistDAO.addToWishlist(userId, partId)) {
            return null; // Thành công
        }
        return "Lỗi khi thêm vào danh sách yêu thích!";
    }

    public String removeWishlist(int userId, int partId) {
        if (wishlistDAO.removeFromWishlist(userId, partId)) {
            return null; // Thành công
        }
        return "Lỗi khi xóa khỏi danh sách yêu thích!";
    }
}