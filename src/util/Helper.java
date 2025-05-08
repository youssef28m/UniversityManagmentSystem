package util;

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
}
