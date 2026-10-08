# Placement Intelligence System
### HCL Advanced Java Mini Hackathon

---

## Application Overview

A console-based **Placement Intelligence System** built in Java that helps college placement teams manage student data, evaluate readiness, generate rankings, and produce analytics reports. The system supports a realistic placement workflow — from adding students to checking company-specific eligibility.

---

## How to Compile and Run

```bash
# Compile
javac -d out -sourcepath src src/placement/Main.java

# Run
java -cp out placement.Main
```

---

## Project Structure

```
src/
└── placement/
    ├── Main.java                              ← Entry point, menu, sample data
    ├── model/
    │   ├── Student.java                       ← Student entity with score calculation
    │   └── PlacementStatus.java               ← Enum for placement categories
    ├── service/
    │   ├── StudentService.java                ← CRUD, evaluation, ranking
    │   └── CompanyEligibilityService.java     ← Creative feature: company matcher
    ├── analytics/
    │   └── PlacementAnalytics.java            ← Reports using Stream API
    ├── exception/
    │   └── PlacementException.java            ← Custom exception with error codes
    └── util/
        └── Validator.java                     ← Input validation utility
```

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `LinkedHashMap` for student storage | Preserves insertion order for consistent display while providing O(1) lookups |
| Custom `PlacementException` with error codes | Enables specific error handling without multiple exception types |
| Weighted scoring formula in `Student` | Keeps score logic with the entity, not scattered in services |
| `Optional<Student>` for search | Avoids null pointer issues; forces callers to handle missing data |
| Static utility class for `Validator` | No state needed; keeps validation logic separate from business logic |
| `PlacementStatus` as enum | Type-safe classification; easy to add future status categories |

---

## Placement Criteria

### Classification Logic

```
PLACEMENT READY ✅
  ├── CGPA >= 7.5
  ├── Coding Score >= 60
  ├── Aptitude Score >= 60
  ├── Interview Score >= 60
  └── Attendance >= 75%

NEEDS IMPROVEMENT ⚠️
  Case A: CGPA >= 7.5 but fails any one score or attendance threshold
  Case B: CGPA in [7.0, 7.5) AND Coding >= 80 AND Skills >= 3  ← Special Policy

NOT ELIGIBLE ❌
  └── CGPA < 7.0
      OR CGPA in [7.0, 7.5) but doesn't meet the special policy
```

### Special Policy — Placement Team Update

> *"Students with CGPA between 7.0 and 7.5 can still qualify if they have a Coding Score of at least 80 and possess at least 3 technical skills."*

**Implementation**: Added a second condition block inside `evaluatePlacementStatus()` in `StudentService.java`. No redesign was needed — the existing `PlacementStatus` enum already had the `NEEDS_IMPROVEMENT` category. We simply added one extra `if` block to handle the new policy. This demonstrates the **Open/Closed Principle** — open for extension, closed for modification of existing logic.

---

## Overall Score Formula

```
Overall Score = (CGPA/10 × 100 × 30%)
              + (Coding Score  × 25%)
              + (Aptitude Score × 20%)
              + (Interview Score × 15%)
              + (Attendance     × 10%)
```

**Justification**:
- **CGPA (30%)** — Measures sustained academic performance over the entire course
- **Coding (25%)** — Most critical technical skill for software roles
- **Aptitude (20%)** — Tests logical reasoning and problem-solving
- **Interview (15%)** — Communication and real-time thinking
- **Attendance (10%)** — Indicator of discipline and commitment

---

## Java Concepts Used

| Concept | Where Used |
|---|---|
| **OOP** (Encapsulation) | `Student.java` — private fields, getters/setters |
| **Enum** | `PlacementStatus.java` — type-safe categories |
| **Custom Exception** | `PlacementException.java` — with error codes |
| **Collections** | `LinkedHashMap`, `ArrayList`, `List` throughout |
| **Stream API** | `PlacementAnalytics.java` — all report generation |
| **Lambda Expressions** | Sorting, filtering, grouping in analytics |
| **Collectors** | `groupingBy`, `averagingDouble`, `counting`, `limit` |
| **Optional** | `searchStudent()` — null-safe student lookup |
| **Functional Interfaces** | `Comparator` lambdas for ranking |
| **Immutable Collections** | `Collections.unmodifiableList()` in Student |
| **`List.of()` / `.toList()`** | Modern Java stream terminal ops |
| **`ifPresentOrElse`** | Optional-based search feedback |

