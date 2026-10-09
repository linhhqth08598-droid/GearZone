package DAO;

import Models.CartItem;
import Url.DBConnect;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class OrderDAO {

    // 1. Hàm tạo đơn hàng mới, lưu chi tiết và xóa giỏ hàng
    public String createOrder(int userId, Integer promotionId, double totalAmount, 
                               String shippingName, String shippingPhone, String shippingAddress, 
                               String paymentMethod, List<CartItem> cartItems) {
        
        // Thêm N đằng trước chuỗi tiếng Việt để fix lỗi font trong SQL Server
        String sqlOrder = "INSERT INTO Orders (UserID, PromotionID, OrderCode, TotalAmount, ShippingName, ShippingPhone, ShippingAddress, PaymentMethod, PaymentStatus, OrderStatus) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Chưa thanh toán', N'Chờ xác nhận')";
                        
        String sqlOrderDetail = "INSERT INTO Order_Details (OrderID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE Products SET StockQuantity = StockQuantity - ? WHERE ProductID = ?";
        String sqlClearCart = "DELETE FROM Cart_Items WHERE CartID = (SELECT CartID FROM Carts WHERE UserID = ?)";

        Connection con = null;
        try {
            con = DBConnect.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn dữ liệu

            // 1. Tạo mã đơn hàng ngẫu nhiên (VD: GZ-A1B2C3D4)
            String orderCode = "GZ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 2. Lưu vào bảng Orders
            int newOrderId = -1;
            try (PreparedStatement psOrder = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, userId);
                if (promotionId != null && promotionId > 0) {
                    psOrder.setInt(2, promotionId);
                } else {
                    psOrder.setNull(2, java.sql.Types.INTEGER);
                }
                psOrder.setString(3, orderCode);
                psOrder.setDouble(4, totalAmount);
                psOrder.setString(5, shippingName);
                psOrder.setString(6, shippingPhone);
                psOrder.setString(7, shippingAddress);
                psOrder.setString(8, paymentMethod);
                
                psOrder.executeUpdate();
                
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        newOrderId = rs.getInt(1);
                    }
                }
            }

            if (newOrderId == -1) {
                con.rollback();
                return null;
            }

            // 3. Lưu vào bảng Order_Details và trừ số lượng trong kho
            try (PreparedStatement psDetail = con.prepareStatement(sqlOrderDetail);
                 PreparedStatement psStock = con.prepareStatement(sqlUpdateStock)) {
                
                for (CartItem item : cartItems) {
                    // Lưu thông tin chi tiết từng sản phẩm
                    psDetail.setInt(1, newOrderId);
                    psDetail.setInt(2, item.getProductId());
                    psDetail.setInt(3, item.getQuantity());
                    psDetail.setDouble(4, item.getBasePrice());
                    psDetail.addBatch(); // Gom lệnh
                    
                    // Trừ số lượng tồn kho của sản phẩm đó
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.addBatch();
                }
                psDetail.executeBatch();
                psStock.executeBatch();
            }

            // 4. Cập nhật lượt dùng khuyến mãi (nếu khách có dùng mã)
            if (promotionId != null && promotionId > 0) {
                try (PreparedStatement psPromo = con.prepareStatement("UPDATE Promotions SET UsedCount = UsedCount + 1 WHERE PromotionID = ?")) {
                    psPromo.setInt(1, promotionId);
                    psPromo.executeUpdate();
                }
            }

            // 5. Làm sạch giỏ hàng của User sau khi đặt thành công
            try (PreparedStatement psClear = con.prepareStatement(sqlClearCart)) {
                psClear.setInt(1, userId);
                psClear.executeUpdate();
            }

            // Nếu tất cả các bước trên đều thành công -> Lưu thật vào Database
            con.commit();
            return orderCode; // Trả về mã đơn để làm QR Code

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Hoàn tác nếu có lỗi giữa chừng
            } catch (SQLException ex) { ex.printStackTrace(); }
            return null;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // Lấy danh sách Đơn Hàng của 1 Khách hàng (Hiển thị trang my-orders và order-detail)
    public List<Models.Order> getOrdersByUser(int userId) {
        List<Models.Order> list = new java.util.ArrayList<>();
        // Bổ sung thêm các cột ShippingName, ShippingPhone, ShippingAddress vào câu lệnh SELECT
        String sql = "SELECT OrderID, UserID, PromotionID, OrderCode, TotalAmount, ShippingName, ShippingPhone, ShippingAddress, PaymentMethod, PaymentStatus, OrderStatus, CreatedAt FROM Orders WHERE UserID = ? ORDER BY CreatedAt DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Models.Order order = new Models.Order();
                    order.setOrderId(rs.getInt("OrderID"));
                    order.setUserId(rs.getInt("UserID"));
                    order.setPromotionId(rs.getInt("PromotionID"));
                    order.setOrderCode(rs.getString("OrderCode"));
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    // THÊM 3 DÒNG GÁN DỮ LIỆU GIAO HÀNG NÀY VÀO:
                    order.setShippingName(rs.getString("ShippingName"));
                    order.setShippingPhone(rs.getString("ShippingPhone"));
                    order.setShippingAddress(rs.getString("ShippingAddress"));
                    
                    order.setOrderStatus(rs.getString("OrderStatus"));
                    order.setPaymentStatus(rs.getString("PaymentStatus"));
                    order.setPaymentMethod(rs.getString("PaymentMethod"));
                    order.setCreatedAt(rs.getDate("CreatedAt"));
                    list.add(order);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    // Lấy TẤT CẢ đơn hàng (Dành cho Admin)
    public List<Models.Order> getAllOrders() {
        List<Models.Order> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY CreatedAt DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Models.Order order = new Models.Order();
                order.setOrderId(rs.getInt("OrderID"));
                order.setUserId(rs.getInt("UserID"));
                order.setOrderCode(rs.getString("OrderCode"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setShippingName(rs.getString("ShippingName"));
                order.setShippingPhone(rs.getString("ShippingPhone"));
                order.setPaymentMethod(rs.getString("PaymentMethod"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setCreatedAt(rs.getDate("CreatedAt"));
                list.add(order);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Lấy chi tiết các sản phẩm trong 1 đơn hàng
    public List<Models.OrderDetail> getOrderDetails(int orderId) {
        List<Models.OrderDetail> list = new java.util.ArrayList<>();
        String sql = "SELECT od.*, p.ProductName FROM Order_Details od JOIN Products p ON od.ProductID = p.ProductID WHERE od.OrderID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Models.OrderDetail detail = new Models.OrderDetail();
                    detail.setOrderDetailId(rs.getInt("OrderDetailID"));
                    detail.setOrderId(rs.getInt("OrderID"));
                    detail.setProductId(rs.getInt("ProductID"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setUnitPrice(rs.getDouble("UnitPrice"));
                    detail.setProductName(rs.getString("ProductName")); // Lấy thêm tên SP
                    list.add(detail);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Cập nhật trạng thái Đơn hàng (Duyệt đơn)
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE Orders SET OrderStatus = ?, UpdatedAt = GETDATE() WHERE OrderID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}