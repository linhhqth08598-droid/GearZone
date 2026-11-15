package linhhqth08598.controller;

import fourSome.model.PartReviews;
import fourSome.model.Users;
import linhhqth08598.dao.PartReviewsDAO;
import linhhqth08598.service.ReviewService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.Base64;

/**
 * UC 296–302. action: add (linh kiện: chất lượng + hiệu năng + nhận xét + ảnh) | reviewBuild | reviewAssembly | reportInfo
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/review"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 6) // 5MB / ảnh
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        String action = request.getParameter("action");
        if (action == null) action = "add";
        String redirect = request.getContextPath() + "/home";

        try {
            switch (action) {
                case "add": case "reportInfo": {
                    int partId = Integer.parseInt(request.getParameter("partId"));
                    redirect = request.getContextPath() + "/product?action=detail&id=" + partId + "#reviews";
                    if ("add".equals(action)) addPartReview(request, session, user, partId);
                    else {
                        String err = reviewService.reportPartInfo(user.getUserId(), partId, request.getParameter("content"));
                        flash(session, err, "Đã gửi báo cáo, nhân viên sẽ kiểm tra lại thông tin. Cảm ơn bạn!");
                    }
                    break;
                }
                case "reviewBuild": {
                    redirect = request.getContextPath() + "/user/pc-build-history";
                    String err = reviewService.reviewBuild(Integer.parseInt(request.getParameter("buildId")), user.getUserId(),
                            Integer.parseInt(request.getParameter("rating")), request.getParameter("comment"));
                    flash(session, err, "Cảm ơn bạn đã đánh giá cấu hình!");
                    break;
                }
                case "reviewAssembly": {
                    redirect = request.getContextPath() + "/assembly";
                    String err = reviewService.reviewAssembly(Integer.parseInt(request.getParameter("assemblyId")), user.getUserId(),
                            Integer.parseInt(request.getParameter("rating")), request.getParameter("comment"));
                    flash(session, err, "Cảm ơn bạn đã đánh giá dịch vụ lắp ráp!");
                    break;
                }
                default:
                    session.setAttribute("error", "Thao tác không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi khi gửi đánh giá, vui lòng thử lại!");
        }
        response.sendRedirect(redirect);
    }

    private void addPartReview(HttpServletRequest request, HttpSession session, Users user, int partId)
            throws IOException, ServletException {
        PartReviews review = new PartReviews();
        review.setUserId(user.getUserId());
        review.setPartId(partId);
        review.setQualityRating(Double.parseDouble(request.getParameter("qualityRating"))); // giữ nguyên 4.5 (decimal(2,1))
        String perf = request.getParameter("performanceRating");
        if (perf != null && perf.matches("[1-5]")) review.setPerformanceRating(Integer.parseInt(perf));
        review.setComment(request.getParameter("comment"));

        String base64Image = null;
        Part filePart = request.getPart("reviewImage");
        if (filePart != null && filePart.getSize() > 0) {
            String type = filePart.getContentType();
            if (type == null || !type.startsWith("image/")) {
                session.setAttribute("error", "Chỉ được đính kèm file ảnh!");
                return;
            }
            base64Image = "data:" + type + ";base64," + Base64.getEncoder().encodeToString(filePart.getInputStream().readAllBytes());
        }

        String error = reviewService.addReview(review);
        if (error == null) {
            if (base64Image != null) new PartReviewsDAO().insertReviewImage(review.getReviewId(), base64Image);
            session.setAttribute("message", "Cảm ơn bạn đã gửi đánh giá!");
        } else {
            session.setAttribute("error", error);
        }
    }

    private void flash(HttpSession session, String error, String ok) {
        if (error == null) session.setAttribute("message", ok);
        else session.setAttribute("error", error);
    }
}
