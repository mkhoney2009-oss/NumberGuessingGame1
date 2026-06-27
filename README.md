import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ==========================================
// 1. COURSE ENTITY CLASS
// ==========================================
class Course {
    private final String code;
    private final String name;
    private final int maxCapacity;
    private int currentEnrollment;

    public Course(String code, String name, int maxCapacity) {
        this.code = code;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = 0;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isFull() { return currentEnrollment >= maxCapacity; }

    public void incrementEnrollment() { currentEnrollment++; }
}

// ==========================================
// 2. ENROLLED COURSE ASSIGNMENT RECORD
// ==========================================
class EnrolledCourse {
    private final Course course;
    private Integer numericGrade; // Null signifies that no grade has been posted yet
    private String letterGrade;
    private double gradePoints;

    public EnrolledCourse(Course course) {
        this.course = course;
        this.numericGrade = null;
        this.letterGrade = "N/A";
        this.gradePoints = -1.0;
    }

    public Course getCourse() { return course; }
    public Integer getNumericGrade() { return numericGrade; }
    public String getLetterGrade() { return letterGrade; }
    public double getGradePoints() { return gradePoints; }
    public boolean hasGrade() { return numericGrade != null; }

    public void setGrade(int score) {
        this.numericGrade = score;
        if (score >= 90 && score <= 100) {
            this.letterGrade = "A";
            this.gradePoints = 4.0;
        } else if (score >= 80) {
            this.letterGrade = "B";
            this.gradePoints = 3.0;
        } else if (score >= 70) {
            this.letterGrade = "C";
            this.gradePoints = 2.0;
        } else if (score >= 60) {
            this.letterGrade = "D";
            this.gradePoints = 1.0;
        } else {
            this.letterGrade = "F";
            this.gradePoints = 0.0;
        }
    }
}

// ==========================================
// 3. STUDENT ENTITY CLASS
// ==========================================
class Student {
    private final String id;
    private final String name;
    private final int age;
    private final List<EnrolledCourse> enrolledCourses;

    public Student(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.enrolledCourses = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public boolean isEnrolledIn(String courseCode) {
        for (EnrolledCourse ec : enrolledCourses) {
            if (ec.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public void addCourseEnrollment(Course course) {
        enrolledCourses.add(new EnrolledCourse(course));
    }

    public EnrolledCourse getEnrolledCourseRecord(String courseCode) {
        for (EnrolledCourse ec : enrolledCourses) {
            if (ec.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                return ec;
            }
        }
        return null;
    }

    // Core Operation: Get GPA
    public double calculateGPA() {
        double totalPoints = 0;
        int gradedCoursesCount = 0;

        for (EnrolledCourse ec : enrolledCourses) {
            if (ec.hasGrade()) {
                totalPoints += ec.getGradePoints();
                gradedCoursesCount++;
            }
        }

        if (gradedCoursesCount == 0) return 0.0;
        return totalPoints / gradedCoursesCount;
    }

    // Core Operation: Get Transcript
    public void printTranscript() {
        System.out.println("\n--- Transcript ---");
        for (EnrolledCourse ec : enrolledCourses) {
            String gradeDisplay = ec.hasGrade() ? String.valueOf(ec.getNumericGrade()) : "Pending";
            String letterDisplay = ec.getLetterGrade();
            String pointsDisplay = ec.hasGrade() ? String.valueOf(ec.getGradePoints()) : "N/A";

            System.out.println(ec.getCourse().getCode() + " - " + ec.getCourse().getName() +
                    " | Grade: " + gradeDisplay + " | " + letterDisplay + " | " + pointsDisplay);
        }
        System.out.printf("GPA: %.2f\n", calculateGPA());
    }
}

// ==========================================
// 4. CENTRAL MANAGEMENT SYSTEM REGISTRY
// ==========================================
public class StudentManagementSystem {
    private final Map<String, Student> studentsDatabase = new HashMap<>();
    private final Map<String, Course> coursesDatabase = new HashMap<>();

    // System Utility: Add Course
    public void addCourse(String code, String name, int maxCapacity) {
        coursesDatabase.put(code.toUpperCase(), new Course(code, name, maxCapacity));
    }

    // Core Operation: Add Student
    public void addStudent(String id, String name, int age) {
        if (studentsDatabase.containsKey(id)) {
            System.out.println("Error: Student ID " + id + " already exists.");
            return;
        }
        studentsDatabase.put(id, new Student(id, name, age));
    }

    // Core Operation: Remove Student
    public void removeStudent(String id) {
        if (!studentsDatabase.containsKey(id)) {
            System.out.println("Error: Student with ID " + id + " does not exist.");
            return;
        }
        studentsDatabase.remove(id);
        System.out.println("Student " + id + " removed successfully from the system.");
    }

    // Core Operation: Enroll Student
    public void enrollStudentInCourse(String studentId, String courseCode) {
        Student student = studentsDatabase.get(studentId);
        Course course = coursesDatabase.get(courseCode.toUpperCase());

        if (student == null) {
            System.out.println("Error: Student does not exist.");
            return;
        }
        if (course == null) {
            System.out.println("Error: Course does not exist.");
            return;
        }
        if (student.isEnrolledIn(courseCode)) {
            System.out.println("Enroll in " + courseCode + " again → Error: Already enrolled");
            return;
        }
        if (course.isFull()) {
            System.out.println("Enroll in " + courseCode + " → Error: Course has reached its maximum capacity.");
            return;
        }

        student.addCourseEnrollment(course);
        course.incrementEnrollment();
        System.out.println("Enroll in " + courseCode + " (" + course.getName() + ") → Success");
    }

    // Core Operation: Add Grade
    public void addGrade(String studentId, String courseCode, int score) {
        if (score < 0 || score > 100) {
            System.out.println("Error: Grade is outside the 0–100 range.");
            return;
        }

        Student student = studentsDatabase.get(studentId);
        if (student == null) {
            System.out.println("Error: Student does not exist.");
            return;
        }

        EnrolledCourse record = student.getEnrolledCourseRecord(courseCode);
        if (record == null) {
            System.out.println("Error: Trying to add a grade for a course (" + courseCode + ") the student isn't enrolled in.");
            return;
        }

        record.setGrade(score);
        System.out.println("Add Grade: " + courseCode + " → " + score + " → " + record.getLetterGrade() + " (" + record.getGradePoints() + ")");
    }

    public Student getStudent(String id) {
        return studentsDatabase.get(id);
    }

    // ==========================================
    // MAIN RUNTIME METRICS DEMONSTRATION
    // ==========================================
    public static void main(String[] args) {
        StudentManagementSystem system = new StudentManagementSystem();

        // Prepare Course Registries
        system.addCourse("CS101", "Data Structures", 30);
        system.addCourse("CS102", "Algorithms", 40);

        // Add Target Student Profile
        system.addStudent("S001", "Ahmed Ali", 20);
        System.out.println("Student: [ID: S001 | Name: Ahmed Ali]");

        // Run the prompt's exact structural workflow simulation
        system.enrollStudentInCourse("S001", "CS101");
        system.enrollStudentInCourse("S001", "CS101"); // Expected Error Case
        system.enrollStudentInCourse("S001", "CS102");

        // Assign Performance Grades
        system.addGrade("S001", "CS101", 85);
        system.addGrade("S001", "CS102", 92);

        // Render Student Grade Distribution Ledgers
        Student ahmed = system.getStudent("S001");
        if (ahmed != null) {
            ahmed.printTranscript();
        }
    }
}
