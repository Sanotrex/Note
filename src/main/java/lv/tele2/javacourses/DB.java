package lv.tele2.javacourses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class DB {
    private static final String WARN_EXISTS = "01J01";

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:derby:notebookdb;create=true");
        SQLWarning warning = con.getWarnings();
        if (warning == null || !WARN_EXISTS.equals(warning.getSQLState())) {
            createTable(con);
        }
        return con;
    }

    private static void createTable(Connection con) {

    }


}
