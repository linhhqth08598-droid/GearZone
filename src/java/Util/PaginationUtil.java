package Util;

/**
 * Tính toán phân trang cho danh sách.
 * Dùng với SQL Server: ORDER BY ... OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
 */
public class PaginationUtil {

    private final int currentPage;
    private final int pageSize;
    private final int totalItems;
    private final int totalPages;

    public PaginationUtil(String pageParam, int pageSize, int totalItems) {
        this.pageSize = pageSize > 0 ? pageSize : Constants.DEFAULT_PAGE_SIZE;
        this.totalItems = Math.max(totalItems, 0);
        this.totalPages = Math.max(1, (int) Math.ceil((double) this.totalItems / this.pageSize));
        this.currentPage = clamp(parsePage(pageParam), 1, this.totalPages);
    }

    private static int parsePage(String p) {
        try {
            return Integer.parseInt(p.trim());
        } catch (Exception e) {
            return 1;
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(v, max));
    }

    /** Vị trí bắt đầu (OFFSET) của trang hiện tại. */
    public int getOffset() { return (currentPage - 1) * pageSize; }

    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public int getTotalItems() { return totalItems; }
    public int getTotalPages() { return totalPages; }
    public boolean hasPrevious() { return currentPage > 1; }
    public boolean hasNext() { return currentPage < totalPages; }
}