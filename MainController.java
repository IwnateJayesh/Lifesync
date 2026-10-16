package com.lifysyn.servlet;

import com.lifysyn.model.Task;
import com.lifysyn.model.User;
import com.lifysyn.service.AuthService;
import com.lifysyn.service.SchedulerService;
import com.lifysyn.util.CSVHandler;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("   LIFESYN - Backend Demo Runner");
        System.out.println("====================================\n");

        AuthService authService = new AuthService();
        SchedulerService schedulerService = new SchedulerService();

        System.out.println("[1] Registering user...");
        String result = authService.registerUser("Arjun Sharma", "arjun@lifysyn.com", "pass1234");
        System.out.println("    Registration result: " + result);

        System.out.println("\n[2] Signing in...");
        User loggedUser = authService.loginUser("arjun@lifysyn.com", "pass1234");

        if (loggedUser != null) {
            System.out.println("    Login successful! Welcome, " + loggedUser.getName());
        } else {
            System.out.println("    Login failed!");
            System.out.println("    Stopping program because login failed.");
            return;
        }

        String occupiedStart = "09:00";
        String occupiedEnd = "17:00";

        System.out.println("\n[3] Occupied time: " + occupiedStart + " - " + occupiedEnd);
        System.out.println("    Buffer window: " + schedulerService.getBufferInfo(occupiedStart, occupiedEnd));

        String today = LocalDate.now().toString();
        String bufferStart = schedulerService.calculateBufferStart(occupiedStart);
        String bufferEnd = schedulerService.calculateBufferEnd(occupiedEnd);

        CSVHandler.saveUserSchedule(
                loggedUser.getUserId(),
                today,
                "College",
                occupiedStart,
                occupiedEnd,
                bufferStart,
                bufferEnd
        );

        List<String> selectedTasks = Arrays.asList("study", "exercise", "eat", "sleep", "rest");

        System.out.println("\n[4] Selected tasks: " + selectedTasks);

        System.out.println("\n[5] Generating personalized plan...\n");

        List<Task> plan = schedulerService.generatePlan(selectedTasks, occupiedStart, occupiedEnd);

        System.out.printf("%-20s %-20s %-20s %-20s %-20s %-25s%n",
                "TASK", "BEST TIME", "ALT TIME 1", "ALT TIME 2", "AVOID TIME", "FINAL RECOMMENDED");

        System.out.println("-".repeat(130));

        for (Task t : plan) {
            System.out.printf("%-20s %-20s %-20s %-20s %-20s %-25s%n",
                    t.getTaskName(),
                    t.getBestTime(),
                    t.getAlternateTime1(),
                    t.getAlternateTime2(),
                    t.getAvoidTime(),
                    t.getFinalRecommendedTime()
            );
        }

        CSVHandler.saveGeneratedPlan(loggedUser.getUserId(), today, plan);

        System.out.println("\n[6] Plan saved to generated_plan.csv");

        System.out.println("\n====================================");
        System.out.println("   Backend demo completed!");
        System.out.println("   Now open index.html in browser");
        System.out.println("====================================");
    }
}