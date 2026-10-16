package com.lifysyn.util;

import com.lifysyn.model.Task;
import com.lifysyn.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVHandler handles all CSV file read/write operations.
 * This replaces the database layer for our project.
 */
public class CSVHandler {

    // =================== PATH CONFIGURATION ===================
    // Update these paths to match your system before running!
    private static final String DATA_PATH = "data/";
    private static final String USERS_CSV = DATA_PATH + "users.csv";
    private static final String TASK_PREF_CSV = DATA_PATH + "task_preferences.csv";
    private static final String USER_SCHEDULE_CSV = DATA_PATH + "user_schedule.csv";
    private static final String GENERATED_PLAN_CSV = DATA_PATH + "generated_plan.csv";

    // =================== USER METHODS ===================

    /**
     * Load all users from users.csv
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    User user = new User(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.csv: " + e.getMessage());
        }
        return users;
    }

    /**
     * Save a new user to users.csv
     */
    public static void saveUser(User user) {
        try (FileWriter fw = new FileWriter(USERS_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            // Check if file is empty (first user)
            File file = new File(USERS_CSV);
            if (file.length() == 0) {
                bw.write("userId,name,email,password");
                bw.newLine();
            }
            bw.write(user.getUserId() + "," + user.getName() + "," +
                     user.getEmail() + "," + user.getPassword());
            bw.newLine();
            System.out.println("User saved: " + user.getEmail());
        } catch (IOException e) {
            System.err.println("Error writing to users.csv: " + e.getMessage());
        }
    }

    /**
     * Get next available user ID
     */
    public static int getNextUserId() {
        List<User> users = loadUsers();
        int maxId = 0;
        for (User u : users) {
            if (u.getUserId() > maxId) maxId = u.getUserId();
        }
        return maxId + 1;
    }

    /**
     * Check if email already exists
     */
    public static boolean emailExists(String email) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    // =================== TASK METHODS ===================

    /**
     * Load all task preferences from task_preferences.csv
     */
    public static List<Task> loadTaskPreferences() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TASK_PREF_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",", 7); // limit 7 to handle reason with commas
                if (parts.length >= 7) {
                    Task task = new Task(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        Integer.parseInt(parts[5].trim()),
                        parts[6].trim()
                    );
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading task_preferences.csv: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Get a single task preference by name
     */
    public static Task getTaskByName(String taskName) {
        List<Task> tasks = loadTaskPreferences();
        for (Task t : tasks) {
            if (t.getTaskName().equalsIgnoreCase(taskName)) return t;
        }
        return null;
    }

    // =================== SCHEDULE METHODS ===================

    /**
     * Save generated plan entries to generated_plan.csv
     */
    public static void saveGeneratedPlan(int userId, String date, List<Task> tasks) {
        try (FileWriter fw = new FileWriter(GENERATED_PLAN_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            File file = new File(GENERATED_PLAN_CSV);
            if (file.length() == 0) {
                bw.write("userId,date,task,best_time,alternate_time_1,alternate_time_2,avoid_time,final_recommended_time");
                bw.newLine();
            }
            for (Task t : tasks) {
                bw.write(userId + "," + date + "," + t.getTaskName() + "," +
                         t.getBestTime() + "," + t.getAlternateTime1() + "," +
                         t.getAlternateTime2() + "," + t.getAvoidTime() + "," +
                         t.getFinalRecommendedTime());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to generated_plan.csv: " + e.getMessage());
        }
    }

    /**
     * Save user schedule to user_schedule.csv
     */
    public static void saveUserSchedule(int userId, String date, String occupationType,
                                        String occupiedStart, String occupiedEnd,
                                        String bufferStart, String bufferEnd) {
        try (FileWriter fw = new FileWriter(USER_SCHEDULE_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            File file = new File(USER_SCHEDULE_CSV);
            if (file.length() == 0) {
                bw.write("userId,date,occupationType,occupiedStart,occupiedEnd,bufferStart,bufferEnd");
                bw.newLine();
            }
            bw.write(userId + "," + date + "," + occupationType + "," +
                     occupiedStart + "," + occupiedEnd + "," +
                     bufferStart + "," + bufferEnd);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to user_schedule.csv: " + e.getMessage());
        }
    }
}