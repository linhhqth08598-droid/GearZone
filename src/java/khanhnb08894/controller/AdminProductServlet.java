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
        request.setAttribute("adminLayout", Boolean.TRUE);
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
            request.setAttribute("partSpecsJson", buildPartSpecsJson(partsList));
            
            // Giữ lại giá trị trên form để hiển thị không bị mất sau khi ấn Lọc
            request.setAttribute("paramKeyword", keyword);
            request.setAttribute("paramCategoryId", categoryId);
            request.setAttribute("paramBrandId", brandId);

            request.getRequestDispatcher("/views/khanhnb08894/admin/products.jsp").forward(request, response);
            
        } else if ("/admin/product-form".equals(action)) {
            ProductSeriesDAO seriesDAO = new ProductSeriesDAO();
            request.setAttribute("seriesList", seriesDAO.getAll());
            String editIdParam = request.getParameter("id");
            if (editIdParam != null && !editIdParam.trim().isEmpty()) {
                try {
                    int editId = Integer.parseInt(editIdParam.trim());
                    Parts editPart = partsDAO.findById(editId);
                    if (editPart != null) {
                        request.setAttribute("editPart", editPart);
                    }
                } catch (NumberFormatException ignored) {
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
            // 1. CHỨC NĂNG THÊM MỚI
            if ("save".equals(action) || "saveAndContinue".equals(action) || "saveProduct".equals(action)) {
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
                if ("saveAndContinue".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/admin/product-form");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                }
                return;

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
                System.err.println("[AdminProductServlet] updateProduct partId=" + partId
                        + ", basePrice=" + basePrice
                        + ", saleStr=" + saleStr
                        + ", salePrice=" + salePrice);

                int warrantyMonths = Integer.parseInt(request.getParameter("warrantyMonths"));
                String description = request.getParameter("description");
                String statusParam = request.getParameter("status");

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
                    if (statusParam != null && statusParam.matches("ACTIVE|HIDDEN|DISCONTINUED")) {
                        partsDAO.setStatus(partId, statusParam);
                    }
                    syncSpecByCategory(request, partId, categoryId, true);
                    saveImage(request, partId);
                    if (adminUser != null) {
                        new linhhqth08598.service.AuditLogService().logAction(
                                adminUser.getUserId(), "UPDATE_PRODUCT", "parts", partId,
                                "Cập nhật linh kiện: " + partName + " (" + productCode + ")");
                    }
                    session.setAttribute("message", "Cập nhật linh kiện thành công!");
                } else {
                    String detailErr = partsDAO.getLastError();
                    System.err.println("[AdminProductServlet] updateProduct thất bại partId=" + partId + ": " + detailErr);
                    session.setAttribute("error", "Không cập nhật được linh kiện #" + partId
                            + (detailErr != null && !detailErr.isEmpty() ? (" (" + detailErr + ")") : "."));
                }
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                response.setHeader("Location", request.getContextPath() + "/admin/products");
                return;

            // 3. CHỨC NĂNG ẨN (SOFT DELETE)
            } else if ("deleteProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.softDelete(id)) {
                    session.setAttribute("message", "Đã ẩn linh kiện thành công!");
                } else {
                    session.setAttribute("error", "Không thể ẩn linh kiện này.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;

            // 4. CHỨC NĂNG HIỆN LẠI (RESTORE)
            } else if ("restoreProduct".equals(action)) {
                int id = Integer.parseInt(request.getParameter("partId"));
                if (partsDAO.setStatus(id, "ACTIVE")) {
                    session.setAttribute("message", "Đã hiển thị lại linh kiện!");
                } else {
                    session.setAttribute("error", "Không thể thay đổi trạng thái.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;

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
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            session = request.getSession();
            session.setAttribute("error", "Lỗi xử lý linh kiện: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void syncSpecByCategory(HttpServletRequest request, int partId, int categoryId, boolean isUpdate) {
        try {
            if (categoryId == 1) {
                String socketStr = request.getParameter("socketId");
                String coresStr = request.getParameter("cores");
                if (socketStr != null && !socketStr.trim().isEmpty() && coresStr != null && !coresStr.trim().isEmpty()) {
                    int socketId = Integer.parseInt(socketStr.trim());
                    int cores = Integer.parseInt(coresStr.trim());
                    int threads = parseOrDefault(request.getParameter("threads"), cores * 2);
                    double baseClock = parseDoubleOrDefault(request.getParameter("baseClock"), 2.5);
                    double boostClock = parseDoubleOrDefault(request.getParameter("boostClock"), 4.0);
                    int tdp = parseOrDefault(request.getParameter("tdp"), 65);
                    if (isUpdate) {
                        if (!cpuDAO.update(partId, socketId, cores, threads, baseClock, boostClock, tdp)) {
                            cpuDAO.insert(partId, socketId, cores, threads, baseClock, boostClock, tdp);
                        }
                    } else {
                        cpuDAO.insert(partId, socketId, cores, threads, baseClock, boostClock, tdp);
                    }
                }
            } else if (categoryId == 2) {
                String socketStr = request.getParameter("socketId");
                String chipset = request.getParameter("chipset");
                if (socketStr != null && !socketStr.trim().isEmpty() && chipset != null && !chipset.trim().isEmpty()) {
                    int socketId = Integer.parseInt(socketStr.trim());
                    String formFactor = request.getParameter("formFactor");
                    String ramStd = request.getParameter("ramStandard");
                    int ramSlots = parseOrDefault(request.getParameter("ramSlots"), 4);
                    int maxRamSpeed = parseOrDefault(request.getParameter("maxRamSpeedMhz"), 3200);
                    int maxRamCap = parseOrDefault(request.getParameter("maxRamCapacityGb"), 64);
                    int m2Slots = parseOrDefault(request.getParameter("m2SlotsCount"), 2);
                    boolean supportsNvme = parseBoolOrDefault(request.getParameter("supportsNvme"), true);
                    String pcieGen = request.getParameter("pcieGeneration");
                    int sataPorts = parseOrDefault(request.getParameter("sataPortsCount"), 4);
                    String cpuPowerConn = request.getParameter("cpuPowerConnectors");
                    String supCpuGens = request.getParameter("supportedCpuGenerations");
                    if (isUpdate) {
                        if (!mbDAO.updateFull(partId, socketId, chipset.trim(), formFactor, ramStd, ramSlots,
                                maxRamSpeed, maxRamCap, m2Slots, supportsNvme, pcieGen, sataPorts, cpuPowerConn, supCpuGens)) {
                            mbDAO.insertFull(partId, socketId, chipset.trim(), formFactor, ramStd, ramSlots,
                                    maxRamSpeed, maxRamCap, m2Slots, supportsNvme, pcieGen, sataPorts, cpuPowerConn, supCpuGens);
                        }
                    } else {
                        mbDAO.insertFull(partId, socketId, chipset.trim(), formFactor, ramStd, ramSlots,
                                maxRamSpeed, maxRamCap, m2Slots, supportsNvme, pcieGen, sataPorts, cpuPowerConn, supCpuGens);
                    }
                }
            } else if (categoryId == 3) {
                String ddrType = request.getParameter("ddrType");
                String capStr = request.getParameter("capacityGb");
                if (ddrType != null && !ddrType.trim().isEmpty() && capStr != null && !capStr.trim().isEmpty()) {
                    int cap = Integer.parseInt(capStr.trim());
                    int bus = parseOrDefault(request.getParameter("busSpeedMhz"), 3200);
                    int modCount = parseOrDefault(request.getParameter("moduleCount"), 1);
                    String cas = request.getParameter("casLatency");
                    if (isUpdate) {
                        if (!ramDAO.update(partId, ddrType.trim(), cap, bus, modCount, cas)) {
                            ramDAO.insert(partId, ddrType.trim(), cap, bus, modCount, cas);
                        }
                    } else {
                        ramDAO.insert(partId, ddrType.trim(), cap, bus, modCount, cas);
                    }
                }
            } else if (categoryId == 4) {
                String gpuChipset = request.getParameter("gpuChipset");
                String vramStr = request.getParameter("vramCapacityGb");
                if (gpuChipset != null && !gpuChipset.trim().isEmpty() && vramStr != null && !vramStr.trim().isEmpty()) {
                    int vram = Integer.parseInt(vramStr.trim());
                    int recPsu = parseOrDefault(request.getParameter("recommendedPsuWatts"), 550);
                    String vramType = request.getParameter("vramType");
                    int tgp = parseOrDefault(request.getParameter("tgpWatts"), Math.max(75, recPsu / 2));
                    String powerConn = request.getParameter("powerConnectors");
                    int lengthMm = parseOrDefault(request.getParameter("lengthMm"), 280);
                    String pcieIf = request.getParameter("pcieInterface");
                    int hdmi = parseOrDefault(request.getParameter("hdmiPorts"), 1);
                    int dp = parseOrDefault(request.getParameter("displayportPorts"), 3);
                    if (isUpdate) {
                        if (!vgaDAO.updateFull(partId, gpuChipset.trim(), vram, vramType, recPsu, tgp, powerConn, lengthMm, pcieIf, hdmi, dp)) {
                            vgaDAO.insertFull(partId, gpuChipset.trim(), vram, vramType, recPsu, tgp, powerConn, lengthMm, pcieIf, hdmi, dp);
                        }
                    } else {
                        vgaDAO.insertFull(partId, gpuChipset.trim(), vram, vramType, recPsu, tgp, powerConn, lengthMm, pcieIf, hdmi, dp);
                    }
                }
            } else if (categoryId == 5) {
                String storageType = request.getParameter("storageType");
                String capStr = request.getParameter("capacityGb");
                if (storageType != null && !storageType.trim().isEmpty() && capStr != null && !capStr.trim().isEmpty()) {
                    int cap = Integer.parseInt(capStr.trim());
                    String formFactor = request.getParameter("formFactor");
                    String iface = request.getParameter("interface");
                    Integer readSp = parseNullableInt(request.getParameter("readSpeedMbps"));
                    Integer writeSp = parseNullableInt(request.getParameter("writeSpeedMbps"));
                    if (isUpdate) {
                        if (!storageDAO.update(partId, storageType.trim(), formFactor, iface, cap, readSp, writeSp)) {
                            storageDAO.insert(partId, storageType.trim(), formFactor, iface, cap, readSp, writeSp);
                        }
                    } else {
                        storageDAO.insert(partId, storageType.trim(), formFactor, iface, cap, readSp, writeSp);
                    }
                }
            } else if (categoryId == 6) {
                String wattStr = request.getParameter("wattage");
                if (wattStr != null && !wattStr.trim().isEmpty()) {
                    int watt = Integer.parseInt(wattStr.trim());
                    String eff = request.getParameter("efficiencyRating");
                    String modType = request.getParameter("modularType");
                    int pcieCnt = parseOrDefault(request.getParameter("pcieConnectorsCount"), 2);
                    boolean has12v = "1".equals(request.getParameter("has12vhpwr")) || "true".equalsIgnoreCase(request.getParameter("has12vhpwr"));
                    int epsCnt = parseOrDefault(request.getParameter("epsConnectorsCount"), 1);
                    String ff = request.getParameter("formFactor");
                    if (isUpdate) {
                        if (!psuDAO.update(partId, watt, eff, modType, pcieCnt, has12v, epsCnt, ff)) {
                            psuDAO.insert(partId, watt, eff, modType, pcieCnt, has12v, epsCnt, ff);
                        }
                    } else {
                        psuDAO.insert(partId, watt, eff, modType, pcieCnt, has12v, epsCnt, ff);
                    }
                }
            } else if (categoryId == 7) {
                String caseFormFactor = request.getParameter("caseFormFactor");
                if (caseFormFactor != null && !caseFormFactor.trim().isEmpty()) {
                    int maxVga = parseOrDefault(request.getParameter("maxVgaLengthMm"), 320);
                    int maxCooler = parseOrDefault(request.getParameter("maxCpuCoolerHeightMm"), 160);
                    String fanInfo = request.getParameter("fanSupportInfo");
                    if (isUpdate) {
                        if (!caseDAO.update(partId, caseFormFactor.trim(), maxVga, maxCooler, fanInfo)) {
                            caseDAO.insert(partId, caseFormFactor.trim(), maxVga, maxCooler, fanInfo);
                        }
                    } else {
                        caseDAO.insert(partId, caseFormFactor.trim(), maxVga, maxCooler, fanInfo);
                    }
                }
            } else if (categoryId == 8) {
                String coolerType = request.getParameter("coolerType");
                if (coolerType != null && !coolerType.trim().isEmpty()) {
                    int maxTdp = parseOrDefault(request.getParameter("maxTdp"), 180);
                    int heightMm = parseOrDefault(request.getParameter("heightMm"), 155);
                    Integer radSize = parseNullableInt(request.getParameter("radiatorSizeMm"));
                    String sockets = request.getParameter("supportedSockets");
                    if (isUpdate) {
                        if (!coolerDAO.update(partId, coolerType.trim(), heightMm, radSize, sockets, maxTdp)) {
                            coolerDAO.insert(partId, coolerType.trim(), heightMm, radSize, sockets, maxTdp);
                        }
                    } else {
                        coolerDAO.insert(partId, coolerType.trim(), heightMm, radSize, sockets, maxTdp);
                    }
                }
            } else if (categoryId == 9) {
                String screenSize = request.getParameter("screenSize");
                if (screenSize != null && !screenSize.trim().isEmpty()) {
                    String res = request.getParameter("resolution");
                    int hz = parseOrDefault(request.getParameter("refreshRate"), 60);
                    String panel = request.getParameter("panelType");
                    Double respMs = parseNullableDouble(request.getParameter("responseTimeMs"));
                    String sync = request.getParameter("adaptiveSync");
                    int hdmi = parseOrDefault(request.getParameter("hdmiPorts"), 1);
                    int dp = parseOrDefault(request.getParameter("displayportPorts"), 1);
                    String hdmiVer = request.getParameter("hdmiVersion");
                    String dpVer = request.getParameter("displayportVersion");
                    if (isUpdate) {
                        if (!monitorDAO.updateFull(partId, screenSize.trim(), res, hz, panel, respMs, sync, hdmi, dp, hdmiVer, dpVer)) {
                            monitorDAO.insertFull(partId, screenSize.trim(), res, hz, panel, respMs, sync, hdmi, dp, hdmiVer, dpVer);
                        }
                    } else {
                        monitorDAO.insertFull(partId, screenSize.trim(), res, hz, panel, respMs, sync, hdmi, dp, hdmiVer, dpVer);
                    }
                }
            } else if (categoryId == 10) {
                String gearType = request.getParameter("gearType");
                if (gearType != null && !gearType.trim().isEmpty()) {
                    String conn = request.getParameter("connectivity");
                    String swOrDpi = request.getParameter("switchOrDpi");
                    String layout = request.getParameter("layout");
                    Integer dpiMax = parseNullableInt(request.getParameter("dpiMax"));
                    Integer pollingHz = parseNullableInt(request.getParameter("pollingRateHz"));
                    Integer weightG = parseNullableInt(request.getParameter("weightG"));
                    if (isUpdate) {
                        if (!peripheralDAO.updateFull(partId, gearType.trim(), conn, swOrDpi, layout, dpiMax, pollingHz, weightG)) {
                            peripheralDAO.insertFull(partId, gearType.trim(), conn, swOrDpi, layout, dpiMax, pollingHz, weightG);
                        }
                    } else {
                        peripheralDAO.insertFull(partId, gearType.trim(), conn, swOrDpi, layout, dpiMax, pollingHz, weightG);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseOrDefault(String raw, int defVal) {
        if (raw == null || raw.trim().isEmpty()) return defVal;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    private double parseDoubleOrDefault(String raw, double defVal) {
        if (raw == null || raw.trim().isEmpty()) return defVal;
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    private Integer parseNullableInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseNullableDouble(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean parseBoolOrDefault(String raw, boolean defVal) {
        if (raw == null || raw.trim().isEmpty()) return defVal;
        String t = raw.trim().toLowerCase();
        return "1".equals(t) || "true".equals(t) || "yes".equals(t) || "on".equals(t);
    }

    private String buildPartSpecsJson(List<Parts> partsList) {
        StringBuilder root = new StringBuilder("{");
        if (partsList == null || partsList.isEmpty()) {
            return "{}";
        }
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (Parts p : partsList) {
            if (p != null && p.getPartId() > 0) ids.add(p.getPartId());
        }
        if (ids.isEmpty()) return "{}";

        java.util.Map<Integer, java.util.Map<String, String>> specByPart = new java.util.HashMap<>();
        String[] tables = {
            "spec_cpu", "spec_mainboard", "spec_ram", "spec_vga", "spec_storage",
            "spec_psu", "spec_case", "spec_cooler", "spec_monitor", "spec_peripheral"
        };
        try (java.sql.Connection con = fourSome.url.DBConnect.getConnection()) {
            for (String tbl : tables) {
                String sql = "SELECT * FROM [" + tbl + "]";
                try (java.sql.PreparedStatement ps = con.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    java.sql.ResultSetMetaData md = rs.getMetaData();
                    while (rs.next()) {
                        int pid = rs.getInt("part_id");
                        if (!ids.contains(pid)) continue;
                        java.util.Map<String, String> map = specByPart.computeIfAbsent(pid, k -> new java.util.HashMap<>());
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String col = md.getColumnLabel(i).toLowerCase();
                            Object val = rs.getObject(i);
                            if (val != null) {
                                String strVal = (val instanceof java.math.BigDecimal)
                                        ? ((java.math.BigDecimal) val).stripTrailingZeros().toPlainString()
                                        : String.valueOf(val);
                                map.put(col, strVal);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean firstPart = true;
        for (Integer pid : ids) {
            java.util.Map<String, String> fields = specByPart.get(pid);
            if (fields == null || fields.isEmpty()) continue;
            if (!firstPart) root.append(",");
            firstPart = false;
            root.append("\"").append(pid).append("\":{");
            boolean firstField = true;
            for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
                if (!firstField) root.append(",");
                firstField = false;
                root.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
            }
            root.append("}");
        }
        root.append("}");
        return root.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}