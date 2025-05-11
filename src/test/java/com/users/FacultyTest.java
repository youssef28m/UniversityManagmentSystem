package com.users;

import com.academics.Course;
import com.academics.Enrollment;
import com.database.DatabaseManager;
import com.users.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.users.Faculty;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacultyTest {

    private Faculty faculty;
    private DatabaseManager mockDb;
    private Course course;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize Faculty with empty courses list
        ArrayList<Course> courses = new ArrayList<>();
        faculty = new Faculty("Faculty", "F001", "johndoe", "password", "John Doe", "john@example.com", "1234567890", "F240001", 1, "Computer Science", courses);

        // Mock DatabaseManager for Faculty
        mockDb = mock(DatabaseManager.class);
        Field dbField = Faculty.class.getDeclaredField("db");
        dbField.setAccessible(true);
        dbField.set(faculty, mockDb);

        // Initialize Course
        course = new Course(101, "Intro to CS", "Description", 3, new ArrayList<>(), "Mon 9-11", "F240001", 30, new ArrayList<>(), 1);

        // Mock DatabaseManager for Course
        Field courseDbField = Course.class.getDeclaredField("db");
        courseDbField.setAccessible(true);
        courseDbField.set(course, mockDb);
    }

    @Test
    public void testConstructor() {
        assertEquals("F001", faculty.getUserId());
        assertEquals("johndoe", faculty.getUsername());
        assertEquals("password", faculty.getPassword());
        assertEquals("John Doe", faculty.getName());
        assertEquals("john@example.com", faculty.getEmail());
        assertEquals("1234567890", faculty.getContactInfo());
        assertEquals("F240001", faculty.getFacultyId());
        assertEquals(1, faculty.getDepartment());
        assertEquals("Computer Science", faculty.getExpertise());
        assertTrue(faculty.getCoursesTeaching().isEmpty());
    }

    @Test
    public void testSetOfficeHours() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        faculty.setOfficeHours("Mon 10-12");
        assertEquals("Mon 10-12", faculty.getOfficeHours());
        assertTrue(outContent.toString().contains("Office hours updated to: Mon 10-12"));
        System.setOut(System.out);
    }

    @Test
    public void testUpdateProfile() {
        when(mockDb.updateUserProfile(eq("F001"), any(String[].class))).thenReturn(true);
        boolean result = faculty.updateProfile();
        assertTrue(result);
        verify(mockDb).updateUserProfile(eq("F001"), argThat(userData ->
                userData[0].equals("johndoe") &&
                        userData[1].equals("John Doe") &&
                        userData[2].equals("john@example.com") &&
                        userData[3].equals("1234567890") &&
                        userData[4].equals("Faculty")
        ));
    }

    @Test
    public void testAddCourse_Success() {
        when(mockDb.updateCourse(any(Course.class))).thenReturn(true);
        boolean result = faculty.addCourse(course);
        assertTrue(result);
        assertEquals("F240001", course.getInstructor());
        assertEquals(1, course.getDepartment());
        assertTrue(faculty.getCoursesTeaching().contains(course));
        verify(mockDb).updateCourse(course);
    }

    @Test
    public void testAddCourse_AlreadyTeaching() {
        faculty.getCoursesTeaching().add(course);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        boolean result = faculty.addCourse(course);
        assertFalse(result);
        assertTrue(outContent.toString().contains("The course is already being taught by the faculty."));
        verify(mockDb, never()).updateCourse(any(Course.class));
        System.setOut(System.out);
    }

    @Test
    public void testAddCourse_NullCourse() {
        assertThrows(IllegalArgumentException.class, () -> faculty.addCourse(null));
    }

    @Test
    public void testAssignGrades_CourseNotTaught() {
        Course otherCourse = new Course(102, "Advanced CS", "Description", 3, new ArrayList<>(), "Tue 9-11", "F240002", 30, new ArrayList<>(), 1);
        Student student = new Student("Student", "S001", "janedoe", "pass123", "Jane Doe", "jane@example.com", "0987654321", "S240001", "2023-01-01", "Active", new ArrayList<>());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        faculty.assignGrades(student, otherCourse, 90);
        assertTrue(outContent.toString().contains("You do not teach this course."));
        verify(mockDb, never()).getEnrollment(anyString(), anyInt());
        verify(mockDb, never()).updateEnrollment(any(Enrollment.class));
        System.setOut(System.out);
    }

    @Test
    public void testAssignGrades_NoEnrollment() {
        faculty.getCoursesTeaching().add(course);
        Student student = new Student("Student", "S001", "janedoe", "pass123", "Jane Doe", "jane@example.com", "0987654321", "S240001", "2023-01-01", "Active", new ArrayList<>());
        when(mockDb.getEnrollment(student.getStudentId(), course.getCourseId())).thenReturn(null);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        faculty.assignGrades(student, course, 90);
        assertTrue(outContent.toString().contains("The student is not registered in this course."));
        verify(mockDb).getEnrollment(student.getStudentId(), course.getCourseId());
        verify(mockDb, never()).updateEnrollment(any(Enrollment.class));
        System.setOut(System.out);
    }

    @Test
    public void testAssignGrades_Success() {
        faculty.getCoursesTeaching().add(course);
        Student student = new Student("Student", "S001", "janedoe", "pass123", "Jane Doe", "jane@example.com", "0987654321", "S240001", "2023-01-01", "Active", new ArrayList<>());
        Enrollment enrollment = new Enrollment(student, course, "active");
        when(mockDb.getEnrollment(student.getStudentId(), course.getCourseId())).thenReturn(enrollment);
        when(mockDb.updateEnrollment(enrollment)).thenReturn(true);
        faculty.assignGrades(student, course, 85);
        assertEquals(85, enrollment.getGrade());
        verify(mockDb).getEnrollment(student.getStudentId(), course.getCourseId());
        verify(mockDb).updateEnrollment(enrollment);
    }

    @Test
    public void testViewStudentRoster() {
        faculty.getCoursesTeaching().add(course);
        Student student1 = new Student("Student", "S001", "janedoe", "pass123", "Jane Doe", "jane@example.com", "0987654321", "S240001", "2023-01-01", "Active", new ArrayList<>());
        Student student2 = new Student("Student", "S002", "bobsmith", "pass456", "Bob Smith", "bob@example.com", "1122334455", "S240002", "2023-01-01", "Active", new ArrayList<>());

        // Mock DatabaseManager.getAllStudents to return student data
        ArrayList<String[]> dbStudents = new ArrayList<>();
        dbStudents.add(new String[]{student1.getStudentId(), "2023-01-01", "Active", "S001"});
        dbStudents.add(new String[]{student2.getStudentId(), "2023-01-01", "Active", "S002"});
        when(mockDb.getAllStudents()).thenReturn(dbStudents);

        // Add students to course
        course.addStudent(student1);
        course.addStudent(student2);

        // Mock DatabaseManager.getStudent
        when(mockDb.getStudent(student1.getStudentId())).thenReturn(student1);
        when(mockDb.getStudent(student2.getStudentId())).thenReturn(student2);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        faculty.viewStudentRoster(course);
        String output = outContent.toString();
        assertTrue(output.contains("STUDENT ROSTER: Intro to CS (101)"));
        assertTrue(output.contains("S240001"));
        assertTrue(output.contains("Jane Doe"));
        assertTrue(output.contains("S240002"));
        assertTrue(output.contains("Bob Smith"));
        verify(mockDb).getStudent(student1.getStudentId());
        verify(mockDb).getStudent(student2.getStudentId());
        System.setOut(System.out);
    }
}