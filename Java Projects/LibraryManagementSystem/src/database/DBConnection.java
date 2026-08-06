package database;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL = System.getenv().getOrDefault(
            "LMS_DB_URL",
            "jdbc:mysql://localhost:3306/LibraryManagementDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String USER = System.getenv().getOrDefault("LMS_DB_USER", "root");
    private static final String PASSWORD = resolvePassword();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found. Place mysql-connector-j.jar on the classpath.");
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String resolvePassword() {
        String configuredPassword = System.getenv("LMS_DB_PASSWORD");
        if (configuredPassword != null) {
            return configuredPassword;
        }
        return "Bittu@726";
    }
}
