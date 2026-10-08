package placement;

import placement.analytics.PlacementAnalytics;
import placement.exception.PlacementException;
import placement.model.Student;
import placement.service.CompanyEligibilityService;
import placement.service.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main entry point for the Placement Intelligence System.
 * Provides an interactive console menu for all operations.
 */
public class Main {

    private static final StudentService          studentService   = new StudentService();
    private static final PlacementAnalytics      analytics        = new PlacementAnalytics(studentService);
    private static final CompanyEligibilityService companyService = new CompanyEligibilityService();
    private static final Scanner                 scanner          = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\n" + "★".repeat(60));
        System.out.println("       PLACEMENT INTELLIGENCE SYSTEM");
        System.out.println("       HCL Advanced Java Mini Hackathon");
        System.out.println("★".repeat(60));

        // Load sample data for demonstration
        loadSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            try {
                running = handleChoice(input);
            } catch (PlacementException e) {
                System.out.println("\n❌ Error: " + e);
            } catch (Exception e) {
                System.out.println("\n⚠️  Unexpected error: " + e.getMessage());
            }
        }

        System.out.println("\n👋 Thank you for using Placement Intelligence System. Goodbye!");
        scanner.close();
    }

    // ======================== MENU ========================

    private static void printMenu() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("                    MAIN MENU");
        System.out.println("─".repeat(55));
        System.out.println("  STUDENT MANAGEMENT");
        System.out.println("  [1] Add Student");
        System.out.println("  [2] Remove Student");
        System.out.println("  [3] Search Student by ID");
        System.out.println("  [4] Display All Students");
        System.out.println();
        System.out.println("  PLACEMENT READINESS");
        System.out.println("  [5] Check Placement Status (All Students)");
        System.out.println("  [6] Check Placement Status (Specific Student)");
        System.out.println();
        System.out.println("  RANKING & ANALYTICS");
        System.out.println("  [7] Student Rankings");
        System.out.println("  [8] Top 5 Students");
        System.out.println("  [9] Best Performing Department");
        System.out.println("  [10] Avg Coding Score by Department");
        System.out.println("  [11] Most Common Technical Skills");
        System.out.println("  [12] Students with Most Skills");
        System.out.println("  [13] Students Requiring Improvement");
        System.out.println("  [14] Full Analytics Report");
        System.out.println();
        System.out.println("  ⭐ COMPANY ELIGIBILITY (Creative Feature)");
        System.out.println("  [15] Check Company Eligibility (Student)");
        System.out.println("  [16] Company Eligibility Matrix (All)");
        System.out.println();
        System.out.println("  [0] Exit");
        System.out.println("─".repeat(55));
    }

    private static boolean handleChoice(String choice) throws PlacementException {
        switch (choice) {
            case "1"  -> addStudentInteractive();
            case "2"  -> removeStudentInteractive();
            case "3"  -> searchStudentInteractive();
            case "4"  -> studentService.displayAllStudents();
            case "5"  -> studentService.displayPlacementStatus();
            case "6"  -> checkStatusInteractive();
            case "7"  -> studentService.displayRanking();
            case "8"  -> analytics.showTop5Students();
            case "9"  -> analytics.showBestPerformingDepartment();
            case "10" -> analytics.showAvgCodingScoreByDept();
            case "11" -> analytics.showMostCommonSkills();
            case "12" -> analytics.showStudentsWithMostSkills();
            case "13" -> analytics.showStudentsRequiringImprovement();
            case "14" -> analytics.generateFullReport();
            case "15" -> checkCompanyEligibilityInteractive();
            case "16" -> companyService.showCompanyEligibilityMatrix(studentService.getAllStudents());
            case "0"  -> { return false; }
            default   -> System.out.println("⚠️  Invalid choice. Please enter a number from 0 to 16.");
        }
        return true;
    }

    // ======================== INTERACTIVE HANDLERS ========================

    private static void addStudentInteractive() throws PlacementException {
        System.out.println("\n--- ADD NEW STUDENT ---");
        System.out.print("Student ID    : "); String id   = scanner.nextLine().trim();
        System.out.print("Name          : "); String name = scanner.nextLine().trim();
        System.out.print("Department    : "); String dept = scanner.nextLine().trim();

        System.out.print("CGPA (0-10)   : ");
        double cgpa = parseDouble(scanner.nextLine().trim(), "CGPA");

        System.out.print("Coding Score (0-100) : ");
        int coding = parseInt(scanner.nextLine().trim(), "Coding Score");

        System.out.print("Aptitude Score (0-100): ");
        int aptitude = parseInt(scanner.nextLine().trim(), "Aptitude Score");

        System.out.print("Interview Score (0-100): ");
        int interview = parseInt(scanner.nextLine().trim(), "Interview Score");

        System.out.print("Attendance % (0-100) : ");
        double attendance = parseDouble(scanner.nextLine().trim(), "Attendance");

        System.out.print("Technical Skills (comma-separated): ");
        String skillsInput = scanner.nextLine().trim();
        List<String> skills = Arrays.stream(skillsInput.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

        studentService.addStudent(id, name, dept, cgpa, coding, aptitude,
                                  interview, attendance, skills);
    }

    private static void removeStudentInteractive() throws PlacementException {
        System.out.print("\nEnter Student ID to remove: ");
        String id = scanner.nextLine().trim();
        studentService.removeStudent(id);
    }

    private static void searchStudentInteractive() {
        System.out.print("\nEnter Student ID to search: ");
        String id = scanner.nextLine().trim();
        Optional<Student> result = studentService.searchStudent(id);
        result.ifPresentOrElse(
            s -> {
                System.out.println("\n✅ Student Found:");
                System.out.println(s);
                System.out.println("   Placement Status: "
                    + studentService.evaluatePlacementStatus(s));
            },
            () -> System.out.println("❌ No student found with ID: " + id)
        );
    }

    private static void checkStatusInteractive() {
        System.out.print("\nEnter Student ID: ");
        String id = scanner.nextLine().trim();
        studentService.searchStudent(id).ifPresentOrElse(
            s -> System.out.println("\nPlacement Status for " + s.getName()
                + ": " + studentService.evaluatePlacementStatus(s)),
            () -> System.out.println("❌ Student not found: " + id)
        );
    }

    private static void checkCompanyEligibilityInteractive() {
        System.out.print("\nEnter Student ID: ");
        String id = scanner.nextLine().trim();
        studentService.searchStudent(id).ifPresentOrElse(
            s -> companyService.checkEligibility(s),
            () -> System.out.println("❌ Student not found: " + id)
        );
    }

    // ======================== PARSE HELPERS ========================

    private static double parseDouble(String input, String fieldName) throws PlacementException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new PlacementException(
                "Invalid value for " + fieldName + ": '" + input + "'",
                "PARSE_ERROR"
            );
        }
    }

    private static int parseInt(String input, String fieldName) throws PlacementException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new PlacementException(
                "Invalid value for " + fieldName + ": '" + input + "'",
                "PARSE_ERROR"
            );
        }
    }

    // ======================== SAMPLE DATA ========================

    /**
     * Loads a realistic set of sample students to demonstrate all features.
     */
    private static void loadSampleData() {
        System.out.println("\n⏳ Loading sample student data...");
        Object[][] data = {
            // id,    name,           dept,  cgpa, coding, apt, intrvw, att,  skills
            {"S001", "Arjun Sharma",   "CSE", 8.9,  88,  82,  85,  90.0,  new String[]{"Java","Python","SQL","Spring","Linux"}},
            {"S002", "Priya Nair",     "CSE", 8.2,  76,  78,  80,  88.0,  new String[]{"Java","JavaScript","React","SQL"}},
            {"S003", "Rohit Verma",    "ECE", 7.8,  65,  70,  68,  82.0,  new String[]{"C","Embedded C","MATLAB"}},
            {"S004", "Sneha Patel",    "IT",  9.1,  92,  88,  91,  95.0,  new String[]{"Java","Python","ML","TensorFlow","SQL","Docker"}},
            {"S005", "Kiran Reddy",    "CSE", 7.5,  60,  62,  58,  75.0,  new String[]{"Java","HTML","CSS"}},
            {"S006", "Divya Menon",    "IT",  6.8,  55,  60,  50,  70.0,  new String[]{"Python","HTML"}},
            {"S007", "Arun Kumar",     "MECH",6.2,  40,  45,  42,  65.0,  new String[]{"AutoCAD"}},
            {"S008", "Meena Iyer",     "CSE", 7.2,  82,  75,  70,  78.0,  new String[]{"Java","Python","SQL","Git"}},
            {"S009", "Vijay Singh",    "ECE", 8.5,  78,  80,  79,  86.0,  new String[]{"C++","VHDL","MATLAB","Python"}},
            {"S010", "Pooja Gupta",    "IT",  7.0,  83,  70,  65,  72.0,  new String[]{"Java","SQL","AWS"}},
        };

        for (Object[] row : data) {
            try {
                String[] skillArr = (String[]) row[8];
                studentService.addStudent(
                    (String) row[0], (String) row[1], (String) row[2],
                    (double) row[3], (int) row[4], (int) row[5],
                    (int) row[6], (double) row[7],
                    Arrays.asList(skillArr)
                );
            } catch (PlacementException e) {
                System.out.println("⚠️  Sample data error: " + e);
            }
        }
        System.out.println("✅ Sample data loaded successfully. 10 students registered.\n");
    }
}
