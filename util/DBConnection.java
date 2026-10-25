package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/digital_diary";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "YOUR_PASSWORD_HERE";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver was not found.");
            e.printStackTrace();
        }
    }

    // Creates and returns a new connection to the digital_diary database.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
