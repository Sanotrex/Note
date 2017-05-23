package lv.tele2.javacourses;

public abstract class Record implements Comparable<Record> {
    private static int recordCount;
    private int id;

    public Record() {
        recordCount++;
        id = recordCount;
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

    @Override
    public int hashCode() {
        return id % 4;
    }

    @Override
    public int compareTo(Record o) {
        return o.id - this.id;
    }
}
