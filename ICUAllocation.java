import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICUAllocation {

    // Clinical priority tier classifications
    public enum PriorityTier {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    // Structural model defining incoming Patient Vital records
    public static class Patient {
        public String id;
        public int age;
        public int oxygenLevel;    // Percentage (0-100)
        public int heartRate;       // Beats per minute
        public String bloodPressure; // Format "120/80"
        public double temperature;   // Celsius
        public boolean hasCoMorbidities;
        public boolean isEmergencyOverride;
        public PriorityTier tier;
        public int priorityScore;

        public Patient(String id, int age, int oxygenLevel, int heartRate, String bloodPressure, 
                       double temperature, boolean hasCoMorbidities, boolean isEmergencyOverride) {
            this.id = id;
            this.age = age;
            this.oxygenLevel = oxygenLevel;
            this.heartRate = heartRate;
            this.bloodPressure = bloodPressure;
            this.temperature = temperature;
            this.hasCoMorbidities = hasCoMorbidities;
            this.isEmergencyOverride = isEmergencyOverride;
        }
    }

    // Response packet architecture for processing diagnostics
    public static class AllocationResponse {
        public boolean isAllocated;
        public String message;
        public PriorityTier assignedTier;
        public int finalScore;

        public AllocationResponse(boolean isAllocated, String message, PriorityTier assignedTier, int finalScore) {
            this.isAllocated = isAllocated;
            this.message = message;
            this.assignedTier = assignedTier;
            this.finalScore = finalScore;
        }
    }

    private int availableBeds;
    private Map<String, Patient> activeAdmissions; // Protection mapping against duplicate Patient IDs
    private List<Patient> waitingList;

    public ICUAllocation(int initialBeds) {
        this.availableBeds = initialBeds;
        this.activeAdmissions = new HashMap<>();
        this.waitingList = new ArrayList<>();
    }

    // Setter routine to manually update resource pools
    public void updateBedInventory(int totalBeds) {
        this.availableBeds = totalBeds;
    }

    public int getAvailableBedsCount() {
        return this.availableBeds;
    }

    public int getWaitingListSize() {
        return this.waitingList.size();
    }

    // Core Business Logic: Priority Point Index Matrix Generator and Allocation Route Engine
    public AllocationResponse triageAndAllocate(Patient patient) {
        
        // --- 1. DATA VALIDATION & SECURITY SHIELDING ---
        if (patient.id == null || patient.id.trim().isEmpty()) {
            return new AllocationResponse(false, "Admission Rejected: Invalid patient record tracking strings.", null, 0);
        }
        if (activeAdmissions.containsKey(patient.id)) {
            return new AllocationResponse(false, "Admission Rejected: Duplicate Patient ID registration trace detected.", null, 0);
        }
        if (patient.oxygenLevel < 0 || patient.oxygenLevel > 100) {
            return new AllocationResponse(false, "Admission Rejected: Out-of-bounds oxygen level saturation data parameters.", null, 0);
        }
        if (patient.heartRate < 0 || patient.heartRate > 300) {
            return new AllocationResponse(false, "Admission Rejected: Invalid heart rate monitoring configuration parameters.", null, 0);
        }
        if (patient.age < 0 || patient.age > 150) {
            return new AllocationResponse(false, "Admission Rejected: Patient age registration field holds anomalous value.", null, 0);
        }

        // --- 2. PRIORITY INDEX SCORE CALCULATION ROUTINE ---
        int score = 0;

        // Rule A: Oxygen Depletion Severity Metrics (Hypoxia markers)
        if (patient.oxygenLevel < 85) score += 50;
        else if (patient.oxygenLevel < 92) score += 30;
        else if (patient.oxygenLevel < 95) score += 10;

        // Rule B: Heart Rate Extremities (Tachycardia / Bradycardia conditions)
        if (patient.heartRate > 130 || patient.heartRate < 45) score += 25;
        else if (patient.heartRate > 100 || patient.heartRate < 60) score += 10;

        // Rule C: Thermal Fluctuations (Hyperpyrexia or severe hypothermia)
        if (patient.temperature >= 39.5 || patient.temperature <= 35.0) score += 15;
        else if (patient.temperature >= 38.0) score += 5;

        // Rule D: Age Vulnerability Adjustments
        if (patient.age >= 65 || patient.age <= 5) score += 10;

        // Rule E: Chronic Co-Morbid Conditions Weighting
        if (patient.hasCoMorbidities) score += 15;

        // Assign Severity Classifications based on Score Boundary Ranges
        PriorityTier computedTier;
        if (score >= 60) computedTier = PriorityTier.CRITICAL;
        else if (score >= 40) computedTier = PriorityTier.HIGH;
        else if (score >= 20) computedTier = PriorityTier.MEDIUM;
        else computedTier = PriorityTier.LOW;

        // Enforce instance updates
        patient.priorityScore = score;
        patient.tier = computedTier;

        // --- 3. RESOURCE SCHEDULING & EMERGENCY OVERRIDE MANAGEMENT LAYER ---
        activeAdmissions.put(patient.id, patient);

        // Scenario A: Emergency Override Command sequence intercepts allocation line immediately
        if (patient.isEmergencyOverride) {
            if (availableBeds > 0) {
                availableBeds--;
                return new AllocationResponse(true, "Allocated: Immediate Emergency Override Action Successful.", computedTier, score);
            } else {
                // If beds are totally full, emergency case forces their way to the front of the waiting list index
                waitingList.add(0, patient);
                return new AllocationResponse(false, "Waiting List: Bed capacity zero. Emergency triage placed at rank index 1.", computedTier, score);
            }
        }

        // Scenario B: Standard Capacity Availability verification
        if (availableBeds > 0) {
            availableBeds--;
            return new AllocationResponse(true, "Allocated: Patient assigned directly to available ICU isolation chamber unit.", computedTier, score);
        } else {
            // Scenario C: Route to Waiting List sorted systematically via priority point vectors
            insertIntoSortedWaitingList(patient);
            return new AllocationResponse(false, "Waiting List: Facility saturated. Patient queued dynamically based on severity indicators.", computedTier, score);
        }
    }

    // Custom sorting optimization routing insertion routine to ensure Critical cases take precedence
    private void insertIntoSortedWaitingList(Patient newPatient) {
        int targetIndex = waitingList.size();
        for (int i = 0; i < waitingList.size(); i++) {
            Patient queuedPatient = waitingList.get(i);
            
            // Critical patients override everything. Higher total scores break ties inside matching tiers
            if (newPatient.tier.ordinal() < queuedPatient.tier.ordinal() || 
               (newPatient.tier == queuedPatient.tier && newPatient.priorityScore > queuedPatient.priorityScore)) {
                targetIndex = i;
                break;
            }
        }
        waitingList.add(targetIndex, newPatient);
    }
}
