package lv.tele2.javacourses;

import java.sql.*;

public class DB {

    public static <T> T doConnection(DBFunction<Connection, T> processor) throws SQLException {
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb;create=true")) {
            createTablesIfNeeded(con);
            return processor.apply(con);
        }
    }

    public static int executeUpdate(String sql) throws SQLException {
        return doConnection(c -> {
            try (Statement stmt = c.createStatement()) {
                return stmt.executeUpdate(sql);
            }
        });
    }

    public static <T> T executeQuery(String sql,
                                     DBFunction<ResultSet, T> resultProcessor) throws SQLException {
        return doConnection(c -> {
            try (Statement stmt = c.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                return resultProcessor.apply(rs);
            }
        });
    }

    public static int executePreparedUpdate(String sql,
                                            DBConsumer<PreparedStatement> paramsProvider) throws SQLException {
        return doConnection(c -> {
            try (PreparedStatement stmt = c.prepareStatement(sql)) {
                paramsProvider.consume(stmt);
                return stmt.executeUpdate();
            }
        });
    }

    public static <T> T executePreparedQuery(String sql,
                                             DBConsumer<PreparedStatement> paramsProvider,
                                             DBFunction<ResultSet, T> resultProcessor) throws SQLException {
        return doConnection(c -> {
            try (PreparedStatement stmt = c.prepareStatement(sql)) {
                paramsProvider.consume(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultProcessor.apply(rs);
                }
            }
        });
    }

    private static void createTablesIfNeeded(Connection con) throws SQLException {
        // Checking all warnings.
        // If one of the warnings is "01J01" then db is already created, so table is already exist.
        SQLWarning warning = con.getWarnings();
        boolean needCreateTable = true;
        while (warning != null) {
            if ("01J01".equals(warning.getSQLState())) {
                needCreateTable = false;
                break;
            }
            warning = warning.getNextWarning();
        }
        con.clearWarnings();
        if (needCreateTable) {
            createTables(con);
        }
    }

    private static void createTables(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE RECORD (" +
                            "ID                   INT         NOT NULL PRIMARY KEY," +
                            "REC_TYPE             VARCHAR(25) NOT NULL," +
                            "FIRST_NAME           VARCHAR(200)," +
                            "LAST_NAME            VARCHAR(200)," +
                            "EMAIL                VARCHAR(200)," +
                            "NOTE                 VARCHAR(500)," +
                            "REMINDER_DATETIME    TIMESTAMP," +
                            "REMINDER_DISMISSED   BOOLEAN," +
                            "ALARM_TIME           TIME," +
                            "ALARM_DISMISSED_DATE DATE)");
            stmt.executeUpdate(
                    "CREATE TABLE PHONE (" +
                            "RECORD_ID            INT        NOT NULL REFERENCES RECORD (ID)," +
                            "IDX                  INT        NOT NULL," +
                            "VALUE                VARCHAR(25)," +
                            "PRIMARY KEY (RECORD_ID, IDX))");
        }
    }

    @FunctionalInterface
    public interface DBFunction<T extends AutoCloseable, R> {

        R apply(T ac) throws SQLException;

    }

    @FunctionalInterface
    public interface DBConsumer<T extends AutoCloseable> {

        void consume(T ac) throws SQLException;

    }
}
