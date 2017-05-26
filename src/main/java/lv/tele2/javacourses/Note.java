package lv.tele2.javacourses;


import asg.cliche.Command;

import java.sql.*;

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

    @Command
    public void setNote(String note) {
        this.note = note;
    }

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
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb");
             PreparedStatement stmt = con.prepareStatement(
                     "INSERT INTO RECORD (ID, REC_TYPE, NOTE) " +
                             "VALUES (?, ?, ?)")) {
            stmt.setInt(1, getId());
            stmt.setString(2, "note");
            stmt.setString(3, note);

            stmt.executeUpdate();
        }
    }
}
