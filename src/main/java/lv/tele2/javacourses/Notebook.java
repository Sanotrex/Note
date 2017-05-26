package lv.tele2.javacourses;

import asg.cliche.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Notebook implements ShellDependent {
    private Shell parentShell;

    @Command(description = "lists all records")
    public Collection<Record> list() throws SQLException {
        return DB.executeQuery("SELECT * FROM RECORD ORDER BY ID",
                rs -> {
                    List<Record> result = new ArrayList<>();
                    while (rs.next()) {
                        Record r = RecordFactory.create(rs);
                        result.add(r);
                    }
                    return result;
                });
    }

    @Command(description = "lists all records of specified type")
    public Collection<Record> list(@Param(name = "type", description = "person, note, alarm or reminder") String type) throws SQLException {
        return DB.executePreparedQuery("SELECT * FROM RECORD WHERE REC_TYPE = ? ORDER BY ID",
                stmt -> stmt.setString(1, type),
                rs -> {
                    List<Record> result = new ArrayList<>();
                    while (rs.next()) {
                        Record r = RecordFactory.create(rs);
                        result.add(r);
                    }
                    return result;
                });
    }

    @Command(description = "creates new person")
    public Record createPerson(@Param(name = "first name") String firstName,
                               @Param(name = "last name") String lastName,
                               @Param(name = "email") String email,
                               @Param(name = "phones") String... phone) throws SQLException {
        Person result = new Person();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setPhones(new ArrayList<>(Arrays.asList(phone)));
        result.setEmail(email);
        result.insert();
        return result;
    }

    @Command(description = "creates new note")
    public Record createNote(@Param(name = "note") String note) throws SQLException {
        Note result = new Note();
        result.setNote(note);
        result.insert();
        return result;
    }

    @Command(description = "creates new reminder")
    public Record createReminder(@Param(name = "note") String note, @Param(name = "date/time") String time) throws SQLException {
        Reminder result = new Reminder();
        result.setNote(note);
        result.setTime(time);
        result.insert();
        return result;
    }

    @Command(description = "creates new alarm")
    public Record createAlarm(@Param(name = "note") String note, @Param(name = "time") String time) throws SQLException {
        Alarm result = new Alarm();
        result.setNote(note);
        result.setTime(time);
        result.insert();
        return result;
    }

    @Command(description = "edit record")
    public Record edit(@Param(name = "id") int id) throws IOException, SQLException {
        Record r = find(id);
        if (r != null) {
            Shell shell = ShellFactory.createSubshell("#" + id, parentShell, "Editing record #" + id, r);
            shell.commandLoop();
            r.update();
        }
        return r;
    }

    private Record find(int id) throws SQLException {
        return DB.executePreparedQuery("SELECT * FROM RECORD WHERE ID = ?",
                stmt -> stmt.setInt(1, id),
                rs -> {
                    if (rs.next()) {
                        return RecordFactory.create(rs);
                    } else {
                        return null;
                    }
                });
    }

    @Command(description = "finds records by substring")
    public List<Record> find(@Param(name = "substring") String str) throws SQLException {
        return list().stream()
                .filter(r -> r.contains(str))
                .collect(Collectors.toList());
    }

    @Command(description = "lists expired reminders or alarms")
    public List<Record> listExpired() throws SQLException {
        return list().stream()
                .filter(r -> r instanceof Expirable)
                .filter(r -> ((Expirable) r).isExpired())
                .collect(Collectors.toList());
    }

    @Command(description = "dismisses reminder or alarm")
    public void dismiss(@Param(name = "id") int id) throws SQLException {
        Record r = find(id);
        if (r != null && r instanceof Expirable) {
            Expirable e = (Expirable) r;
            e.dismiss();
            r.update();
        } else {
            System.out.println("this isn't an expirable");
        }
    }


    @Override
    public void cliSetShell(Shell theShell) {
        this.parentShell = theShell;
    }
}
