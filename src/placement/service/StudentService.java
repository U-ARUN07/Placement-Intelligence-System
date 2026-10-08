package placement.service;

import placement.exception.PlacementException;
import placement.model.PlacementStatus;
import placement.model.Student;
import placement.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service for managing students in the Placement Intelligence System.
 * Handles CRUD operations, validation, and placement status evaluation.
 */
public class StudentService {

    // Using LinkedHashMap to preserve insertion order
    private final Map<String, Student> studentMap = new LinkedHashMap<>();

    // ======================== STUDENT MANAGEMENT ========================

    /**
     * Adds a new student after validation.
     */
    public void addStudent(String studentId, String name, String department,
                           double cgpa, int codingScore, int aptitudeScore,
                           int interviewScore, double attendance,
                           List<String> skills) throws PlacementException {

        // Validate all inputs first
        Validator.validate(studentId, name, department, cgpa, codingScore,
                           aptitudeScore, interviewScore, attendance, skills);

        // Check for duplicate ID
        if (studentMap.containsKey(studentId)) {
            throw new PlacementException(
                "Student with ID '" + studentId + "' already exists.",
                "DUPLICATE_ID"
            );
        }

        Student student = new Student(studentId, name, department, cgpa,
                                      codingScore, aptitudeScore, interviewScore,
                                      attendance, skills);
        studentMap.put(studentId, student);
        System.out.println("✅ Student added successfully: " + name + " [" + studentId + "]");
    }

    /**
     * Removes a student by ID.
     */
    public void removeStudent(String studentId) throws PlacementException {
        if (!studentMap.containsKey(studentId)) {
            throw new PlacementException(
                "Student with ID '" + studentId + "' not found.",
                "NOT_FOUND"
            );
        }
        String name = studentMap.get(studentId).getName();
        studentMap.remove(studentId);
        System.out.println("🗑️  Student removed: " + name + " [" + studentId + "]");
    }

    /**
     * Searches for a student by ID and returns an Optional.
     */
    public Optional<Student> searchStudent(String studentId) {
        return Optional.ofNullable(studentMap.get(studentId));
    }

    /**
     * Returns all students as an unmodifiable list.
     */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(studentMap.values()));
    }

    /**
     * Displays all students.
     */
    public void displayAllStudents() {
        if (studentMap.isEmpty()) {
            System.out.println("⚠️  No students registered in the system.");
            return;
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           ALL REGISTERED STUDENTS");
        System.out.println("=".repeat(60));
        studentMap.values().forEach(s -> {
            System.out.println(s);
            System.out.println("-".repeat(60));
        });
    }

    // ======================== PLACEMENT READINESS ========================

    /**
     * Evaluates the placement status of a student.
     *
     * Standard Criteria (PLACEMENT READY):
     *   - CGPA >= 7.5
     *   - Coding Score >= 60
     *   - Aptitude Score >= 60
     *   - Interview Score >= 60
     *   - Attendance >= 75%
     *
     * Special Policy (NEEDS IMPROVEMENT — partial eligibility):
     *   - CGPA in [7.0, 7.5) AND Coding Score >= 80 AND Skills >= 3
     *
     * NOT ELIGIBLE:
     *   - CGPA < 7.0  OR  does not meet special policy conditions
     */
    public PlacementStatus evaluatePlacementStatus(Student student) {
        double cgpa    = student.getCgpa();
        int    coding  = student.getCodingScore();
        int    apt     = student.getAptitudeScore();
        int    intrvw  = student.getInterviewScore();
        double att     = student.getAttendance();
        int    skills  = student.getTechnicalSkills().size();

        // Standard eligibility check
        if (cgpa >= 7.5 && coding >= 60 && apt >= 60 && intrvw >= 60 && att >= 75.0) {
            return PlacementStatus.PLACEMENT_READY;
        }

        // Student meets base CGPA but fails on other criteria → NEEDS IMPROVEMENT
        if (cgpa >= 7.5) {
            return PlacementStatus.NEEDS_IMPROVEMENT;
        }

        // Special policy: CGPA in [7.0, 7.5) with high coding + skills
        if (cgpa >= 7.0 && coding >= 80 && skills >= 3) {
            return PlacementStatus.NEEDS_IMPROVEMENT;
        }

        // Everything else
        return PlacementStatus.NOT_ELIGIBLE;
    }

    /**
     * Displays the placement status of all students.
     */
    public void displayPlacementStatus() {
        if (studentMap.isEmpty()) {
            System.out.println("⚠️  No students to evaluate.");
            return;
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           PLACEMENT STATUS REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("%-12s %-20s %-10s %s%n", "ID", "Name", "CGPA", "Status");
        System.out.println("-".repeat(60));
        studentMap.values().forEach(s -> {
            PlacementStatus status = evaluatePlacementStatus(s);
            System.out.printf("%-12s %-20s %-10.2f %s%n",
                s.getStudentId(), s.getName(), s.getCgpa(), status);
        });
    }

    // ======================== STUDENT RANKING ========================

    /**
     * Returns a ranked list of students by overall score (highest first).
     */
    public List<Student> getRankedStudents() {
        return studentMap.values().stream()
            .sorted(Comparator.comparingDouble(Student::calculateOverallScore).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Displays the student ranking table.
     */
    public void displayRanking() {
        List<Student> ranked = getRankedStudents();
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                STUDENT RANKING (By Overall Score)");
        System.out.println("=".repeat(70));
        System.out.printf("%-5s %-12s %-20s %-10s %-10s%n",
            "Rank", "ID", "Name", "Dept", "Score");
        System.out.println("-".repeat(70));
        for (int i = 0; i < ranked.size(); i++) {
            Student s = ranked.get(i);
            System.out.printf("%-5d %-12s %-20s %-10s %.2f%n",
                (i + 1), s.getStudentId(), s.getName(),
                s.getDepartment(), s.calculateOverallScore());
        }
    }
}
