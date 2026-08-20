import java.time.LocalTime;

public class RideBookingQA {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    STARTING RIDE-SHARING DISPATCH QA SYSTEM      ");
        System.out.println("==================================================");

        // Core business layout setup instantiations
        RideBooking system = new RideBooking();

        // Populate driver asset pools mapping requirements matrix
        system.registerDriver("D_BIKE_01", "Ramesh (Bike)", "Bike", true);
        system.registerDriver("D_SEDAN_01", "Suresh (Sedan)", "Sedan", true);
        system.registerDriver("D_SUV_01", "Anand (SUV)", "SUV", true);
        system.registerDriver("D_PREM_01", "Kabir (Premium)", "Premium", true);
        system.registerDriver("D_BUSY_01", "John (Unavailable)", "Sedan", false);

        // QA Test 1: Normal Standard Booking Lifecycle Processing
        System.out.println("\n[Test 1] Normal Booking Lifecycle Processing (Sedan)");
        RideBooking.BookingResponse r1 = system.processBooking(
                "CUST01", "Central Station", "Airport HUB", 12.5, 2, "Sedan", LocalTime.of(14, 0), "NONE"
        );
        printReceipt(r1);

        // QA Test 2: Peak-Hour Surge Window Verification Checks
        System.out.println("\n[Test 2] Peak-Hour Surge Window Validation (08:30 Rush hour)");
        // Register another sedan since first driver was locked up by previous system check
        system.registerDriver("D_SEDAN_02", "Vikram (Sedan)", "Sedan", true);
        RideBooking.BookingResponse r2 = system.processBooking(
                "CUST02", "DownTown Center", "Business Block", 5.0, 1, "Sedan", LocalTime.of(8, 30), "NONE"
        );
        printReceipt(r2);

        // QA Test 3: Night Surcharge Metric Calculation Controls
        System.out.println("\n[Test 3] Night Shift Premium Surcharge Matrix Checks (01:15 AM)");
        RideBooking.BookingResponse r3 = system.processBooking(
                "CUST03", "Club Zone", "Residential Suburb", 10.0, 1, "Bike", LocalTime.of(1, 15), "NONE"
        );
        printReceipt(r3);

        // QA Test 4: Faulty Negative Distance and Boundary Value Rejection
        System.out.println("\n[Test 4] Faulty Zero / Negative Distance Input Safeguards");
        RideBooking.BookingResponse r4 = system.processBooking(
                "CUST04", "Hotel Lobby", "Hotel Gate", 0.0, 1, "Sedan", LocalTime.of(12, 0), "NONE"
        );
        System.out.println("Zero Distance Allocation Status Approved: " + r4.isApproved + " | Message: " + r4.statusMessage);

        // QA Test 5: Excessive Passenger Counts Limit Bounds Checks
        System.out.println("\n[Test 5] Passenger Over-Capacity Threshold Violations");
        RideBooking.BookingResponse r5 = system.processBooking(
                "CUST05", "Mall Area", "Theme Park", 15.0, 3, "Bike", LocalTime.of(12, 0), "NONE"
        );
        System.out.println("3 Passengers on a Bike Status Approved: " + r5.isApproved + " | Message: " + r5.statusMessage);

        // QA Test 6: Driver Inventory Depletion Exception Scenarios
        System.out.println("\n[Test 6] Exhausted Fleet Inventory & Unavailable Driver Logic");
        // Requesting another Premium ride - but D_PREM_01 is currently available, let's claim it first
        system.processBooking("CUST06", "A", "B", 2.0, 1, "Premium", LocalTime.of(12, 0), "NONE");
        // Now no premium driver assets are left inside the matching pool system array lists
        RideBooking.BookingResponse r6 = system.processBooking(
                "CUST07", "Luxury Strip", "Country Club", 8.0, 2, "Premium", LocalTime.of(13, 0), "NONE"
        );
        System.out.println("Depleted Premium Fleet Status Approved: " + r6.isApproved + " | Message: " + r6.statusMessage);

        // QA Test 7: Max Promotional Coupon Discount Ceiling Threshold Validation
        System.out.println("\n[Test 7] Maximum Discount Cap Constraints Evaluation");
        system.registerDriver("D_SUV_02", "Amit (SUV)", "SUV", true);
        RideBooking.BookingResponse r7 = system.processBooking(
                "CUST08", "City A", "Distant City B", 80.0, 5, "SUV", LocalTime.of(12, 0), "MAXSAVINGS"
        );
        printReceipt(r7);

        // QA Test 8: Multiple Vehicle Type Configurations Routing Matrix Check
        System.out.println("\n[Test 8] Multi-Vehicle Operational Routing Configurations Matrix Matrix Checks");
        system.registerDriver("D_SEDAN_03", "Rahul (Sedan)", "Sedan", true);
        RideBooking.BookingResponse r8a = system.processBooking("CUST09", "X", "Y", 4.0, 1, "Sedan", LocalTime.of(15, 0), "NONE");
        RideBooking.BookingResponse r8b = system.processBooking("CUST10", "X", "Y", 4.0, 4, "SUV", LocalTime.of(15, 0), "NONE");
        System.out.println("Sedan Final Pricing Matrix Value: Rs." + r8a.finalFare + " | Allocated Driver: " + r8a.assignedDriverId);
        System.out.println("SUV Final Pricing Matrix Value:   Rs." + r8b.finalFare + " | Allocated Driver: " + r8b.assignedDriverId);

        System.out.println("\n==================================================");
        System.out.println("     ALL RIDE-SHARING DISPATCH QA TESTS COMPLETE  ");
        System.out.println("==================================================");
    }

    private static void printReceipt(RideBooking.BookingResponse res) {
        if (!res.isApproved) {
            System.out.println("Transaction Processing Aborted: " + res.statusMessage);
            return;
        }
        System.out.println("Booking Tracking Details: " + res.statusMessage);
        System.out.println(" -> Assigned Driver System Key ID: " + res.assignedDriverId);
        System.out.println(" -> Base Fare Metrics Matrix:     Rs." + res.baseFare);
        System.out.println(" -> Distance Based Run Rate:      Rs." + res.distanceFare);
        System.out.println(" -> Rush Hour Surge Surcharge:    Rs." + res.peakSurcharge);
        System.out.println(" -> Night Operations Premium:     Rs." + res.nightSurcharge);
        System.out.println(" -> Extra Passenger Surcharge:    Rs." + res.passengerSurcharge);
        System.out.println(" -> Coupon Promo Deductions:     -Rs." + res.discountAmount);
        System.out.println(" -> Final Settled Ledger Cost:    Rs." + res.finalFare);
    }
}
