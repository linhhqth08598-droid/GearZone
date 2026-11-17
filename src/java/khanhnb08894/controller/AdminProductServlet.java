package khanhnb08894.controller;

import fourSome.model.Parts;
import fourSome.model.Users;
import khanhnb08894.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products", "/admin/product-form"})
public class AdminProductServlet extends HttpServlet {

    private PartsDAO partsDAO = new PartsDAO();
    private SpecCpuDAO cpuDAO = new SpecCpuDAO();
    private SpecMainboardDAO mbDAO = new SpecMainboardDAO();
    private SpecRamDAO ramDAO = new SpecRamDAO();
    private SpecVgaDAO vgaDAO = new SpecVgaDAO();
    private SpecStorageDAO storageDAO = new SpecStorageDAO();
    private SpecPsuDAO psuDAO = new SpecPsuDAO();
    private SpecCaseDAO caseDAO = new SpecCaseDAO();
    private SpecCoolerDAO coolerDAO = new SpecCoolerDAO();
    private khanhnb08894.dao.PartMediaDAO mediaDAO = new khanhnb08894.dao.PartMediaDAO();
    private SpecMonitorDAO monitorDAO = new SpecMonitorDAO();
    private SpecPeripheralDAO peripheralDAO = new SpecPeripheralDAO();

    private CategoriesDAO categoriesDAO = new CategoriesDAO();
    private BrandsDAO brandsDAO = new BrandsDAO();

