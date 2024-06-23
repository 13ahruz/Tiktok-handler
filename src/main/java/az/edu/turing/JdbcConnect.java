package az.edu.turing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnect {
    final String url = "jdbc:postgresql://localhost:5433/postgres";
    final String user = "postgres";
    final String password = "postgres";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}