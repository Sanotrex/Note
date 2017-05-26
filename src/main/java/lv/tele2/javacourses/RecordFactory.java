package lv.tele2.javacourses;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordFactory {

    public static Record create(ResultSet rs) throws SQLException {
        String recType = rs.getString("REC_TYPE");
        Record record;
        switch (recType) {
            case "person":
                record = new Person(rs);
                break;
            case "note":
                record = new Note(rs);
                break;
            case "reminder":
                record = new Reminder(rs);
                break;
            case "alarm":
                record = new Alarm(rs);
                break;
            default:
                throw new IllegalArgumentException(recType + " is not supported");
        }
        return record;
    }
}
