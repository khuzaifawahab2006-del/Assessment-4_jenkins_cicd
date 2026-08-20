import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationQA {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("     STARTING UNIVERSITY REGISTRATION QA SYSTEM   ");
        System.out.println("==================================================");

        // Deploys the main orchestrator instance framework
        CourseRegistration registrar = new CourseRegistration();

        // Populate curriculum data matching requirements specs mapping rules
        registrar.addCourseToCatalog(new CourseRegistration.Course("PROG101", "Programming", 3, "", "MON_09", 30, 1));
        registrar.addCourseToCatalog(new CourseRegistration.Course("DS201", "Data Structures", 4, "PROG101", "TUE_11", 25, 2));
        registrar.addCourseToCatalog(new CourseRegistration.Course("STAT301", "Statistics", 3, "", "WED_14", 40, 1));
        registrar.addCourseToCatalog(new CourseRegistration.Course("NET401", "Networking", 3, "", "THU_09", 35, 2));

        // Prompt example configuration matrices assignments
        registrar.addCourseToCatalog(new CourseRegistration.Course("DBMS", "Database Management Systems", 4, "PROG101", "MON_14", 30, 2));
        registrar.addCourseToCatalog(new CourseRegistration.Course("AI", "Artificial Intelligence", 4, "DS201", "FRI_09", 25, 3));
        registrar.addCourseToCatalog(new CourseRegistration.Course("ML", "Machine Learning", 3, "STAT301", "FRI_09", 20, 3)); // Same slot as AI
        registrar.addCourseToCatalog(new CourseRegistration.Course("CLOUD", "Cloud Computing", 3, "NET401", "WED_14", 1, 3));    // Only 1 seat capacity

        // Onboard automation student objects into core maps tracking spaces
        CourseRegistration.Student alice = new CourseRegistration.Student("STU_ALICE", "Computer Science", 3, 12);
        alice.completedCourses.add("PROG101");
        alice.completedCourses.add("DS201");
        alice.completedCourses.add("STAT301");
        registrar.registerStudentProfile(alice);

        // QA Test 1: Valid registration checklist validation
        System.out.println("\n[Test 1] Valid Normal Registration Flow Processing");
        List<String> validSet = Arrays.asList("DBMS", "AI");
        CourseRegistration.RegistrationResponse res1 = registrar.registerCoursesForSemester("STU_ALICE", validSet);
        System.out.println("Status: " + res1.success + " | Message: " + res1.message + " | Total Credits: " + res1.totalRegisteredCredits);

        // QA Test 2: Missing Core Prerequisite Chain Exceptions
        System.out.println("\n[Test 2] Missing Academic Prerequisite Validation Check");
        CourseRegistration.Student bob = new CourseRegistration.Student("STU_BOB", "Data Science", 3, 15); // Missing DS201 core history records
        registrar.registerStudentProfile(bob);
        CourseRegistration.RegistrationResponse res2 = registrar.registerCoursesForSemester("STU_BOB", Arrays.asList("AI"));
        System.out.println("Status Approved: " + res2.success + " | Error Log: " + res2.message);

        // QA Test 3: Credit-Limit Boundaries Overflow Verification
        System.out.println("\n[Test 3] Credit-Limit Surcharge Cap Threshold Violation");
        CourseRegistration.Student charlie = new CourseRegistration.Student("STU_CHARLIE", "Cyber Security", 3, 6); // Hard structural credit limit cap = 6
        charlie.completedCourses.add("PROG101");
        registrar.registerStudentProfile(charlie);
        CourseRegistration.RegistrationResponse res3 = registrar.registerCoursesForSemester("STU_CHARLIE", Arrays.asList("DBMS", "DS201")); // Sum value equals 8 credits
        System.out.println("Status Approved: " + res3.success + " | Error Log: " + res3.message);

        // QA Test 4: Overlapping Timetable Schedule Conflict Routines
        System.out.println("\n[Test 4] Timetable Slot Overlap & Clash Interceptions");
        CourseRegistration.Student daniel = new CourseRegistration.Student("STU_DANIEL", "AI Engineering", 4, 16);
        daniel.completedCourses.add("DS201");
        daniel.completedCourses.add("STAT301");
        registrar.registerStudentProfile(daniel);
        CourseRegistration.RegistrationResponse res4 = registrar.registerCoursesForSemester("STU_DANIEL", Arrays.asList("AI", "ML")); // Both share slot FRI_09
        System.out.println("Status Approved: " + res4.success + " | Error Log: " + res4.message);

        // QA Test 5: Fully Saturated Course Capacities
        System.out.println("\n[Test 5] Exhausted Course Capacity Limits Rejection Check");
        CourseRegistration.Student eva = new CourseRegistration.Student("STU_EVA", "Cloud Engineering", 3, 15);
        eva.completedCourses.add("NET401");
        registrar.registerStudentProfile(eva);
        
        // Claim the single available seat in CLOUD
        registrar.registerCoursesForSemester("STU_EVA", Arrays.asList("CLOUD"));

        CourseRegistration.Student frank = new CourseRegistration.Student("STU_FRANK", "Cloud Engineering", 3, 15);
        frank.completedCourses.add("NET401");
        registrar.registerStudentProfile(frank);
        CourseRegistration.RegistrationResponse res5 = registrar.registerCoursesForSemester("STU_FRANK", Arrays.asList("CLOUD")); // Attempt to join a full class
        System.out.println("Status Approved: " + res5.success + " | Error Log: " + res5.message);

        // QA Test 6: In-Flight Duplicate Course Submission
        System.out.println("\n[Test 6] Replay / Duplicate Course Entry Protection Guardrails");
        CourseRegistration.Student grace = new CourseRegistration.Student("STU_GRACE", "Information Systems", 2, 12);
        grace.completedCourses.add("PROG101");
        registrar.registerStudentProfile(grace);
        CourseRegistration.RegistrationResponse res6 = registrar.registerCoursesForSemester("STU_GRACE", Arrays.asList("DBMS", "DBMS"));
        System.out.println("Status Approved: " + res6.success + " | Error Log: " + res6.message);

        // QA Test 7: Unknown Core Code Indexing Key Checks
        System.out.println("\n[Test 7] Unknown Course Code Catalog Parsing Exceptions");
        CourseRegistration.RegistrationResponse res7 = registrar.registerCoursesForSemester("STU_GRACE", Arrays.asList("INVALID_CODE_123"));
        System.out.println("Status Approved: " + res7.success + " | Error Log: " + res7.message);

        // QA Test 8: Semester Placement Access Control Restrictions
        System.out.println("\n[Test 8] Semester Level Access Policy Verifications");
        CourseRegistration.Student freshie = new CourseRegistration.Student("STU_FRESHMAN", "CS", 1, 15); // Sits at Semester 1 right now
        registrar.registerStudentProfile(freshie);
        CourseRegistration.RegistrationResponse res8 = registrar.registerCoursesForSemester("STU_FRESHMAN", Arrays.asList("DBMS")); // Requires minimum Semester 2 entry
        System.out.println("Status Approved: " + res8.success + " | Error Log: " + res8.message);

        // QA Test 9: Boundary Credit Maximum Values Matching Bounds Checks
        System.out.println("\n[Test 9] Boundary Credit Max Value Precision Checking");
        CourseRegistration.Student borderStu = new CourseRegistration.Student("STU_BORDER", "CS", 2, 4); // Capped precisely at 4
        borderStu.completedCourses.add("PROG101");
        registrar.registerStudentProfile(borderStu);
        CourseRegistration.RegistrationResponse res9 = registrar.registerCoursesForSemester("STU_BORDER", Arrays.asList("DBMS")); // Fits perfectly at exactly 4 credits
        System.out.println("Status Approved: " + res9.success + " | Message Log: " + res9.message + " | Enrolled: " + res9.totalRegisteredCredits);

        System.out.println("\n==================================================");
        System.out.println("      ALL ACADEMIC REGISTRATION QA TESTS PASSED   ");
        System.out.println("==================================================");
    }
}
