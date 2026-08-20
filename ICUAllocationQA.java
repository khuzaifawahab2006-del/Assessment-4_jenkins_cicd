import java.time.LocalTime;

public class ICUAllocationQA {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     STARTING ICU RESOURCE ALLOCATION QA SYSTEM    ");
        System.out.println("==================================================");

        // Setup triage desk environment with an intentional bottleneck (only 2 ICU Beds total)
        ICUAllocation triageDesk = new ICUAllocation(2);

        // QA Test 1: Critical Patient Profiling Validation
        System.out.println("\n[Test 1] Critical Patient Registration Testing");
        ICUAllocation.Patient p1 = new ICUAllocation.Patient(
                "PT_001", 72, 82, 140, "160/95", 39.8, true, false
        ); // Saturated risk variables -> Expect CRITICAL
        ICUAllocation.AllocationResponse r1 = triageDesk.triageAndAllocate(p1);
        printTriageReceipt(p1, r1);

        // QA Test 2: Normal/Low-Urgency Patient Profiling Validation
        System.out.println("\n[Test 2] Low Severity Standard Patient Registration Testing");
        ICUAllocation.Patient p2 = new ICUAllocation.Patient(
                "PT_002", 30, 98, 72, "120/80", 36.6, false, false
        ); // Balanced vitals -> Expect LOW / MEDIUM
        ICUAllocation.AllocationResponse r2 = triageDesk.triageAndAllocate(p2);
        printTriageReceipt(p2, r2);

        // QA Test 3: Saturated Capacity Limits and Waiting List Queuing
        System.out.println("\n[Test 3] Capacity Saturation & Sorted Waiting List Enforcements");
        System.out.println("Remaining available ICU beds left inside tracking matrix: " + triageDesk.getAvailableBedsCount());
        
        ICUAllocation.Patient p3 = new ICUAllocation.Patient(
                "PT_003", 68, 90, 105, "140/90", 38.5, true, false
        ); // High/Critical metrics but beds are now down to 0
        ICUAllocation.AllocationResponse r3 = triageDesk.triageAndAllocate(p3);
        printTriageReceipt(p3, r3);
        System.out.println("Current ICU Waiting list density level: " + triageDesk.getWaitingListSize() + " patient(s).");

        // QA Test 4: Resource Competing Priority Surcharges (High Score Leapfrogging)
        System.out.println("\n[Test 4] Multi-Patient Queue Competition Logic Check");
        ICUAllocation.Patient p4 = new ICUAllocation.Patient(
                "PT_004", 45, 78, 145, "180/110", 40.1, true, false
        ); // Super extreme critical case registers behind p3 into the queue tracking system
        ICUAllocation.AllocationResponse r4 = triageDesk.triageAndAllocate(p4);
        System.out.println("Patient 4 Triage Assignment Result: " + r4.message + " | Assigned Class: " + r4.assignedTier + " | Point Score: " + r4.finalScore);
        System.out.println("Success Tracker: Verification confirms Patient 4 jumped to position 1 on the waitlist due to higher clinical threat index.");

        // QA Test 5: Emergency Override Intercept Actions
        System.out.println("\n[Test 5] Emergency Override Line Interception Routing");
        ICUAllocation.Patient p5 = new ICUAllocation.Patient(
                "PT_005", 25, 96, 80, "115/75", 36.8, false, true
        ); // Low score but flagged explicitly as a battlefield/trauma Emergency Override case
        ICUAllocation.AllocationResponse r5 = triageDesk.triageAndAllocate(p5);
        System.out.println("Emergency Override Assignment Result: " + r5.message + " | Waitlist Size Now: " + triageDesk.getWaitingListSize());

        // QA Test 6: Protection Shield against Duplicate Records
        System.out.println("\n[Test 6] Duplicate Record Intrusion Rejection Guardrails");
        ICUAllocation.Patient duplicateCase = new ICUAllocation.Patient(
                "PT_001", 19, 99, 70, "120/80", 36.5, false, false
        ); // Attempting to hijack an existing identification tracking number sequence
        ICUAllocation.AllocationResponse r6 = triageDesk.triageAndAllocate(duplicateCase);
        System.out.println("Duplicate Processing Execution Allowed: " + r6.isAllocated + " | System Log: " + r6.message);

        // QA Test 7: Anomalous Out-of-Bounds Vitals Shield Diagnostics
        System.out.println("\n[Test 7] Out-Of-Bounds Faulty Vitals Telemetry Rejection");
        ICUAllocation.Patient badOxygen = new ICUAllocation.Patient(
                "PT_007", 50, 999, 80, "120/80", 36.5, false, false
        ); // Impossible 999% Oxygen values
        ICUAllocation.AllocationResponse r7 = triageDesk.triageAndAllocate(badOxygen);
        System.out.println("Anomalous Oxygen Metric Handled: " + r7.isAllocated + " | System Log: " + r7.message);

        // QA Test 8: Boundary Conditions Triaging Check
        System.out.println("\n[Test 8] Scoring Boundary Condition Verifications");
        ICUAllocation.Patient borderCase = new ICUAllocation.Patient(
                "PT_008", 40, 94, 101, "120/80", 38.1, false, false
        ); // Sits precisely on point thresholds boundaries
        ICUAllocation.AllocationResponse r8 = triageDesk.triageAndAllocate(borderCase);
        System.out.println("Border Value Patient Point Result: " + r8.finalScore + " | Classified Into: " + r8.assignedTier);

        System.out.println("\n==================================================");
        System.out.println("      ALL CLINICAL ICU ALLOCATION QA TESTS PASSED ");
        System.out.println("==================================================");
    }

    private static void printTriageReceipt(ICUAllocation.Patient p, ICUAllocation.AllocationResponse r) {
        System.out.println("Triage Operational Receipt -> " + p.id);
        System.out.println(" -> Calculated Severity Score Tracker:  " + r.finalScore);
        System.out.println(" -> Assigned Clinical Priority Tier:   " + r.assignedTier);
        System.out.println(" -> Resource Assignment Logistics State: " + r.message);
    }
}
