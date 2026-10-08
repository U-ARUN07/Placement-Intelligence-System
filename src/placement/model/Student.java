package placement.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a student in the Placement Intelligence System.
 * Holds all relevant data needed for placement evaluation.
 */
public class Student {

    // ===== Constants =====
    public static final double MAX_CGPA          = 10.0;
    public static final int    MAX_SCORE         = 100;
    public static final double MAX_ATTENDANCE    = 100.0;

    // ===== Fields =====
    private final String       studentId;
    private       String       name;
    private       String       department;
    private       double       cgpa;
    private       int          codingScore;
    private       int          aptitudeScore;
    private       int          interviewScore;
    private       double       attendance;
    private final List<String> technicalSkills;

    // ===== Constructor =====
    public Student(String studentId, String name, String department,
                   double cgpa, int codingScore, int aptitudeScore,
                   int interviewScore, double attendance,
                   List<String> technicalSkills) {
        this.studentId      = studentId;
        this.name           = name;
        this.department     = department;
        this.cgpa           = cgpa;
        this.codingScore    = codingScore;
        this.aptitudeScore  = aptitudeScore;
        this.interviewScore = interviewScore;
        this.attendance     = attendance;
        this.technicalSkills = new ArrayList<>(technicalSkills);
    }

    // ===== Calculated Field =====
    /**
     * Overall score formula:
     *   CGPA        → 30% weight (normalized to 100: cgpa/10 * 100 * 0.30)
     *   CodingScore → 25% weight
     *   Aptitude    → 20% weight
     *   Interview   → 15% weight
     *   Attendance  → 10% weight
     *
     * Justification: CGPA has the highest weight as it reflects sustained
     * academic performance. Coding score is second-most important for
     * technical roles. Aptitude and interview assess soft skills and
     * communication. Attendance shows commitment.
     */
    public double calculateOverallScore() {
        double cgpaNorm = (cgpa / MAX_CGPA) * 100;
        return (cgpaNorm      * 0.30)
             + (codingScore   * 0.25)
             + (aptitudeScore * 0.20)
             + (interviewScore* 0.15)
             + (attendance    * 0.10);
    }

    // ===== Getters =====
    public String       getStudentId()      { return studentId; }
    public String       getName()           { return name; }
    public String       getDepartment()     { return department; }
    public double       getCgpa()           { return cgpa; }
    public int          getCodingScore()    { return codingScore; }
    public int          getAptitudeScore()  { return aptitudeScore; }
    public int          getInterviewScore() { return interviewScore; }
    public double       getAttendance()     { return attendance; }
    public List<String> getTechnicalSkills(){ return Collections.unmodifiableList(technicalSkills); }

    // ===== Setters (for updates) =====
    public void setName(String name)                   { this.name = name; }
    public void setDepartment(String department)       { this.department = department; }
    public void setCgpa(double cgpa)                   { this.cgpa = cgpa; }
    public void setCodingScore(int codingScore)        { this.codingScore = codingScore; }
    public void setAptitudeScore(int aptitudeScore)    { this.aptitudeScore = aptitudeScore; }
    public void setInterviewScore(int interviewScore)  { this.interviewScore = interviewScore; }
    public void setAttendance(double attendance)       { this.attendance = attendance; }
    public void addSkill(String skill)                 { if (!technicalSkills.contains(skill)) technicalSkills.add(skill); }

    @Override
    public String toString() {
        return String.format(
            "┌─ Student ID : %s%n" +
            "│  Name       : %s%n" +
            "│  Department : %s%n" +
            "│  CGPA       : %.2f%n" +
            "│  Coding     : %d | Aptitude: %d | Interview: %d%n" +
            "│  Attendance : %.1f%%%n" +
            "│  Skills     : %s%n" +
            "└─ Overall Score: %.2f",
            studentId, name, department, cgpa,
            codingScore, aptitudeScore, interviewScore,
            attendance, technicalSkills,
            calculateOverallScore()
        );
    }
}
