package com.util;

import com.database.DatabaseManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Helper {

    public static boolean isValidDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static String generateStudentId() {
        return "test";
    }
    public static String generateFacultyId() {
        return "test";
    }
    public static String generateAdminStaffId() {
        return "test";
    }
    public static String generateSystemAdminId() {
        return "test";
    }
}
