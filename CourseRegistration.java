import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseRegistration {

    // Structural model defining a University Course
    public static class Course {
        public String code;
        public String name;
        public int credits;
        public String prerequisite; // Null or empty if none
        public String timeSlot;      // e.g., "MON_09" (Monday 9 AM), "WED_11"
        public int capacity;
        public int enrolledCount;
        public int allowedSemester;  // Minimum semester allowed for this course

        public Course(String code, String name, int credits, String prerequisite, String timeSlot, int capacity, int allowedSemester) {
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.prerequisite = prerequisite;
            this.timeSlot = timeSlot;
            this.capacity = capacity;
            this.enrolledCount = 0;
            this.allowedSemester = allowedSemester;
        }
    }

    // Structural model defining a Student Profile
    public static class Student {
        public String id;
        public String program;
        public int semester;
        public int maxCreditLimit;
        public Set<String> completedCourses; // Set of course codes already passed
        public Set<String> registeredCourses; // Set of course codes currently registered

        public Student(String id, String program, int semester, int maxCreditLimit) {
            this.id = id;
            this.program = program;
            this.semester = semester;
            this.maxCreditLimit = maxCreditLimit;
            this.completedCourses = new HashSet<>();
            this.registeredCourses = new HashSet<>();
        }
    }

    // Response model containing systematic transaction registration metrics
    public static class RegistrationResponse {
        public boolean success;
        public String message;
        public int totalRegisteredCredits;

        public RegistrationResponse(boolean success, String message, int totalRegisteredCredits) {
            this.success = success;
            this.message = message;
            this.totalRegisteredCredits = totalRegisteredCredits;
        }
    }

    private Map<String, Course> courseCatalog;
    private Map<String, Student> studentRegistry;

    public CourseRegistration() {
        this.courseCatalog = new HashMap<>();
        this.studentRegistry = new HashMap<>();
    }

    // Methods to initialize master database catalogs
    public void addCourseToCatalog(Course course) {
        courseCatalog.put(course.code, course);
    }

    public void registerStudentProfile(Student student) {
        studentRegistry.put(student.id, student);
    }

    // Core Business Logic: Multi-Tier Academic Constraint Check Engine
    public RegistrationResponse registerCoursesForSemester(String studentId, List<String> selectedCourseCodes) {
        // --- 1. DATA VALIDATION & SECURITY SECURITY SHIELDING ---
        if (!studentRegistry.containsKey(studentId)) {
            return new RegistrationResponse(false, "Registration Failed: Student ID not found in registry.", 0);
        }
        if (selectedCourseCodes == null || selectedCourseCodes.isEmpty()) {
            return new RegistrationResponse(false, "Registration Failed: No courses selected for enrollment.", 0);
        }

        Student student = studentRegistry.get(studentId);
        
        // Track transaction processing states locally to maintain atomic rollback features
        Set<String> trialRegistrations = new HashSet<>();
        Set<String> occupiedTimeSlots = new HashSet<>();
        int runningCreditSum = 0;

        // Loop over the batch input array list to verify core university restrictions
        for (String code : selectedCourseCodes) {
            
            // Rule A: Invalid Course Code Verification Checks
            if (!courseCatalog.containsKey(code)) {
                return new RegistrationResponse(false, "Registration Failed: Course code '" + code + "' does not exist in master catalog.", 0);
            }

            Course course = courseCatalog.get(code);

            // Rule B: Prevent Duplicate Registration Attempts within the same batch window
            if (trialRegistrations.contains(code) || student.registeredCourses.contains(code)) {
                return new RegistrationResponse(false, "Registration Failed: Duplicate registration detected for course " + code + ".", 0);
            }

            // Rule C: Semester Restriction Verification Checks
            if (student.semester < course.allowedSemester) {
                return new RegistrationResponse(false, "Registration Failed: Course " + code + " is restricted to semester " + course.allowedSemester + " and above.", 0);
            }

            // Rule D: Course Capacity Exhaustion Verification
            if (course.enrolledCount >= course.capacity) {
                return new RegistrationResponse(false, "Registration Failed: Course " + code + " is completely full.", 0);
            }

            // Rule E: Prerequisite Completion Verification Graphs
            if (course.prerequisite != null && !course.prerequisite.trim().isEmpty()) {
                if (!student.completedCourses.contains(course.prerequisite)) {
                    return new RegistrationResponse(false, "Registration Failed: Missing missing prerequisite '" + course.prerequisite + "' for course " + code + ".", 0);
                }
            }

            // Rule F: Structural Timetable Clash / Overlap Detection Routines
            if (occupiedTimeSlots.contains(course.timeSlot)) {
                return new RegistrationResponse(false, "Registration Failed: Timetable conflict detected at slot [" + course.timeSlot + "].", 0);
            }

            // Temporarily commit metrics to transaction checking state maps
            trialRegistrations.add(code);
            occupiedTimeSlots.add(course.timeSlot);
            runningCreditSum += course.credits;
        }

        // Rule G: Maximum Boundary Credit Limits Check Constraints
        if (runningCreditSum > student.maxCreditLimit) {
            return new RegistrationResponse(false, "Registration Failed: Credit limit violation. Requested " + runningCreditSum + " credits, maximum allowed is " + student.maxCreditLimit + ".", 0);
        }

        // --- 2. EXECUTION MUTATION COMMITMENT LAYER ---
        // All checks passed error-free -> Atomically persist enrollment records to memory models
        for (String code : trialRegistrations) {
            student.registeredCourses.add(code);
            courseCatalog.get(code).enrolledCount++;
        }

        return new RegistrationResponse(true, "Registration successful! Enrollment list committed to university records database.", runningCreditSum);
    }
}
