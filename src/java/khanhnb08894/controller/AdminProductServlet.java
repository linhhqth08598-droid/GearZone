package khanhnb08894.controller;

import fourSome.model.Parts;
import fourSome.model.Users;
import khanhnb08894.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

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
    private PartMediaDAO mediaDAO = new PartMediaDAO();
    private SpecMonitorDAO monitorDAO = new SpecMonitorDAO();
    private SpecPeripheralDAO peripheralDAO = new SpecPeripheralDAO();

    private CategoriesDAO categoriesDAO = new CategoriesDAO();
    private BrandsDAO brandsDAO = new BrandsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        // Nạp sẵn danh mục và thương hiệu dùng chung cho cả danh sách (để hiển thị trong Modal Thêm/Sửa)
        request.setAttribute("categories", categoriesDAO.getAll());
        request.setAttribute("brands", brandsDAO.getAll());

        if ("/admin/products".equals(action)) {
            // Đảm bảo tên attribute là "parts" để khớp với vòng cờ lặp trong products.jsp của nhóm
            request.setAttribute("parts", partsDAO.getAll());
            request.getRequestDispatcher("/views/khanhnb08894/admin/products.jsp").forward(request, response);

        } else if ("/admin/product-form".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int partId = Integer.parseInt(idStr);
                    // Nếu DAO của bạn có hàm findById thì gọi findById(partId), nếu dùng mã sản phẩm thì giữ nguyên getPartByProductCode
                    request.setAttribute("part", partsDAO.findById(partId));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
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
            // 1. CHỨC NĂNG THÊM MỚI (Hỗ trợ cả "save" từ form riêng và "saveProduct" từ modal)
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

                // =========================================================================
                // LƯU THÔNG SỐ CHUYÊN SÂU AN TOÀN (Tránh lỗi văng ngoại lệ khi dùng Modal thêm nhanh)
                // =========================================================================
                if (partId > 0) {
                    if (categoryId == 1) { // 1. CPU
                        String socketStr = request.getParameter("socketId");
                        if (socketStr != null && !socketStr.trim().isEmpty()) {
                            int socketId = Integer.parseInt(socketStr);
                            int cores = Integer.parseInt(request.getParameter("cores"));
                            int threads = Integer.parseInt(request.getParameter("threads"));
                            double baseClock = Double.parseDouble(request.getParameter("baseClock"));
                            double boostClock = Double.parseDouble(request.getParameter("boostClock"));
                            int tdp = Integer.parseInt(request.getParameter("tdp"));
                            cpuDAO.insert(partId, socketId, cores, threads, baseClock, boostClock, tdp);
                        }
                    } else if (categoryId == 2) { // 2. Mainboard
                        String socketStr = request.getParameter("socketId");
                        if (socketStr != null && !socketStr.trim().isEmpty()) {
                            int socketId = Integer.parseInt(socketStr);
                            String chipset = request.getParameter("chipset");
                            String ramStandard = request.getParameter("ramStandard");
                            int ramSlots = Integer.parseInt(request.getParameter("ramSlots"));
                            mbDAO.insert(partId, socketId, chipset, ramStandard, ramSlots);
                        }
                    } else if (categoryId == 3) { // 3. RAM
                        String ddrType = request.getParameter("ddrType");
                        if (ddrType != null && !ddrType.trim().isEmpty()) {
                            int capacityGb = Integer.parseInt(request.getParameter("capacityGb"));
                            int busSpeedMhz = Integer.parseInt(request.getParameter("busSpeedMhz"));
                            ramDAO.insert(partId, ddrType, capacityGb, busSpeedMhz);
                        }
                    } else if (categoryId == 4) { // 4. VGA
                        String gpuChipset = request.getParameter("gpuChipset");
                        if (gpuChipset != null && !gpuChipset.trim().isEmpty()) {
                            int vramCapacityGb = Integer.parseInt(request.getParameter("vramCapacityGb"));
                            int recommendedPsuWatts = Integer.parseInt(request.getParameter("recommendedPsuWatts"));
                            vgaDAO.insert(partId, gpuChipset, vramCapacityGb, recommendedPsuWatts);
                        }
                    } else if (categoryId == 5) { // 5. SSD / HDD
                        String storageType = request.getParameter("storageType");
                        if (storageType != null && !storageType.trim().isEmpty()) {
                            int capacityGb = Integer.parseInt(request.getParameter("capacityGb"));
                            storageDAO.insert(partId, storageType, capacityGb);
                        }
                    } else if (categoryId == 6) { // 6. PSU (Nguồn)
                        String wattageStr = request.getParameter("wattage");
                        if (wattageStr != null && !wattageStr.trim().isEmpty()) {
                            int wattage = Integer.parseInt(wattageStr);
                            String efficiencyRating = request.getParameter("efficiencyRating");
                            String modularType = request.getParameter("modularType");
                            psuDAO.insert(partId, wattage, efficiencyRating, modularType);
                        }
                    } else if (categoryId == 7) { // 7. Case
                        String caseFormFactor = request.getParameter("caseFormFactor");
                        if (caseFormFactor != null && !caseFormFactor.trim().isEmpty()) {
                            caseDAO.insert(partId, caseFormFactor);
                        }
                    } else if (categoryId == 8) { // 8. Cooler
                        String coolerType = request.getParameter("coolerType");
                        if (coolerType != null && !coolerType.trim().isEmpty()) {
                            int maxTdp = Integer.parseInt(request.getParameter("maxTdp"));
                            coolerDAO.insert(partId, coolerType, maxTdp);
                        }
                    } else if (categoryId == 9) { // 9. Màn hình
                        String screenSize = request.getParameter("screenSize");
                        if (screenSize != null && !screenSize.trim().isEmpty()) {
                            String resolution = request.getParameter("resolution");
                            int refreshRate = Integer.parseInt(request.getParameter("refreshRate"));
                            String panelType = request.getParameter("panelType");
                            monitorDAO.insert(partId, screenSize, resolution, refreshRate, panelType);
                        }
                    } else if (categoryId == 10) { // 10. Gaming Gear
                        String gearType = request.getParameter("gearType");
                        if (gearType != null && !gearType.trim().isEmpty()) {
                            String connectivity = request.getParameter("connectivity");
                            String switchOrDpi = request.getParameter("switchOrDpi");
                            peripheralDAO.insert(partId, gearType, connectivity, switchOrDpi);
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
                    session.setAttribute("message", "Cập nhật linh kiện thành công!");
                } else {
                    session.setAttribute("error", "Không cập nhật được (mã sản phẩm có thể bị trùng).");
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            session = request.getSession();
            session.setAttribute("error", "Lỗi lưu linh kiện: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }
}
