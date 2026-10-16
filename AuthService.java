package com.lifysyn.service;

import com.lifysyn.model.User;
import com.lifysyn.util.CSVHandler;

import java.util.List;

/**
 * AuthService handles user registration and login logic.
 */
public class AuthService {

    /**
     * Register a new user.
     * Returns "SUCCESS" or an error message.
     */
    public String registerUser(String name, String email, String password) {

        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }

        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        if (!email.contains("@")) {
            return "Invalid email format.";
        }

        email = email.trim();
        name = name.trim();

        if (CSVHandler.emailExists(email)) {
            return "Email already registered. Please sign in.";
        }

        int newId = CSVHandler.getNextUserId();

        User newUser = new User(newId, name, email, password);

        CSVHandler.saveUser(newUser);

        return "SUCCESS";
    }

    /**
     * Authenticate a user by email and password.
     * Returns User object if valid, null if not found.
     */
    public User loginUser(String email, String password) {

        if (email == null || password == null) {
            return null;
        }

        email = email.trim();

        List<User> users = CSVHandler.loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && u.getPassword().equals(password)) {
                return u;
            }
        }

        return null;
    }

    /**
     * Get user by email.
     */
    public User getUserByEmail(String email) {

        if (email == null) {
            return null;
        }

        email = email.trim();

        List<User> users = CSVHandler.loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }

        return null;
    }
}