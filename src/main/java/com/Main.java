package com;

import com.database.DatabaseManager;


public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.createTables();

    }
}