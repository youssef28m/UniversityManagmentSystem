import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import users.*;


public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.createTables();

        ArrayList<List<Object>> allCourses = db.getAllCourses();

        int coursesToShow = Math.min(2, allCourses.size());
        for (int i = 0; i < coursesToShow; i++) {
            List<Object> course = allCourses.get(i);
            System.out.println("Course #" + (i+1) + ":");
            System.out.println("ID: " + course.get(0));
            System.out.println("Title: " + course.get(1));
            System.out.println("Description: " + course.get(2));
            System.out.println("Credit Hours: " + course.get(3));
            System.out.println("Instructor ID: " + course.get(4));
            System.out.println("Max Capacity: " + course.get(5));
            System.out.println("Schedule: " + course.get(6));
            System.out.println("-----------------------------------");
        }



    }
}