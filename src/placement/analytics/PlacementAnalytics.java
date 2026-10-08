package placement.analytics;

import placement.model.PlacementStatus;
import placement.model.Student;
import placement.service.StudentService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics engine for generating placement reports.
 * Uses Java Stream API and lambda expressions for data processing.
 */
public class PlacementAnalytics {

    private final StudentService service;

    public PlacementAnalytics(StudentService service) {
        this.service = service;
    }

    // ======================== TOP 5 STUDENTS ========================

    /**
     * Displays the top 5 students by overall score.
     */
    public void showTop5Students() {
        List<Student> top5 = service.getRankedStudents().stream()
            .limit(5)
            .collect(Collectors.toList());

        System.out.println("\n" + "=".repeat(65));
        System.out.println("                    TOP 5 STUDENTS");
        System.out.println("=".repeat(65));
        System.out.printf("%-5s %-12s %-20s %-6s %-10s%n",
            "Rank", "ID", "Name", "CGPA", "Overall");
        System.out.println("-".repeat(65));

        for (int i = 0; i < top5.size(); i++) {
            Student s = top5.get(i);
            System.out.printf("%-5d %-12s %-20s %-6.2f %.2f%n",
                (i + 1), s.getStudentId(), s.getName(),
                s.getCgpa(), s.calculateOverallScore());
        }
    }

    // ======================== BEST PERFORMING DEPARTMENT ========================

    /**
     * Calculates and displays average overall score per department.
     * The department with the highest average is declared best-performing.
     */
    public void showBestPerformingDepartment() {
        Map<String, Double> deptAverage = service.getAllStudents().stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.averagingDouble(Student::calculateOverallScore)
            ));

        System.out.println("\n" + "=".repeat(55));
        System.out.println("           DEPARTMENT PERFORMANCE SUMMARY");
        System.out.println("=".repeat(55));
        System.out.printf("%-20s %-15s%n", "Department", "Avg Overall Score");
        System.out.println("-".repeat(55));

        deptAverage.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%-20s %.2f%n", e.getKey(), e.getValue()));

        deptAverage.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(e ->
                System.out.println("\n🏆 Best Department: " + e.getKey()
                    + " (Avg Score: " + String.format("%.2f", e.getValue()) + ")")
            );
    }

    // ======================== AVERAGE CODING SCORE BY DEPT ========================

    /**
     * Displays average coding score grouped by department.
     */
    public void showAvgCodingScoreByDept() {
        Map<String, Double> avgCoding = service.getAllStudents().stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.averagingInt(Student::getCodingScore)
            ));

        System.out.println("\n" + "=".repeat(50));
        System.out.println("       AVG CODING SCORE BY DEPARTMENT");
        System.out.println("=".repeat(50));
        System.out.printf("%-20s %-15s%n", "Department", "Avg Coding Score");
        System.out.println("-".repeat(50));

        avgCoding.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%-20s %.2f%n", e.getKey(), e.getValue()));
    }

    // ======================== MOST COMMON TECHNICAL SKILLS ========================

    /**
     * Counts frequency of each technical skill across all students.
     * Displays skills sorted by frequency (most common first).
     */
    public void showMostCommonSkills() {
        Map<String, Long> skillFreq = service.getAllStudents().stream()
            .flatMap(s -> s.getTechnicalSkills().stream())
            .collect(Collectors.groupingBy(
                skill -> skill,
                Collectors.counting()
            ));

        System.out.println("\n" + "=".repeat(45));
        System.out.println("         MOST COMMON TECHNICAL SKILLS");
        System.out.println("=".repeat(45));
        System.out.printf("%-25s %-10s%n", "Skill", "Count");
        System.out.println("-".repeat(45));

        skillFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%-25s %d%n", e.getKey(), e.getValue()));
    }

    // ======================== STUDENTS WITH MOST SKILLS ========================

    /**
     * Displays students who have the most technical skills.
     * If multiple students share the top count, all are shown.
     */
    public void showStudentsWithMostSkills() {
        if (service.getAllStudents().isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        int maxSkills = service.getAllStudents().stream()
            .mapToInt(s -> s.getTechnicalSkills().size())
            .max()
            .orElse(0);

        List<Student> topSkilled = service.getAllStudents().stream()
            .filter(s -> s.getTechnicalSkills().size() == maxSkills)
            .collect(Collectors.toList());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("       STUDENTS WITH MOST SKILLS (" + maxSkills + " skills)");
        System.out.println("=".repeat(60));

        topSkilled.forEach(s ->
            System.out.printf("%-12s %-20s Skills: %s%n",
                s.getStudentId(), s.getName(), s.getTechnicalSkills())
        );
    }

    // ======================== STUDENTS REQUIRING IMPROVEMENT ========================

    /**
     * Lists students who are classified as NEEDS IMPROVEMENT or NOT ELIGIBLE.
     */
    public void showStudentsRequiringImprovement() {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("           STUDENTS REQUIRING IMPROVEMENT");
        System.out.println("=".repeat(65));
        System.out.printf("%-12s %-20s %-6s %-20s%n", "ID", "Name", "CGPA", "Status");
        System.out.println("-".repeat(65));

        boolean found = false;
        for (Student s : service.getAllStudents()) {
            PlacementStatus status = service.evaluatePlacementStatus(s);
            if (status != PlacementStatus.PLACEMENT_READY) {
                System.out.printf("%-12s %-20s %-6.2f %s%n",
                    s.getStudentId(), s.getName(), s.getCgpa(), status);
                found = true;
            }
        }
        if (!found) {
            System.out.println("🎉 All students are Placement Ready!");
        }
    }

    // ======================== FULL ANALYTICS REPORT ========================

    public void generateFullReport() {
        System.out.println("\n" + "#".repeat(65));
        System.out.println("           PLACEMENT INTELLIGENCE — FULL ANALYTICS REPORT");
        System.out.println("#".repeat(65));
        showTop5Students();
        showBestPerformingDepartment();
        showAvgCodingScoreByDept();
        showMostCommonSkills();
        showStudentsWithMostSkills();
        showStudentsRequiringImprovement();
    }
}
