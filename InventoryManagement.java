import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagement {
    
    // Core structural model data structures
    public static class Product {
        public String id;
        public String name;
        public int reorderThreshold;
        public String supplierName;

        public Product(String id, String name, int reorderThreshold, String supplierName) {
            this.id = id;
            this.name = name;
            this.reorderThreshold = reorderThreshold;
            this.supplierName = supplierName;
        }
    }

    public static class Warehouse {
        public String name;
        public Map<String, Integer> stockMap; // ProductId -> Quantity

        public Warehouse(String name) {
            this.name = name;
            this.stockMap = new HashMap<>();
        }
    }

    public static class ServiceResponse {
        public boolean success;
        public String message;

        public ServiceResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    // Master operational tables
    private Map<String, Product> globalProducts;
    private Map<String, Warehouse> warehouses;

    public InventoryManagement() {
        this.globalProducts = new HashMap<>();
        this.warehouses = new HashMap<>();
        
        // Auto-initialize required Warehouses A, B, and C
        warehouses.put("Warehouse A", new Warehouse("Warehouse A"));
        warehouses.put("Warehouse B", new Warehouse("Warehouse B"));
        warehouses.put("Warehouse C", new Warehouse("Warehouse C"));
    }

    // Operation: Add product to master system catalog
    public ServiceResponse registerProduct(String id, String name, int threshold, String supplier) {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            return new ServiceResponse(false, "Invalid product identification strings.");
        }
        if (threshold < 0) {
            return new ServiceResponse(false, "Reorder threshold cannot be negative.");
        }
        Product product = new Product(id, name, threshold, supplier);
        globalProducts.put(id, product);
        return new ServiceResponse(true, "Product '" + name + "' added to global registry successfully.");
    }

    // Operation: Add stock replenishment directly to a target warehouse
    public ServiceResponse addStock(String warehouseName, String productId, int qty) {
        if (!warehouses.containsKey(warehouseName)) {
            return new ServiceResponse(false, "Target warehouse does not exist.");
        }
        if (!globalProducts.containsKey(productId)) {
            return new ServiceResponse(false, "Product entry not found in global registry.");
        }
        if (qty <= 0) {
            return new ServiceResponse(false, "Replenishment stock quantity must be positive.");
        }

        Warehouse wh = warehouses.get(warehouseName);
        int currentQty = wh.stockMap.getOrDefault(productId, 0);
        wh.stockMap.put(productId, currentQty + qty);
        return new ServiceResponse(true, "Successfully added " + qty + " units to " + warehouseName + ".");
    }

    // Operation: Remove product or reduce absolute storage footprint counts manually
    public ServiceResponse removeStockManually(String warehouseName, String productId, int qty) {
        if (!warehouses.containsKey(warehouseName)) return new ServiceResponse(false, "Warehouse execution failed.");
        if (qty <= 0) return new ServiceResponse(false, "Quantity must be positive.");
        
        Warehouse wh = warehouses.get(warehouseName);
        int currentQty = wh.stockMap.getOrDefault(productId, 0);
        if (currentQty < qty) {
            return new ServiceResponse(false, "Cannot remove stock. Negative inventory states are blocked.");
        }
        
        wh.stockMap.put(productId, currentQty - qty);
        return new ServiceResponse(true, "Manually cleared " + qty + " units from storage footprints.");
    }

    // Operation: Transfer Stock across localized regional processing layers
    public ServiceResponse transferStock(String sourceWh, String destWh, String productId, int qty) {
        if (!warehouses.containsKey(sourceWh) || !warehouses.containsKey(destWh)) {
            return new ServiceResponse(false, "One or both operational warehouse nodes do not exist.");
        }
        if (qty <= 0) {
            return new ServiceResponse(false, "Transfer metrics volume requests must be positive.");
        }
        
        Warehouse source = warehouses.get(sourceWh);
        Warehouse dest = warehouses.get(destWh);
        int sourceStock = source.stockMap.getOrDefault(productId, 0);

        if (sourceStock < qty) {
            return new ServiceResponse(false, "Insufficient inventory footprint inside source node.");
        }

        source.stockMap.put(productId, sourceStock - qty);
        int destStock = dest.stockMap.getOrDefault(productId, 0);
        dest.stockMap.put(productId, destStock + qty);

        return new ServiceResponse(true, "Successfully shifted " + qty + " items from " + sourceWh + " to " + destWh + ".");
    }

    // Operation: Intelligent Warehouse Selection Order Fulfillment Routine
    // Requirements: Scan nodes alphabetically/sequentially to automatically allocate matching lots
    public ServiceResponse fulfillOrder(String productId, int qty) {
        if (!globalProducts.containsKey(productId)) {
            return new ServiceResponse(false, "Invalid product code sequence entered.");
        }
        if (qty <= 0) {
            return new ServiceResponse(false, "Fulfillment quantity metrics must be positive numerical vectors.");
        }

        // Search logic sequences: Warehouse A -> Warehouse B -> Warehouse C
        String[] prioritizedNodes = {"Warehouse A", "Warehouse B", "Warehouse C"};
        String selectedWarehouse = null;

        for (String node : prioritizedNodes) {
            Warehouse wh = warehouses.get(node);
            if (wh.stockMap.getOrDefault(productId, 0) >= qty) {
                selectedWarehouse = node;
                break;
            }
        }

        if (selectedWarehouse == null) {
            return new ServiceResponse(false, "Order tracking failed: Insufficient aggregate inventory found across localized processing nodes.");
        }

        // Deduct quantities systematically
        Warehouse target = warehouses.get(selectedWarehouse);
        int ongoingStock = target.stockMap.get(productId);
        target.stockMap.put(productId, ongoingStock - qty);

        // Check low-stock triggers instantly
        String triggerLog = "";
        if (checkLowStockTrigger(productId)) {
            triggerLog = " [ALERT: Low-stock detected! Triggering Supplier automatic reorder procedure]";
        }

        return new ServiceResponse(true, "Order fulfilled completely via " + selectedWarehouse + "." + triggerLog);
    }

    // Operation: Low-Stock Detection Routine heuristics
    public boolean checkLowStockTrigger(String productId) {
        Product p = globalProducts.get(productId);
        if (p == null) return false;

        int totalCombinedStock = 0;
        for (Warehouse wh : warehouses.values()) {
            totalCombinedStock += wh.stockMap.getOrDefault(productId, 0);
        }
        return totalCombinedStock <= p.reorderThreshold;
    }

    // Operation: Reorder and Supplier Management processing engines
    public ServiceResponse triggerSupplierReorder(String productId, int orderVolume) {
        if (!globalProducts.containsKey(productId)) return new ServiceResponse(false, "Unknown item.");
        if (orderVolume <= 0) return new ServiceResponse(false, "Reorder request quantities must be positive.");
        
        Product p = globalProducts.get(productId);
        // Automatically route replenishment batches into baseline Warehouse A anchor hub
        addStock("Warehouse A", productId, orderVolume);
        return new ServiceResponse(true, "Supplier '" + p.supplierName + "' processed request. Inbound supply lines closed into Warehouse A.");
    }

    // Helper visibility monitoring logic wrappers
    public int getStockAtWarehouse(String warehouseName, String productId) {
        if (!warehouses.containsKey(warehouseName)) return 0;
        return warehouses.get(warehouseName).stockMap.getOrDefault(productId, 0);
    }
}
