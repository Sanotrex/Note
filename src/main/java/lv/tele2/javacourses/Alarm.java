package lv.tele2.javacourses;

import asg.cliche.Command;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Alarm extends Note implements Expirable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private LocalTime time;
    private LocalDate dismissedDate;

    public Alarm() {

    }

    public Alarm(ResultSet rs) throws SQLException {
        super(rs);
        time = rs.getTime("ALARM_TIME").toLocalTime();
        Date dd = rs.getDate("ALARM_DISIMSSED_DATE");
        if (dd != null) {
            dismissedDate = dd.toLocalDate();
        }
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Command
    public void setTime(String strTime) {
        this.time = LocalTime.parse(strTime, FORMAT);
    }

    @Override
    public boolean contains(String str) {
        if (super.contains(str)) {
            return true;
        }
        String lt = time.format(FORMAT).toLowerCase();
        return lt.contains(str);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + getId() + '\'' +
                ", note='" + getNote() + '\'' +
                ", time='" + time.format(FORMAT) + '\'' +
                '}';
    }

    @Override
    public boolean isExpired() {
        LocalDate currentDate = LocalDate.now();
        if (dismissedDate != null && currentDate.isEqual(dismissedDate)) {
            return false;
        }
        LocalTime currentTime = LocalTime.now();
        return time.isBefore(currentTime);
    }

    @Override
    public void dismiss() {
        dismissedDate = LocalDate.now();
    }

    @Override
    public void insert() throws SQLException {
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb");
             PreparedStatement stmt = con.prepareStatement(
                     "INSERT INTO RECORD (ID, REC_TYPE, NOTE, ALARM_TIME) " +
                             "VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, getId());
            stmt.setString(2, "alarm");
            stmt.setString(3, getNote());
            stmt.setTime(4, Time.valueOf(time));

            stmt.executeUpdate();
        }
    }

}
