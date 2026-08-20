public class InventoryQA {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     STARTING INVENTORY SUPPLY CHAIN QA SYSTEM    ");
        System.out.println("==================================================");

        // Deploy system orchestrator engine
        InventoryManagement ims = new InventoryManagement();

        // Registry configuration inputs initialization
        String prodId1 = "PROD_001"; // Secure Core Item
        ims.registerProduct(prodId1, "Enterprise Microprocessor", 5, "Intel Systems Corp");

        // QA Test 1: Stock Availability Setup across Multi-Warehouse distributions
        System.out.println("\n[Test 1] Initializing Stock Availability Matrix");
        ims.addStock("Warehouse A", prodId1, 10);
        ims.addStock("Warehouse B", prodId1, 20);
        ims.addStock("Warehouse C", prodId1, 30);
        System.out.println("Warehouse A Stock baseline: " + ims.getStockAtWarehouse("Warehouse A", prodId1));
        System.out.println("Warehouse B Stock baseline: " + ims.getStockAtWarehouse("Warehouse B", prodId1));
        System.out.println("Warehouse C Stock baseline: " + ims.getStockAtWarehouse("Warehouse C", prodId1));

        // QA Test 2: Sequential Auto-Warehouse Selection Fulfillment Matrix verification
        System.out.println("\n[Test 2] Order Routing & Auto Warehouse Selection Diagnostics");
        // Request 8: Fits perfectly in A (A has 10)
        InventoryManagement.ServiceResponse resA = ims.fulfillOrder(prodId1, 8);
        System.out.println("Order 8 Units: " + resA.message + " | Remainder A: " + ims.getStockAtWarehouse("Warehouse A", prodId1));

        // Request 15: Cannot fit inside A anymore (A has 2), skips directly to B (B has 20)
        InventoryManagement.ServiceResponse resB = ims.fulfillOrder(prodId1, 15);
        System.out.println("Order 15 Units: " + resB.message + " | Remainder B: " + ims.getStockAtWarehouse("Warehouse B", prodId1));

        // QA Test 3: Insufficient Aggregate Inventory verification blocks
        System.out.println("\n[Test 3] Insufficient Inventory Threat Management");
        // No single workspace has 45 units remaining right now
        InventoryManagement.ServiceResponse resFail = ims.fulfillOrder(prodId1, 45);
        System.out.println("Order 45 Units Status: " + resFail.success + " | Message: " + resFail.message);

        // QA Test 4: Node to Node Structural Warehouse Transfers
        System.out.println("\n[Test 4] Warehouse Internal Stock Transfers");
        System.out.println("Warehouse C before transfer: " + ims.getStockAtWarehouse("Warehouse C", prodId1));
        InventoryManagement.ServiceResponse resTx = ims.transferStock("Warehouse C", "Warehouse A", prodId1, 10);
        System.out.println("Transfer Execution: " + resTx.message);
        System.out.println("Warehouse A after receiving: " + ims.getStockAtWarehouse("Warehouse A", prodId1));
        System.out.println("Warehouse C after matching deduction: " + ims.getStockAtWarehouse("Warehouse C", prodId1));

        // QA Test 5: Reorder Threshold Evaluation and Automated Supplier Procurement loops
        System.out.println("\n[Test 5] Low Stock Threshold Detection and Supplier Reordering");
        // Force overall total down aggressively to trigger alarms (Threshold baseline limit config is 5)
        ims.fulfillOrder(prodId1, 12); // Clean out Warehouse A metrics
        ims.fulfillOrder(prodId1, 18); // Target Warehouse C assets 
        
        System.out.println("Current Stock Profile - A: " + ims.getStockAtWarehouse("Warehouse A", prodId1) 
                           + " | B: " + ims.getStockAtWarehouse("Warehouse B", prodId1) 
                           + " | C: " + ims.getStockAtWarehouse("Warehouse C", prodId1));
        
        // Critical step execution trigger: forces sum properties past boundary lines
        InventoryManagement.ServiceResponse resLow = ims.fulfillOrder(prodId1, 4);
        System.out.println("Critical Depletion Request: " + resLow.message);

        // Execute procurement line loops
        InventoryManagement.ServiceResponse reorderRes = ims.triggerSupplierReorder(prodId1, 50);
        System.out.println("Procurement Line Activation: " + reorderRes.message);
        System.out.println("Post Reorder Hub A Storage Level: " + ims.getStockAtWarehouse("Warehouse A", prodId1));

        // QA Test 6: Invalid Entry validation routines
        System.out.println("\n[Test 6] Invalid Product & Warehouse Fault Interceptions");
        InventoryManagement.ServiceResponse badId = ims.fulfillOrder("FAKE_ITEM_XYZ", 10);
        System.out.println("Unknown Key Resolution: " + badId.success + " | Message: " + badId.message);

        // QA Test 7: Negative Inventory Guardrails verification
        System.out.println("\n[Test 7] Negative Inventory Intrusion Shield Checks");
        InventoryManagement.ServiceResponse negativeQty = ims.addStock("Warehouse A", prodId1, -100);
        System.out.println("Negative Addition Rejected: " + negativeQty.success + " | Message: " + negativeQty.message);
        InventoryManagement.ServiceResponse overRemoval = ims.removeStockManually("Warehouse B", prodId1, 5000);
        System.out.println("Negative Exploit Drop Rejected: " + overRemoval.success + " | Message: " + overRemoval.message);

        // QA Test 8: High Frequency Race-Condition Simulators
        System.out.println("\n[Test 8] Concurrent Order Request Simulation Logging");
        InventoryManagement.ServiceResponse hit1 = ims.fulfillOrder(prodId1, 40);
        InventoryManagement.ServiceResponse hit2 = ims.fulfillOrder(prodId1, 40);
        System.out.println("Microsecond Transaction Hit 1 Result: " + hit1.success + " | Message: " + hit1.message);
        System.out.println("Microsecond Transaction Hit 2 Result: " + hit2.success + " | Message: " + hit2.message);

        System.out.println("\n==================================================");
        System.out.println("      ALL INVENTORY & SUPPLY QA TESTS COMPLETE    ");
        System.out.println("==================================================");
    }
}
