import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RideBooking {

    // Model structural definition for a registered system Driver
    public static class Driver {
        public String id;
        public String name;
        public String vehicleType; // Bike, Sedan, SUV, Premium
        public boolean isAvailable;

        public Driver(String id, String name, String vehicleType, boolean isAvailable) {
            this.id = id;
            this.name = name;
            this.vehicleType = vehicleType;
            this.isAvailable = isAvailable;
        }
    }

    // Comprehensive response matrix containing detailed structural receipt tracking
    public static class BookingResponse {
        public boolean isApproved;
        public String statusMessage;
        public String assignedDriverId;
        public double baseFare;
        public double distanceFare;
        public double peakSurcharge;
        public double nightSurcharge;
        public double passengerSurcharge;
        public double discountAmount;
        public double finalFare;

        public BookingResponse(boolean isApproved, String statusMessage) {
            this.isApproved = isApproved;
            this.statusMessage = statusMessage;
        }
    }

    private List<Driver> driversRegistry;

    public RideBooking() {
        this.driversRegistry = new ArrayList<>();
    }

    // Driver onboarding configuration routine
    public void registerDriver(String id, String name, String vehicleType, boolean isAvailable) {
        driversRegistry.add(new Driver(id, name, vehicleType, isAvailable));
    }

    // Core Business Logic: Core Multi-Tiered Fare Calculator and Driver Allocator Engine
    public BookingResponse processBooking(
            String customerId, 
            String pickup, 
            String drop, 
            double distance, 
            int passengerCount, 
            String vehicleType, 
            LocalTime bookingTime, 
            String promoCode
    ) {
        // --- 1. VALIDATION AND PROTECTION GUARDRAILS ---
        if (customerId == null || customerId.trim().isEmpty() || pickup == null || drop == null) {
            return new BookingResponse(false, "Invalid booking identification layout parameters.");
        }
        if (distance <= 0) {
            return new BookingResponse(false, "Booking rejected: Distance tracking parameters must be greater than zero.");
        }
        if (bookingTime == null) {
            return new BookingResponse(false, "Booking rejected: Invalid booking timestamp structural format.");
        }

        // Validate vehicle configurations type and maximize seating rules
        int maxCapacity;
        double baseRate;
        double perKmRate;

        if (vehicleType.equalsIgnoreCase("Bike")) {
            maxCapacity = 1; baseRate = 30.0; perKmRate = 10.0;
        } else if (vehicleType.equalsIgnoreCase("Sedan")) {
            maxCapacity = 4; baseRate = 60.0; perKmRate = 15.0;
        } else if (vehicleType.equalsIgnoreCase("SUV")) {
            maxCapacity = 7; baseRate = 100.0; perKmRate = 22.0;
        } else if (vehicleType.equalsIgnoreCase("Premium")) {
            maxCapacity = 4; baseRate = 150.0; perKmRate = 30.0;
        } else {
            return new BookingResponse(false, "Booking rejected: Unsupported localized vehicle class type request.");
        }

        if (passengerCount <= 0 || passengerCount > maxCapacity) {
            return new BookingResponse(false, "Booking rejected: Passenger density metrics exceed maximum vehicle configuration ceiling levels.");
        }

        // --- 2. DRIVER ALLOCATION MATCHING ROUTINE ---
        Driver matchedDriver = null;
        for (Driver driver : driversRegistry) {
            if (driver.isAvailable && driver.vehicleType.equalsIgnoreCase(vehicleType)) {
                matchedDriver = driver;
                break; // Closest first matching driver optimization vector
            }
        }

        if (matchedDriver == null) {
            return new BookingResponse(false, "Booking rejected: No available driver currently active in this matching asset class.");
        }

        // --- 3. FARE TRACKING & SURCHARGE COMPUTATION LAYER ---
        double distanceFare = distance * perKmRate;
        double peakSurcharge = 0.0;
        double nightSurcharge = 0.0;
        double passengerSurcharge = 0.0;

        // Peak hours window baseline definitions: 08:00 - 10:59 (Morning Rush) and 17:00 - 19:59 (Evening Rush)
        int hour = bookingTime.getHour();
        if ((hour >= 8 && hour < 11) || (hour >= 17 && hour < 20)) {
            peakSurcharge = (baseRate + distanceFare) * 0.25; // 25% Surge Multiplier
        }

        // Night time surcharge definitions window: 23:00 to 04:59
        if (hour >= 23 || hour < 5) {
            nightSurcharge = (baseRate + distanceFare) * 0.15; // 15% Night Shift premium modifier
        }

        // Extra passenger surcharge allocations (Apply metrics to shared sedan/SUV setups past baseline)
        if (passengerCount > 2) {
            passengerSurcharge = (passengerCount - 2) * 20.0;
        }

        // Calculate subtotal metrics
        double initialSubtotal = baseRate + distanceFare + peakSurcharge + nightSurcharge + passengerSurcharge;

        // Promotional deductions mapping profile matrix (Capped boundary validation limit = max 150.0)
        double discountAmount = 0.0;
        if (promoCode != null && promoCode.equalsIgnoreCase("MAXSAVINGS")) {
            discountAmount = initialSubtotal * 0.50; // Attempt 50% discount mapping
            if (discountAmount > 150.0) {
                discountAmount = 150.0; // Enforce maximum structural discount limit cap
            }
        }

        double finalFare = initialSubtotal - discountAmount;
        if (finalFare < 0) finalFare = 0.0; // Safe-guard boundary checking limits

        // --- 4. EXECUTION COMMITMENT LAYER ---
        matchedDriver.isAvailable = false; // Lock driver asset inventory configuration state

        // Package output metrics response
        BookingResponse receipt = new BookingResponse(true, "Booking successful! Fleet assets allocated.");
        receipt.assignedDriverId = matchedDriver.id;
        receipt.baseFare = baseRate;
        receipt.distanceFare = distanceFare;
        receipt.peakSurcharge = peakSurcharge;
        receipt.nightSurcharge = nightSurcharge;
        receipt.passengerSurcharge = passengerSurcharge;
        receipt.discountAmount = discountAmount;
        receipt.finalFare = finalFare;

        return receipt;
    }
}
