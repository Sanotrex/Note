package lv.tele2.javacourses;

import asg.cliche.Command;

import java.sql.*;
import java.util.ArrayList;

public class Person extends Record {
    private String firstName;
    private String lastName;
    private ArrayList<String> phone;

    public Person() {

    }

    public Person(ResultSet rs) throws SQLException {
        super(rs);
        firstName = rs.getString("FIRST_NAME");
        lastName = rs.getString("LAST_NAME");
    }

    public String getFirstName() {
        return firstName;
    }

    @Command
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Command
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + getId() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }


    @Override
    public boolean contains(String str) {
        if (super.contains(str)) {
            return true;
        }
        String low = str.toLowerCase();
        String fn = firstName.toLowerCase();
        String ln = lastName.toLowerCase();
        if (fn.contains(low)) {
            return true;
        } else if (ln.contains(low)) {
            return true;
        } else {
            for (String p : phone) {
                String lp = p.toLowerCase();
                if (lp.contains(low)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void insert() throws SQLException {
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb");
             PreparedStatement stmt = con.prepareStatement(
                     "INSERT INTO RECORD (ID, REC_TYPE, FIRST_NAME, LAST_NAME) " +
                             "VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, getId());
            stmt.setString(2, "person");
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);

            stmt.executeUpdate();
        }
    }

}
