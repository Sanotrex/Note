package lv.tele2.javacourses;


import asg.cliche.Command;
import asg.cliche.Param;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Note extends Record {
    private String note;

    public Note() {

    }

    public Note(ResultSet rs) throws SQLException {
        super(rs);
        note = rs.getString("NOTE");
    }

    public String getNote() {
        return note;
    }

    @Command(name = "note", abbrev = "n", description = "update note")
    public void setNote(@Param(name = "note", description = "text of note") String note) {
        this.note = note;
    }

    @Command(name = "show", abbrev = "s", description = "displays record")
    @Override
    public String toString() {
        return "Note{" +
                "id='" + getId() + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean contains(String str) {
        if (super.contains(str)) {
            return true;
        }
        String low = str.toLowerCase();
        String ln = note.toLowerCase();
        return ln.contains(low);
    }

    @Override
    public void insert() throws SQLException {
        DB.executePreparedUpdate("INSERT INTO RECORD (ID, REC_TYPE, NOTE) VALUES (?, ?, ?)",
                stmt -> {
                    stmt.setInt(1, getId());
                    stmt.setString(2, "note");
                    stmt.setString(3, note);
                });
    }

    @Override
    public void update() throws SQLException {
        DB.executePreparedUpdate("UPDATE RECORD SET NOTE = ? WHERE ID = ?",
                stmt -> {
                    stmt.setString(1, note);
                    stmt.setInt(2, getId());
                });
    }
}
