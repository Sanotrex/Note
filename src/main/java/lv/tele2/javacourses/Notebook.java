package lv.tele2.javacourses;

import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import asg.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Notebook implements ShellDependent {
    private final Map<Integer, Record> records = new HashMap<>();
    private final NavigableMap<String, Record> personsByName = new TreeMap<>();
    private Shell parentShell;

    @Command
    public Record findPerson(String str) {
        return personsByName.ceilingEntry(str).getValue();
    }

    @Command
    public Collection<Record> list() throws SQLException {
        List<Record> result = new ArrayList<>();
        try (Connection con =
                     DriverManager.getConnection("jdbc:derby:notebookdb");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM RECORD")) {

            while (rs.next()) {
                String recType = rs.getString("REC_TYPE");
                Record rec = null;
                switch (recType) {
                    case "person":
                        rec = new Person(rs);
                        break;
                    case "note":
                        rec = new Note(rs);
                        break;
                    case "reminder":
                        rec = new Reminder(rs);
                        break;
                    case "alarm":
                        rec = new Alarm(rs);
                        break;
                    default:
                        System.out.println(recType + " unsupported");
                }
                result.add(rec);
            }

        }
        return result;
    }

    @Command
    public Collection<Record> listPersons() {
        return personsByName.values();
    }

    @Command
    public Record createPerson(String firstName, String lastName, String... phone) throws SQLException {
        Person result = new Person();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setPhone(new ArrayList<>(Arrays.asList(phone)));
        records.put(result.getId(), result);
        personsByName.put(firstName + " " + lastName, result);
        result.insert();
        return result;
    }

    @Command
    public Record createNote(String note) throws SQLException {
        Note result = new Note();
        result.setNote(note);
        records.put(result.getId(), result);
        result.insert();
        return result;
    }

    @Command
    public Record createReminder(String note, String time) throws SQLException {
        Reminder result = new Reminder();
        result.setNote(note);
        result.setTime(time);
        records.put(result.getId(), result);
        result.insert();
        return result;
    }

    @Command
    public Record createAlarm(String note, String time) throws SQLException {
        Alarm result = new Alarm();
        result.setNote(note);
        result.setTime(time);
        records.put(result.getId(), result);
        result.insert();
        return result;
    }

    @Command
    public Record edit(int id) throws IOException {
        Record r = find(id);
        if (r != null) {
            Shell shell = ShellFactory.createSubshell("#" + id, parentShell, "Editing record #" + id, r);
            shell.commandLoop();
        }
        return r;
    }

    private Record find(int id) {
        return records.get(id);
    }

    @Command
    public List<Record> find(String str) {
        return records.values().stream()
                .filter(r -> r.contains(str))
                .collect(Collectors.toList());
    }

    @Command
    public List<Record> listExpired() {
        return records.values().stream()
                .filter(r -> r instanceof Expirable)
                .filter(r -> ((Expirable) r).isExpired())
                .collect(Collectors.toList());
    }

    @Command
    public void dismiss(int id) {
        Record r = find(id);
        if (r != null && r instanceof Expirable) {
            Expirable e = (Expirable) r;
            e.dismiss();
        } else {
            System.out.println("this isn't an expirable");
        }
    }


    @Override
    public void cliSetShell(Shell theShell) {
        this.parentShell = theShell;
    }
}
