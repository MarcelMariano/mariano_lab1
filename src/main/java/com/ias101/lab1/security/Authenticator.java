package com.ias101.lab1.security;

import com.ias101.lab1.database.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Authenticator {

    public static boolean authenticateUser(String username, String password) {
        // Validate input: Both username and password allow special characters
        if (!isValidUsername(username) || !isValidPassword(password)) {
            System.out.println("Invalid username or password format.");

            // Additional message specifying what went wrong
            if (!isValidUsername(username)) {
                System.out.println("Error: Username contains invalid characters. Allowed: Letters, Numbers, Spaces, and Special Characters.");
            }
            if (!isValidPassword(password)) {
                System.out.println("Error: Password contains invalid characters. Allowed: Letters, Numbers, and Special Characters.");
            }

            return false;
        }

        // Sanitize input by escaping single and double quotes to prevent SQL injection
        String sanitizedUsername = sanitizeInput(username);
        String sanitizedPassword = sanitizeInput(password);

        String query = "SELECT username FROM user_data WHERE username = '" + sanitizedUsername + "' AND password = '" + sanitizedPassword + "'";

        try (Connection conn = DBUtil.connect("jdbc:sqlite:src/main/resources/database/sample.db", "root", "root");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                System.out.println("User authenticated: " + sanitizedUsername);
                return true;
            } else {
                System.out.println("No matching user found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            return false;
        }
    }

    // Validate username: Allows all letters, numbers, spaces, and special characters
    private static boolean isValidUsername(String username) {
        return username != null && username.matches("^[\\w !@#$%^&*()_+={}|:;'<>,.?/\"\\\\-]+$");
    }

    // Validate password: Allows all printable special characters including double quotes
    private static boolean isValidPassword(String password) {
        return password != null && password.matches("^[a-zA-Z0-9!@#$%^&*()_+={}|:;'<>,.?/\"\\\\-]+$");
    }

    // Check if input contains invalid characters (for debugging/logging)
    private static boolean hasInvalidCharacters(String input) {
        return input != null && !input.matches("^[a-zA-Z0-9!@#$%^&*()_+={}|:;'<>,.?/\"\\\\-]+$");
    }

    // Sanitization: Escape single quotes and double quotes to prevent SQL injection
    private static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replace("'", "''").replace("\"", "\\\"");
    }
}