---

## AI-Generated Code Review

### Original AI-Generated Code (Skill Frequency Report)

```java
// AI-generated version
public void showMostCommonSkills() {
    Map<String, Integer> skillCount = new HashMap<>();
    for (Student s : students) {
        for (String skill : s.getTechnicalSkills()) {
            if (skillCount.containsKey(skill)) {
                skillCount.put(skill, skillCount.get(skill) + 1);
            } else {
                skillCount.put(skill, 1);
            }
        }
    }
    List<Map.Entry<String, Integer>> sorted = new ArrayList<>(skillCount.entrySet());
    sorted.sort((a, b) -> b.getValue() - a.getValue());
    for (Map.Entry<String, Integer> e : sorted) {
        System.out.println(e.getKey() + " : " + e.getValue());
    }
}
```

### Issues Identified

1. **Verbose manual loop** instead of Stream API — misses modern Java features
2. **`HashMap` doesn't preserve order** — `TreeMap` would be better for manual approach
3. **`containsKey()` + `put()`** pattern — should use `getOrDefault()` or `merge()`
4. **Mutable sort on `entrySet()`** — creates unnecessary intermediate collection
5. **No header/formatting** — raw output is hard to read

### Our Improved Version

```java
public void showMostCommonSkills() {
    Map<String, Long> skillFreq = service.getAllStudents().stream()
        .flatMap(s -> s.getTechnicalSkills().stream())
        .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

    System.out.println("\n" + "=".repeat(45));
    System.out.println("         MOST COMMON TECHNICAL SKILLS");
    System.out.println("=".repeat(45));
    System.out.printf("%-25s %-10s%n", "Skill", "Count");
    System.out.println("-".repeat(45));

    skillFreq.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .forEach(e -> System.out.printf("%-25s %d%n", e.getKey(), e.getValue()));
}
```

**What We Changed and Why**:
- Replaced nested `for` loops with `flatMap` + `Collectors.groupingBy` — cleaner and more expressive
- Used `Long` with `Collectors.counting()` — idiomatic Java streams
- Used `Map.Entry.comparingByValue()` — avoids manual comparator bugs
- Added formatted table header for readable output

---

## Creative Feature ⭐ — Company Eligibility Checker

**File**: `CompanyEligibilityService.java`

### What it does:
- Defines **5 real company profiles** (TCS, Infosys, Wipro, HCL, Google) with specific requirements
- Menu option **[15]**: Detailed per-student eligibility report showing exactly which criteria a student fails and by how much (skill gap, score gap)
- Menu option **[16]**: **Company Eligibility Matrix** — shows all students vs all companies in a grid (✅/❌) with a total count

### Why it's useful:
- Placement officers can instantly see which company drives are relevant for which student
- Students get specific, actionable feedback: *"You need Java and Spring to qualify for HCL"*

---

## Sample Output

### Placement Status Report
```
ID           Name                 CGPA       Status
S001         Arjun Sharma         8.90       ✅ PLACEMENT READY
S008         Meena Iyer           7.20       ⚠️ NEEDS IMPROVEMENT
S010         Pooja Gupta          7.00       ⚠️ NEEDS IMPROVEMENT  ← Special Policy
S006         Divya Menon          6.80       ❌ NOT ELIGIBLE
```

### Student Ranking (Top 3)
```
Rank  ID     Name           Dept   Score
1     S004   Sneha Patel    IT     91.05
2     S001   Arjun Sharma   CSE    86.85
3     S009   Vijay Singh    ECE    81.45
```

---

## Team

| Member | Role |
|---|---|
| Member 1 | Student Model, Validator, Exception Handling |
| Member 2 | StudentService, Placement Logic, Special Policy |
| Member 3 | PlacementAnalytics, Stream API Reports |
| Member 4 | CompanyEligibilityService, Main Menu, README |
