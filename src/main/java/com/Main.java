package com;

import com.database.DatabaseManager;
import com.system.University;
import com.users.AdminStaff;
import com.users.Student;
import com.users.Student;
import com.users.User;


public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        University university = new University();

        AdminStaff admin = db.getAdminStaff("240003");

    }
}