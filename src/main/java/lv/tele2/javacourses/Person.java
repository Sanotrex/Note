package lv.tele2.javacourses;

import asg.cliche.Command;
import asg.cliche.Param;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Person extends Record {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> phones;

    public Person() {

    }

    public Person(ResultSet rs) throws SQLException {
        super(rs);
        firstName = rs.getString("FIRST_NAME");
        lastName = rs.getString("LAST_NAME");
        email = rs.getString("EMAIL");
        phones = DB.executePreparedQuery("SELECT VALUE FROM PHONE WHERE RECORD_ID = ? ORDER BY IDX",
                stmt -> stmt.setInt(1, getId()),
                phoneRS -> {
                    List<String> result = new ArrayList<>();
                    while (phoneRS.next()) {
                        result.add(phoneRS.getString("VALUE"));
                    }
                    return result;
                });
    }

    public String getFirstName() {
        return firstName;
    }

    @Command(name = "first-name", abbrev = "fn", description = "changes first name")
    public void setFirstName(@Param(name = "first name") String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Command(name = "last-name", abbrev = "ln", description = "changes last name")
    public void setLastName(@Param(name = "last name") String lastName) {
        this.lastName = lastName;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    @Command(name = "phone", abbrev = "p", description = "adds new phone to the list")
    public void addPhone(@Param(name = "phone", description = "phone number") String phone) {
        phones.add(phone);
    }

    public String getEmail() {
        return email;
    }

    @Command(name = "email", abbrev = "e", description = "changes email")
    public void setEmail(@Param(name = "email") String email) {
        this.email = email;
    }

    @Command(name = "show", abbrev = "s", description = "displays record")
    @Override
    public String toString() {
        return "Person{" +
                "id='" + getId() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phones='" + phones + '\'' +
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
            for (String p : phones) {
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
        DB.executePreparedUpdate(
                "INSERT INTO RECORD (ID, REC_TYPE, FIRST_NAME, LAST_NAME, EMAIL) VALUES (?, ?, ?, ?, ?)",
                stmt -> {
                    stmt.setInt(1, getId());
                    stmt.setString(2, "person");
                    stmt.setString(3, firstName);
                    stmt.setString(4, lastName);
                    stmt.setString(5, email);
                });
        savePhones();
    }

    private void savePhones() throws SQLException {
        DB.executePreparedUpdate(
                "DELETE FROM PHONE WHERE RECORD_ID = ?",
                stmt -> stmt.setInt(1, getId()));
        if (phones == null || phones.isEmpty()) {
            return;
        }
        for (int i = 0; i < phones.size(); i++) {
            final int idx = i;
            DB.executePreparedUpdate(
                    "INSERT INTO PHONE (RECORD_ID, IDX, VALUE) VALUES (?, ?, ?)",
                    stmt -> {
                        stmt.setInt(1, getId());
                        stmt.setInt(2, idx);
                        stmt.setString(3, phones.get(idx));
                    });
        }

    }

    @Override
    public void update() throws SQLException {
        DB.executePreparedUpdate("UPDATE RECORD SET FIRST_NAME = ?, LAST_NAME = ? WHERE ID = ?",
                stmt -> {
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setInt(3, getId());
                });
        savePhones();
    }


}
