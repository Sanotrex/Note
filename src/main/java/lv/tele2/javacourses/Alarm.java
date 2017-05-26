package lv.tele2.javacourses;

import asg.cliche.Command;
import asg.cliche.Param;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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
        Date dd = rs.getDate("ALARM_DISMISSED_DATE");
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

    @Command(name = "time", abbrev = "t", description = "change time of alarm")
    public void setTime(@Param(name = "time", description = "time in format HH:mm") String strTime) {
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

    @Command(name = "show", abbrev = "s", description = "displays record")
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

    @Command(name = "dismiss", abbrev = "d", description = "dismisses current alarm for today")
    @Override
    public void dismiss() {
        dismissedDate = LocalDate.now();
    }

    @Override
    public void insert() throws SQLException {
        DB.executePreparedUpdate("INSERT INTO RECORD (ID, REC_TYPE, NOTE, ALARM_TIME) VALUES (?, ?, ?, ?)",
                stmt -> {
                    stmt.setInt(1, getId());
                    stmt.setString(2, "alarm");
                    stmt.setString(3, getNote());
                    stmt.setTime(4, Time.valueOf(time));
                });
    }

    @Override
    public void update() throws SQLException {
        DB.executePreparedUpdate("UPDATE RECORD SET NOTE = ?, ALARM_TIME = ?, ALARM_DISMISSED_DATE = ? WHERE ID = ?",
                stmt -> {
                    stmt.setString(1, getNote());
                    stmt.setTime(2, Time.valueOf(time));
                    stmt.setDate(3, Date.valueOf(dismissedDate));
                    stmt.setInt(4, getId());
                });
    }

}
