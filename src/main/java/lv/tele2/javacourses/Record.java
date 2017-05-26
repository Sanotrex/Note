package lv.tele2.javacourses;

import java.sql.*;

public abstract class Record implements Comparable<Record> {
    private int id;

    public Record(ResultSet rs) throws SQLException {
        id = rs.getInt("ID");
    }

    public Record() {
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM RECORD")) {

            if (rs.next()) {
                id = rs.getInt(1) + 1;
            } else {
                id = 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public boolean contains(String str) {
        return String.valueOf(id).contains(str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return id == record.id;
    }

    public abstract void insert() throws SQLException;

    public abstract void update() throws SQLException;

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(Record o) {
        return o.id - this.id;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                '}';
    }
}
