package placement.util;

import placement.exception.PlacementException;
import placement.model.Student;

import java.util.List;

/**
 * Utility class for validating student data before adding to the system.
 */
public class Validator {

    private Validator() {
        // Utility class — no instantiation needed
    }

    /**
     * Validates all fields of a student.
     * Throws PlacementException with a descriptive error code if validation fails.
     */
    public static void validate(String studentId, String name, String department,
                                double cgpa, int codingScore, int aptitudeScore,
                                int interviewScore, double attendance,
                                List<String> skills) throws PlacementException {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new PlacementException("Student ID cannot be empty.", "INVALID_ID");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new PlacementException("Name cannot be empty.", "INVALID_NAME");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new PlacementException("Department cannot be empty.", "MISSING_DEPARTMENT");
        }
        if (cgpa < 0 || cgpa > Student.MAX_CGPA) {
            throw new PlacementException(
                "CGPA must be between 0 and " + Student.MAX_CGPA + ". Got: " + cgpa,
                "INVALID_CGPA"
            );
        }
        if (codingScore < 0 || codingScore > Student.MAX_SCORE) {
            throw new PlacementException(
                "Coding Score must be 0-" + Student.MAX_SCORE + ". Got: " + codingScore,
                "INVALID_CODING_SCORE"
            );
        }
        if (aptitudeScore < 0 || aptitudeScore > Student.MAX_SCORE) {
            throw new PlacementException(
                "Aptitude Score must be 0-" + Student.MAX_SCORE + ". Got: " + aptitudeScore,
                "INVALID_APTITUDE_SCORE"
            );
        }
        if (interviewScore < 0 || interviewScore > Student.MAX_SCORE) {
            throw new PlacementException(
                "Interview Score must be 0-" + Student.MAX_SCORE + ". Got: " + interviewScore,
                "INVALID_INTERVIEW_SCORE"
            );
        }
        if (attendance < 0 || attendance > Student.MAX_ATTENDANCE) {
            throw new PlacementException(
                "Attendance must be 0-100%. Got: " + attendance,
                "INVALID_ATTENDANCE"
            );
        }
        if (skills == null || skills.isEmpty()) {
            throw new PlacementException(
                "At least one technical skill must be provided.",
                "MISSING_SKILLS"
            );
        }
    }
}