    private String saveImage(HttpServletRequest request, int partId) throws IOException, ServletException {
        String url = null;
        Part filePart = request.getPart("imageFile");
        if (filePart == null || filePart.getSize() == 0) {
            filePart = request.getPart("productImage");
        }

        if (filePart != null && filePart.getSize() > 0) {
            String ct = filePart.getContentType() == null ? "" : filePart.getContentType().toLowerCase();
            if (!java.util.Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif").contains(ct)) {
                return "Ảnh phải là jpg, png, webp hoặc gif!";
            }
            if (filePart.getSize() > 2 * 1024 * 1024) {
                return "Ảnh tối đa 2MB!";
            }

            byte[] bytes = filePart.getInputStream().readAllBytes();
            url = "data:" + ct + ";base64," + java.util.Base64.getEncoder().encodeToString(bytes);
        }

        if (url == null) {
            return null;
        }
        return mediaDAO.setPrimary(partId, url) ? null : "Không lưu được ảnh!";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        request.setAttribute("categories", categoriesDAO.getAll());
        request.setAttribute("brands", brandsDAO.getAll());

        if ("/admin/products".equals(action)) {
            // Nhận dữ liệu từ form tìm kiếm/lọc
            String keyword = request.getParameter("keyword");
            String catStr = request.getParameter("categoryId");
            String brandStr = request.getParameter("brandId");

            Integer categoryId = (catStr != null && !catStr.trim().isEmpty()) ? Integer.parseInt(catStr) : null;
            Integer brandId = (brandStr != null && !brandStr.trim().isEmpty()) ? Integer.parseInt(brandStr) : null;

            List<Parts> partsList;
            
            // Nếu có nhập từ khóa hoặc chọn danh mục/thương hiệu thì gọi hàm Lọc
            if ((keyword != null && !keyword.trim().isEmpty()) || categoryId != null || brandId != null) {
                partsList = partsDAO.filterAdminParts(categoryId, brandId, keyword);
            } else {
                // Nếu không lọc thì lấy toàn bộ
                partsList = partsDAO.getAllWithImages();
            }

            request.setAttribute("parts", partsList);
            
            // Giữ lại giá trị trên form để hiển thị không bị mất sau khi ấn Lọc
            request.setAttribute("paramKeyword", keyword);
            request.setAttribute("paramCategoryId", categoryId);
            request.setAttribute("paramBrandId", brandId);

            request.getRequestDispatcher("/views/khanhnb08894/admin/products.jsp").forward(request, response);
            
        } else if ("/admin/product-form".equals(action)) {
            // THÊM ĐOẠN NÀY ĐỂ XỬ LÝ KHI TRUY CẬP /admin/product-form
            // Lấy thêm danh sách Series (dòng sản phẩm) để hiển thị trong form nếu cần
            ProductSeriesDAO seriesDAO = new ProductSeriesDAO();
            request.setAttribute("seriesList", seriesDAO.getAll());
            
            // Chuyển hướng tới file JSP chứa form thêm mới linh kiện
            request.getRequestDispatcher("/views/khanhnb08894/admin/product_form.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            // 1. CHỨC NĂNG THÊM MỚI
            if ("save".equals(action) || "saveProduct".equals(action)) {
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                int brandId = Integer.parseInt(request.getParameter("brandId"));
                String productCode = request.getParameter("productCode");
                String partName = request.getParameter("partName");
                double basePrice = Double.parseDouble(request.getParameter("basePrice"));

                String saleStr = request.getParameter("salePrice");
                Double salePrice = (saleStr != null && !saleStr.trim().isEmpty()) ? Double.parseDouble(saleStr) : null;

                int warrantyMonths = Integer.parseInt(request.getParameter("warrantyMonths"));
                String status = request.getParameter("status");
                if (status == null || status.trim().isEmpty()) {
                    status = "ACTIVE";
                }

                Parts part = new Parts();
                part.setCategoryId(categoryId);
                part.setBrandId(brandId);
                part.setProductCode(productCode);
                part.setPartName(partName);
                part.setBasePrice(basePrice);
                part.setSalePrice(salePrice);
                part.setWarrantyMonths(warrantyMonths);
                part.setStatus(status);

                int partId = partsDAO.insertAndReturnId(part);

                if (partId > 0) {
                    String imageError = saveImage(request, partId);
                    if (imageError != null) {
                        session.setAttribute("error", imageError);
                    }

                    if (categoryId == 1) {
                        String socketStr = request.getParameter("socketId");
                        if (socketStr != null && !socketStr.trim().isEmpty()) {
                            cpuDAO.insert(partId, Integer.parseInt(socketStr), Integer.parseInt(request.getParameter("cores")),
                                    Integer.parseInt(request.getParameter("threads")), Double.parseDouble(request.getParameter("baseClock")),
                                    Double.parseDouble(request.getParameter("boostClock")), Integer.parseInt(request.getParameter("tdp")));
                        }
                    } else if (categoryId == 2) {
                        String socketStr = request.getParameter("socketId");
                        if (socketStr != null && !socketStr.trim().isEmpty()) {
                            mbDAO.insert(partId, Integer.parseInt(socketStr), request.getParameter("chipset"),
                                    request.getParameter("ramStandard"), Integer.parseInt(request.getParameter("ramSlots")));
                        }
                    } else if (categoryId == 3) {
                        String ddrType = request.getParameter("ddrType");
                        if (ddrType != null && !ddrType.trim().isEmpty()) {
                            ramDAO.insert(partId, ddrType, Integer.parseInt(request.getParameter("capacityGb")), Integer.parseInt(request.getParameter("busSpeedMhz")));
                        }
                    } else if (categoryId == 4) {
                        String gpuChipset = request.getParameter("gpuChipset");
                        if (gpuChipset != null && !gpuChipset.trim().isEmpty()) {
                            vgaDAO.insert(partId, gpuChipset, Integer.parseInt(request.getParameter("vramCapacityGb")), Integer.parseInt(request.getParameter("recommendedPsuWatts")));
                        }
                    } else if (categoryId == 5) {
                        String storageType = request.getParameter("storageType");
                        if (storageType != null && !storageType.trim().isEmpty()) {
                            storageDAO.insert(partId, storageType, Integer.parseInt(request.getParameter("capacityGb")));
                        }
                    } else if (categoryId == 6) {
                        String wattageStr = request.getParameter("wattage");
                        if (wattageStr != null && !wattageStr.trim().isEmpty()) {
                            psuDAO.insert(partId, Integer.parseInt(wattageStr), request.getParameter("efficiencyRating"), request.getParameter("modularType"));
                        }
                    } else if (categoryId == 7) {
                        String caseFormFactor = request.getParameter("caseFormFactor");
                        if (caseFormFactor != null && !caseFormFactor.trim().isEmpty()) {
                            caseDAO.insert(partId, caseFormFactor);
                        }
                    } else if (categoryId == 8) {
                        String coolerType = request.getParameter("coolerType");
                        if (coolerType != null && !coolerType.trim().isEmpty()) {
                            coolerDAO.insert(partId, coolerType, Integer.parseInt(request.getParameter("maxTdp")));
                        }
                    } else if (categoryId == 9) {
                        String screenSize = request.getParameter("screenSize");
                        if (screenSize != null && !screenSize.trim().isEmpty()) {
                            monitorDAO.insert(partId, screenSize, request.getParameter("resolution"), Integer.parseInt(request.getParameter("refreshRate")), request.getParameter("panelType"));
                        }
                    } else if (categoryId == 10) {
                        String gearType = request.getParameter("gearType");
                        if (gearType != null && !gearType.trim().isEmpty()) {
                            peripheralDAO.insert(partId, gearType, request.getParameter("connectivity"), request.getParameter("switchOrDpi"));
                        }
                    }
                }

                session.setAttribute("message", "Thêm linh kiện thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/products");

            // 2. CHỨC NĂNG CẬP NHẬT (SỬA)
            } else if ("updateProduct".equals(action)) {
                int partId = Integer.parseInt(request.getParameter("partId"));
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                int brandId = Integer.parseInt(request.getParameter("brandId"));
                String productCode = request.getParameter("productCode").trim();
                String partName = request.getParameter("partName").trim();
                double basePrice = Double.parseDouble(request.getParameter("basePrice"));

                String saleStr = request.getParameter("salePrice");
                Double salePrice = (saleStr != null && !saleStr.trim().isEmpty()) ? Double.parseDouble(saleStr) : null;

                int warrantyMonths = Integer.parseInt(request.getParameter("warrantyMonths"));
                String description = request.getParameter("description");

                Parts part = new Parts();
                part.setPartId(partId);
                part.setCategoryId(categoryId);
                part.setBrandId(brandId);
                part.setProductCode(productCode);
                part.setPartName(partName);
                part.setBasePrice(basePrice);
                part.setSalePrice(salePrice);
                part.setWarrantyMonths(warrantyMonths);
                part.setDescription(description);

                if (partsDAO.update(part)) {
                    saveImage(request, partId);
                    session.setAttribute("message", "Cập nhật linh kiện thành công!");
                } else {
                    session.setAttribute("error", "Không cập nhật được.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");

            // 3. CHỨC NĂNG ẨN (SOFT DELETE)
            } else if ("deleteProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.softDelete(id)) {
                    session.setAttribute("message", "Đã ẩn linh kiện thành công!");
                } else {
                    session.setAttribute("error", "Không thể ẩn linh kiện này.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");

            // 4. CHỨC NĂNG HIỆN LẠI (RESTORE)
            } else if ("restoreProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.setStatus(id, "ACTIVE")) {
                    session.setAttribute("message", "Đã hiển thị lại linh kiện!");
                } else {
                    session.setAttribute("error", "Không thể thay đổi trạng thái.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");

            // 5. CHỨC NĂNG XÓA VĨNH VIỄN (HARD DELETE)
            } else if ("hardDeleteProduct".equals(action)) {
                String idParam = request.getParameter("partId");
                if (idParam != null && !idParam.trim().isEmpty()) {
                    int id = Integer.parseInt(idParam);
                    if (partsDAO.delete(id)) {
                        session.setAttribute("message", "Đã xóa vĩnh viễn linh kiện thành công!");
                    } else {
                        session.setAttribute("error", "Không thể xóa vĩnh viễn (đang có dữ liệu liên quan ở đơn hàng/giỏ hàng).");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session = request.getSession();
            session.setAttribute("error", "Lỗi xử lý linh kiện: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }
}