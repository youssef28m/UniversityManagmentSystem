package com;

import com.academics.*;
import com.database.DatabaseManager;
import com.system.University;
import com.users.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        ConsoleUI consoleUI = new ConsoleUI(new University());
        consoleUI.start();




    }
}