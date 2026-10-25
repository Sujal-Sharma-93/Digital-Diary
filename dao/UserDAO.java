package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import exception.DuplicateUserException;
import exception.InvalidLoginException;
import model.User;
import util.DBConnection;

public class UserDAO {

    // Registers a new user in the database.
    public void register(User user) throws SQLException, DuplicateUserException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateUserException("That username is already registered.");
        } finally {
            closeResources(statement, connection);
        }
    }

    // Checks the username and password and returns the matching user.
    public User login(String username, String password) throws SQLException, InvalidLoginException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT user_id, username, password FROM users WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                return user;
            } else {
                throw new InvalidLoginException("Incorrect username or password.");
            }
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    // Closes the statement and connection safely.
    private void closeResources(PreparedStatement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Could not close the prepared statement.");
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Could not close the database connection.");
            }
        }
    }

    // Closes the result set, statement and connection safely.
    private void closeResources(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Could not close the result set.");
            }
        }

        closeResources(statement, connection);
    }
}
