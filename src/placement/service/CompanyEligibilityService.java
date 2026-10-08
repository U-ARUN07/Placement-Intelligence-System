package placement.service;

import placement.model.Student;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creative Feature: Company Eligibility & Skill-Gap Analyzer
 *
 * Allows placement officers to define company-specific requirements
 * and check which students are eligible for each company.
 * Also provides personalized skill-gap recommendations.
 */
public class CompanyEligibilityService {

    /**
     * Represents a company with its placement requirements.
     * Uses a simple inner class to keep things self-contained.
     */
    public static class Company {
        private final String name;
        private final double minCgpa;
        private final int    minCodingScore;
        private final int    minAptitudeScore;
        private final double minAttendance;
        private final List<String> requiredSkills;

        public Company(String name, double minCgpa, int minCodingScore,
                       int minAptitudeScore, double minAttendance,
                       List<String> requiredSkills) {
            this.name             = name;
            this.minCgpa          = minCgpa;
            this.minCodingScore   = minCodingScore;
            this.minAptitudeScore = minAptitudeScore;
            this.minAttendance    = minAttendance;
            this.requiredSkills   = new ArrayList<>(requiredSkills);
        }

        public String       getName()            { return name; }
        public double       getMinCgpa()         { return minCgpa; }
        public int          getMinCodingScore()  { return minCodingScore; }
        public int          getMinAptitudeScore(){ return minAptitudeScore; }
        public double       getMinAttendance()   { return minAttendance; }
        public List<String> getRequiredSkills()  { return Collections.unmodifiableList(requiredSkills); }
    }

    // Predefined company profiles
    private final List<Company> companies;

    public CompanyEligibilityService() {
        companies = new ArrayList<>();
        // Load sample companies
        companies.add(new Company("TCS",     6.0, 50, 50, 70,
            Arrays.asList("Java", "SQL")));
        companies.add(new Company("Infosys", 6.5, 55, 55, 75,
            Arrays.asList("Java", "Python")));
        companies.add(new Company("Wipro",   7.0, 60, 60, 75,
            Arrays.asList("Java", "SQL", "Linux")));
        companies.add(new Company("HCL",     7.5, 65, 65, 80,
            Arrays.asList("Java", "Spring", "SQL")));
        companies.add(new Company("Google",  8.5, 85, 80, 85,
            Arrays.asList("Algorithms", "Java", "Python", "System Design")));
    }

    /**
     * Checks eligibility of a student for all companies.
     * Prints a report showing eligible companies and skill gaps.
     */
    public void checkEligibility(Student student) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("   COMPANY ELIGIBILITY REPORT — " + student.getName());
        System.out.println("=".repeat(65));
        System.out.printf("   CGPA: %.2f | Coding: %d | Aptitude: %d | Attendance: %.1f%%%n",
            student.getCgpa(), student.getCodingScore(),
            student.getAptitudeScore(), student.getAttendance());
        System.out.printf("   Skills: %s%n", student.getTechnicalSkills());
        System.out.println("-".repeat(65));

        for (Company c : companies) {
            boolean cgpaOk      = student.getCgpa()           >= c.getMinCgpa();
            boolean codingOk    = student.getCodingScore()    >= c.getMinCodingScore();
            boolean aptOk       = student.getAptitudeScore()  >= c.getMinAptitudeScore();
            boolean attOk       = student.getAttendance()     >= c.getMinAttendance();

            List<String> missingSkills = c.getRequiredSkills().stream()
                .filter(skill -> !student.getTechnicalSkills().contains(skill))
                .collect(Collectors.toList());

            boolean skillsOk = missingSkills.isEmpty();
            boolean eligible = cgpaOk && codingOk && aptOk && attOk && skillsOk;

            System.out.printf("%n  🏢 %-10s → %s%n",
                c.getName(), eligible ? "✅ ELIGIBLE" : "❌ NOT ELIGIBLE");

            if (!eligible) {
                System.out.println("     Gaps:");
                if (!cgpaOk)   System.out.printf("       • CGPA: need %.1f, have %.2f%n",
                    c.getMinCgpa(), student.getCgpa());
                if (!codingOk) System.out.printf("       • Coding: need %d, have %d%n",
                    c.getMinCodingScore(), student.getCodingScore());
                if (!aptOk)    System.out.printf("       • Aptitude: need %d, have %d%n",
                    c.getMinAptitudeScore(), student.getAptitudeScore());
                if (!attOk)    System.out.printf("       • Attendance: need %.0f%%, have %.1f%%%n",
                    c.getMinAttendance(), student.getAttendance());
                if (!skillsOk) System.out.println("       • Missing Skills: " + missingSkills);
            }
        }
        System.out.println();
    }

    /**
     * Generates a batch report of how many companies each student qualifies for.
     */
    public void showCompanyEligibilityMatrix(List<Student> students) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("              COMPANY ELIGIBILITY MATRIX");
        System.out.println("=".repeat(70));

        // Header
        System.out.printf("%-20s", "Student");
        for (Company c : companies) {
            System.out.printf("%-12s", c.getName());
        }
        System.out.println("| Total");
        System.out.println("-".repeat(70));

        for (Student s : students) {
            System.out.printf("%-20s", s.getName().length() > 18
                ? s.getName().substring(0, 18) : s.getName());
            int count = 0;
            for (Company c : companies) {
                boolean ok = s.getCgpa()           >= c.getMinCgpa()
                          && s.getCodingScore()    >= c.getMinCodingScore()
                          && s.getAptitudeScore()  >= c.getMinAptitudeScore()
                          && s.getAttendance()     >= c.getMinAttendance()
                          && c.getRequiredSkills().stream()
                               .allMatch(sk -> s.getTechnicalSkills().contains(sk));
                System.out.printf("%-12s", ok ? "✅" : "❌");
                if (ok) count++;
            }
            System.out.println("| " + count + "/" + companies.size());
        }
    }
}
