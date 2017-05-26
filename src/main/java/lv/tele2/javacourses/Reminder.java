package lv.tele2.javacourses;

import asg.cliche.Command;
import asg.cliche.Param;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reminder extends Note implements Expirable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private LocalDateTime time;
    private boolean dismissed;

    public Reminder() {

    }

    public Reminder(ResultSet rs) throws SQLException {
        super(rs);
        time = rs.getTimestamp("REMINDER_DATETIME").toLocalDateTime();
        dismissed = rs.getBoolean("REMINDER_DISMISSED");
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Command(name = "timestamp", abbrev = "ts", description = "change date and time of the reminder")
    public void setTime(@Param(name = "timestamp", description = "date and time in format dd.MM.yyyy HH:mm") String strTime) {
        this.time = LocalDateTime.parse(strTime, FORMAT);
    }

    @Override
    public boolean contains(String str) {
        if (super.contains(str)) {
            return true;
        }
        String lt = time.format(FORMAT).toLowerCase();
        return lt.contains(str);
    }

    @Command(name = "show", abbrev = "s", description = "displays record")
    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + getId() + '\'' +
                ", note='" + getNote() + '\'' +
                ", time='" + time.format(FORMAT) + '\'' +
                '}';
    }

    @Override
    public boolean isExpired() {
        if (dismissed) {
            return false;
        }
        LocalDateTime currentDate = LocalDateTime.now();
        return time.isBefore(currentDate);
    }

    @Command(name = "dismiss", abbrev = "d", description = "dismisses current reminder")
    @Override
    public void dismiss() {
        dismissed = true;
    }

    @Override
    public void insert() throws SQLException {
        DB.executePreparedUpdate("INSERT INTO RECORD (ID, REC_TYPE, NOTE, REMINDER_DATETIME, REMINDER_DISMISSED) VALUES (?, ?, ?, ?, ?)",
                stmt -> {
                    stmt.setInt(1, getId());
                    stmt.setString(2, "reminder");
                    stmt.setString(3, getNote());
                    stmt.setTimestamp(4, Timestamp.valueOf(time));
                    stmt.setBoolean(5, false);
                });
    }

    @Override
    public void update() throws SQLException {
        DB.executePreparedUpdate("UPDATE RECORD SET NOTE = ?, REMINDER_DATETIME = ?, REMINDER_DISMISSED = ? WHERE ID = ?",
                stmt -> {
                    stmt.setString(1, getNote());
                    stmt.setTimestamp(2, Timestamp.valueOf(time));
                    stmt.setBoolean(3, dismissed);
                    stmt.setInt(4, getId());
                });
    }


}
