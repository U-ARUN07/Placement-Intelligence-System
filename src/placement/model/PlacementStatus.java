package placement.model;

/**
 * Enum representing the placement readiness category of a student.
 *
 * Classification Criteria:
 *
 *  PLACEMENT READY:
 *    - CGPA >= 7.5 AND
 *    - Coding Score >= 60 AND
 *    - Aptitude Score >= 60 AND
 *    - Interview Score >= 60 AND
 *    - Attendance >= 75%
 *
 *  NEEDS IMPROVEMENT:
 *    - CGPA between 7.0 and 7.5 (exclusive) AND
 *      (Coding Score >= 80 AND TechnicalSkills >= 3)   [Special Policy]
 *    OR
 *    - CGPA >= 7.5 but fails one or more of:
 *      Coding/Aptitude/Interview < 60, OR Attendance < 75%
 *
 *  NOT ELIGIBLE:
 *    - CGPA < 7.0
 *    OR does not meet the special policy conditions when CGPA is in [7.0, 7.5)
 */
public enum PlacementStatus {

    PLACEMENT_READY  ("PLACEMENT READY",   "✅"),
    NEEDS_IMPROVEMENT("NEEDS IMPROVEMENT", "⚠️"),
    NOT_ELIGIBLE     ("NOT ELIGIBLE",      "❌");

    private final String label;
    private final String icon;

    PlacementStatus(String label, String icon) {
        this.label = label;
        this.icon  = icon;
    }

    public String getLabel() { return label; }
    public String getIcon()  { return icon; }

    @Override
    public String toString() {
        return icon + " " + label;
    }
}
