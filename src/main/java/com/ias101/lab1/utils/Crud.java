package com.ias101.lab1.utils;

import com.ias101.lab1.database.util.DBUtil;
import com.ias101.lab1.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for performing CRUD operations on user data in the database.
 */
public class Crud {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/sample.db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // SQL queries
    private static final String SELECT_ALL_USERS = "SELECT * FROM user_data";
    private static final String SEARCH_USER = "SELECT * FROM user_data WHERE username = ?";
    private static final String DELETE_USER = "DELETE FROM user_data WHERE username = ?";

    /**
     * Retrieves <b>all users</b> from the database.
     *
     * @return List of all User objects in the database
     */
    public static List<User> getAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
    }

    /**
     * Searches for a user by their <b>username</b>.
     *
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public static User searchByUsername(String username) {
        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(SEARCH_USER)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching for user: " + username, e);
        }
        return null;
    }

    /**
     * Deletes a user from the database by their username.
     *
     * @param username The username of the user to delete
     */
    public static void deleteUserByUsername(String username) {
        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(DELETE_USER)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.printf("User with username '%s' has been removed.%n", username);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + username, e);
        }
    }

    /**
     * Extracts user data from a ResultSet and creates a User object.
     *
     * @param rs The ResultSet containing user data
     * @return User object created from the ResultSet data
     */
    private static User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
